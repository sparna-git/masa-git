package fr.humanum.openarchaeo.federation.referentiels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.humanum.openarchaeo.Perform;

public class ReferentielRepositoryIriHarvester implements IriHarvester {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	protected String referentielRepositoryUrl;
	private Repository referentialRepository;
	private String followPath;
	
	public ReferentielRepositoryIriHarvester(String referentielRepositoryUrl) {
		super();
		this.referentielRepositoryUrl = referentielRepositoryUrl;
		this.init();
	}
	
	public ReferentielRepositoryIriHarvester(String referentielRepositoryUrl, String followPath) {
		super();
		this.referentielRepositoryUrl = referentielRepositoryUrl;
		this.followPath = followPath;
		this.init();
	}



	private void init() {
		this.referentialRepository = new HTTPRepository(referentielRepositoryUrl);
		this.referentialRepository.initialize();
	}

	@Override
	public void harvest(List<IRI> iris) {
		log.debug("Harvesting "+iris.size()+" IRIs...");
		
		List<IRI> graphIris = iris.stream().map(i -> generateGraphIri(frantiqIri(i))).collect(Collectors.toList());
		
		try(RepositoryConnection c = this.referentialRepository.getConnection()) {
			log.debug("Fetching graphs that require an update...");
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_MONTH, 30);
			Date limitDate = cal.getTime();
			
			String fetchGraphsString = buildeFetchUnknownOrInvalidGraphsSparql(graphIris, limitDate);
			// log.debug("Using SPARQL :\n"+fetchGraphsString);
			
			class GraphUpdaterHandler extends AbstractTupleQueryResultHandler {
				
				protected int count = 0;
				
				@Override
				public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
					count++;
					IRI graphIri = (IRI)bindingSet.getValue("g");
					IRI anIri = decodeIri(graphIri);
					
					log.debug("  Harvesting IRI "+anIri);
					
					try {				
						log.debug("Dropping graph IRI : '"+graphIri.toString()+"'...");
						String dropString = buildDropGraphSparql(graphIri);
						Update drop = c.prepareUpdate(dropString);
						drop.execute();					
						
						log.debug("Loading IRI : '"+anIri.toString()+"'...");
						String loadString = buildLoadIriSparql(anIri, graphIri);
						Update load = c.prepareUpdate(loadString);
						load.execute();
						
						log.debug("Registering creation date and source iri...");
						SimpleValueFactory vf = SimpleValueFactory.getInstance();
						c.add(vf.createStatement(
								graphIri,
								DCTERMS.CREATED,
								vf.createLiteral(new Date())
						)
						, graphIri);
						c.add(vf.createStatement(
								graphIri,
								DCTERMS.SOURCE,
								anIri
						)
						, graphIri);
						
					} catch (Exception e) {
						log.error("Error in harvesting "+anIri+" (message is '"+e.getMessage()+"'). See full log for details");
						e.printStackTrace();
					}
				}				
				
			};
			
			GraphUpdaterHandler handler = new GraphUpdaterHandler();
			Perform.on(c).query(fetchGraphsString, handler);
			if(handler.count == 0) {
				log.debug("No data update needed - everything uptodate");
			} else {
				log.debug(handler.count+" IRIs were updated");
			}
		}
		
		List<IRI> targetIris = this.followLinks(iris);
		if(targetIris != null && targetIris.size() > 0) {
			// recurse on target IRIs
			this.harvest(targetIris);
		}
		
	}
	
	private IRI generateGraphIri(IRI iri) {
		try {
			return SimpleValueFactory.getInstance().createIRI("urn:graph?iri="+URLEncoder.encode(iri.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private IRI decodeIri(IRI graphIri) {
		try {
			return SimpleValueFactory.getInstance().createIRI(URLDecoder.decode(graphIri.toString().substring("urn:graph?iri=".length()), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private IRI frantiqIri(IRI originalIri) {
		return SimpleValueFactory.getInstance().createIRI(
				(originalIri.toString().contains("ark.frantiq.fr"))?
						"https://pactols.frantiq.fr/opentheso/webresources/rest/skos/ark:"+originalIri.toString().substring(originalIri.toString().indexOf("ark:")+4)
						:originalIri.toString()
		);
	}
	
	private List<IRI> followLinks(List<IRI> startIris) {		
		if(this.followPath != null && !this.followPath.isEmpty()) {
			log.debug("Following path "+this.followPath+" on "+startIris.size()+" iris...");
			List<IRI> targets = new ArrayList<IRI>();
			try(RepositoryConnection c = this.referentialRepository.getConnection()) {
				String followSparql = this.buildFollowLinksSparql(startIris, this.followPath);
				log.debug("Using SPARQL :\n"+followSparql);
				Perform.on(c).query(followSparql, new AbstractTupleQueryResultHandler() {
					@Override
					public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
						IRI target = (IRI)bindingSet.getValue("target");
						targets.add(target);
					}
				});
			}
			log.debug("Found "+targets.size()+" target IRIs");
			return targets;
		} else {
			return null;
		}
	}
	
	public String buildLoadIriSparql(IRI anIri, IRI graphIri) {
		StringBuffer sb = new StringBuffer();
		sb.append("LOAD <"+anIri+">"+"\n");
		sb.append("INTO GRAPH <"+graphIri+"> "+"\n");
		return sb.toString();
	}
	
	public String buildDropGraphSparql(IRI graphIri) {
		StringBuffer sb = new StringBuffer();
		sb.append("DROP GRAPH <"+graphIri+">"+"\n");
		return sb.toString();
	}
	
	/**
	 * TODO : split in batches
	 * @param iris
	 * @return
	 */
	public String buildeFetchUnknownOrInvalidGraphsSparql(List<IRI> graphIris, Date limitDate) {
		StringBuffer sb = new StringBuffer();
		sb.append("PREFIX dct: <http://purl.org/dc/terms/>"+"\n");
		sb.append("SELECT ?g"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append(" {"+"\n");
		sb.append(" {"+"\n");
		sb.append("   GRAPH ?g { "+"\n");
		sb.append("     ?g dct:created ?created . "+"\n");
		sb.append("     FILTER(?created < \""+new SimpleDateFormat("YYYY-MM-DD").format(limitDate)+"\"^^xsd:date) "+"\n");
		sb.append("   }"+"\n");
		sb.append("   VALUES ?g { "+String.join(" ", graphIris.stream().map(i -> "<"+i.stringValue()+">").collect(Collectors.toList()))+"}");
		sb.append(" } UNION {"+"\n");
		sb.append("   FILTER NOT EXISTS { GRAPH ?g { ?g dct:created ?created . } } "+"\n");
		sb.append("   VALUES ?g { "+String.join(" ", graphIris.stream().map(i -> "<"+i.stringValue()+">").collect(Collectors.toList()))+"}");
		sb.append(" }"+"\n");
		sb.append(" }"+"\n");

		sb.append("}"+"\n");
		return sb.toString();
	}
	
	public String buildFollowLinksSparql(List<IRI> resourceIris, String followPath) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ?target"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  ?iri "+followPath+" ?target ."+"\n");
		sb.append("  VALUES ?iri {"+"\n");
		for (IRI iri : resourceIris) {
			sb.append("    <"+iri+">"+"\n");
		}
		sb.append(" }"+"\n");
		sb.append("}"+"\n");
		return sb.toString();		
	}

	public String getFollowPath() {
		return followPath;
	}

	public void setFollowPath(String followPath) {
		this.followPath = followPath;
	}
	
}
