package fr.humanum.openarchaeo.federation.referentiels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerBase;
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
	
	public ReferentielRepositoryIriHarvester(String referentielRepositoryUrl) {
		super();
		this.referentielRepositoryUrl = referentielRepositoryUrl;
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
			cal.add(Calendar.DAY_OF_MONTH, 30);;
			Date limitDate = cal.getTime();
			
			String fetchGraphsString = buildeFetchUnknownOrInvalidGraphsSparql(graphIris, limitDate);
			Perform.on(c).query(fetchGraphsString, new AbstractTupleQueryResultHandler() {

				@Override
				public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
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
				
			});
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
		sb.append(" } UNION {"+"\n");
		sb.append("   FILTER NOT EXISTS { GRAPH ?g { ?g dct:created ?created . } } "+"\n");
		sb.append(" }"+"\n");
		sb.append(" }"+"\n");
		sb.append(" VALUES ?g {"+"\n");
		for (IRI graphIri : graphIris) {
			sb.append("    <"+graphIri+">"+"\n");
		}
		sb.append(" }"+"\n");
		sb.append("}"+"\n");
		return sb.toString();
	}
	
}
