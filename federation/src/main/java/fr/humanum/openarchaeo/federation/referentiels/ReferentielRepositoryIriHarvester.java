package fr.humanum.openarchaeo.federation.referentiels;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
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
		
		// List<IRI> actualGeonamesHttpIris = iris.stream().map(i -> toGeonamesHttpIri(i)).collect(Collectors.toList());
		List<IRI> graphIris = iris.stream().map(i -> generateGraphIri(i)).collect(Collectors.toList());
		
		GraphToUpdateReader handler = new GraphToUpdateReader();		
		try(RepositoryConnection c = this.referentialRepository.getConnection()) {
			log.debug("Fetching graphs that require an update...");
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_MONTH, 30);
			Date limitDate = cal.getTime();
			
			String fetchGraphsString = buildeFetchUnknownOrInvalidGraphsSparql(graphIris, limitDate);
			// log.debug("Using SPARQL :\n"+fetchGraphsString);

			Perform.on(c).query(fetchGraphsString, handler);			
		} 
		
		if(handler.graphIris.size() == 0) {
			log.debug("No data update needed - everything uptodate");
		} else {
			log.debug(handler.graphIris.size() +" needs an update");
			updateGraphs(handler.graphIris);			
			
		    synchronized (this) {
		        try {
		            this.wait(250);
		        } catch (Throwable e) {
		            e.printStackTrace();
		        }
		    }
			
		    List<IRI> targetIris = null;
		    try(RepositoryConnection c = this.referentialRepository.getConnection()) {
				// we may have loops in Geonames, so follow links only for updated graphs
				targetIris = this.followLinks(c, handler.graphIris.stream().map(iri -> decodeIri(iri)).collect(Collectors.toList()));
		    }
		    
			if(targetIris != null && targetIris.size() > 0) {
				// recurse on target IRIs
				this.harvest(targetIris);
			}
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
	
//	private IRI frantiqIri(IRI originalIri) {
//		return SimpleValueFactory.getInstance().createIRI(
//				(originalIri.toString().contains("ark.frantiq.fr"))?
//						"https://pactols.frantiq.fr/opentheso/webresources/rest/skos/ark:"+originalIri.toString().substring(originalIri.toString().indexOf("ark:")+4)
//						:originalIri.toString()
//		);
//	}

	public static IRI toGeonamesHttpIri(IRI originalIri) {
		return SimpleValueFactory.getInstance().createIRI(
				(originalIri.toString().startsWith("https://sws.geonames.org"))?
						"http://sws.geonames.org"+originalIri.toString().substring("https://sws.geonames.org".length())
						:originalIri.toString()
				);
	}
	
	public static IRI toGeonamesHttpsIri(IRI originalIri) {
		
		IRI result = SimpleValueFactory.getInstance().createIRI(
				(originalIri.toString().startsWith("http://sws.geonames.org"))?
						"https://sws.geonames.org"+originalIri.toString().substring("http://sws.geonames.org".length())+"/"
						:originalIri.toString()
				);
		return result;
	}
	
	private List<IRI> followLinks(RepositoryConnection c, List<IRI> startIris) {		
		if(this.followPath != null && !this.followPath.isEmpty()) {
			log.debug("Following path "+this.followPath+" on "+startIris.size()+" iris...");
			
			
//			List<IRI> targets = new ArrayList<IRI>();
//			String followSparql = this.buildFollowLinksSparql(startIris, this.followPath);
//			log.debug("Using SPARQL :\n"+followSparql);
//			Perform.on(c).query(followSparql, new AbstractTupleQueryResultHandler() {
//				@Override
//				public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
//					IRI target = (IRI)bindingSet.getValue("target");
//					targets.add(target);
//				}
//			});
			
			Set<IRI> targets = new HashSet<IRI>();
			final int BATCH_SIZE = 20;				
			List<IRI> remainingEntries = new ArrayList<>(startIris);
			while(remainingEntries.size() > BATCH_SIZE) {
				List<IRI> currentBatch = remainingEntries.subList(0, BATCH_SIZE);
				
				String followSparql = this.buildFollowLinksSparql(currentBatch, this.followPath);
				log.debug("Using SPARQL :\n"+followSparql);
				Perform.on(c).query(followSparql, new AbstractTupleQueryResultHandler() {
					@Override
					public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
						IRI target = (IRI)bindingSet.getValue("target");
						targets.add(target);
					}
				});

				remainingEntries.removeAll(currentBatch);
			}
			// process last batch
			if(remainingEntries.size() > 0) {
				String followSparql = this.buildFollowLinksSparql(remainingEntries, this.followPath);
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
			return new ArrayList<IRI>(targets);
		} else {
			return null;
		}
	}
	
	public String buildLoadIriSparql(String anIri, IRI graphIri) {
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
		sb.append("     FILTER(?created < \""+new SimpleDateFormat("yyyy-MM-dd").format(limitDate)+"\"^^xsd:date) "+"\n");
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
		sb.append("SELECT DISTINCT ?target"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  ?iri "+followPath+" ?target ."+"\n");
		sb.append("  VALUES ?iri {"+"\n");
		for (IRI iri : resourceIris) {
			// sb.append("    <"+toGeonamesHttpsIri(iri)+">"+"\n");
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
	
	public void updateGraphs(List<IRI> graphIris) {
		for (IRI graphIri : graphIris) {
			try(RepositoryConnection connection = this.referentialRepository.getConnection()) {
				IRI anIri = decodeIri(graphIri);
				
				log.debug("  Harvesting IRI "+anIri);
				
				try {				
					log.debug("Dropping graph IRI : '"+graphIri.toString()+"'...");
					String dropString = buildDropGraphSparql(graphIri);
					Update drop = connection.prepareUpdate(dropString);
					drop.execute();					
					
					// manually fetch IRI content
					String actualIriToFetch = anIri.toString();
					if(anIri.toString().contains("geonames.org")) {
						actualIriToFetch = anIri.toString()+(anIri.toString().endsWith("/")?"":"/")+"about.rdf";
					}
					
					try {
						log.debug("Loading IRI : '"+actualIriToFetch+"' manually, trusting response headers...");
						connection.add(new URL(actualIriToFetch), RDF.NAMESPACE, null, graphIri);
					} catch (Exception e) {
						try {
							RDFFormat format = Rio.getParserFormatForFileName(actualIriToFetch).orElse(RDFFormat.RDFXML);
							log.debug("Loading IRI : '"+actualIriToFetch+"' manually, forcing format to "+format.getName()+" based on file extension or default RDF/XML");
							connection.add(new URL(actualIriToFetch), RDF.NAMESPACE, format, graphIri);
						} catch (Exception e1) {
							log.debug("Loading IRI : '"+actualIriToFetch+"' using a SPARQL LOAD update to GraphDB");
							String loadString = buildLoadIriSparql(actualIriToFetch, graphIri);
							Update load = connection.prepareUpdate(loadString);
							load.execute();
						}
					}
					
					log.debug("Registering creation date and source iri...");
					SimpleValueFactory vf = SimpleValueFactory.getInstance();
					connection.add(vf.createStatement(
							graphIri,
							DCTERMS.CREATED,
							vf.createLiteral(new Date())
					)
					, graphIri);
					connection.add(vf.createStatement(
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
		}
	}
	
	class GraphToUpdateReader extends AbstractTupleQueryResultHandler {
		protected List<IRI> graphIris = new ArrayList<>();
		
		@Override
		public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
			IRI graphIri = (IRI)bindingSet.getValue("g");
			this.graphIris.add(graphIri);
		}	
	}
	
}
