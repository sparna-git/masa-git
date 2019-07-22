package fr.humanum.openarchaeo.federation.source;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

/**
 * Represents a data source within a federation
 * @author thomas
 *
 */
public interface FederationSource {

	/**
	 * URI of the source that can be used to query the source in a FROM clause
	 * @return
	 */
	public IRI getSourceIri();
	
	/**
	 * Reference to the remote endpoint to be queried
	 * @return
	 */
	public IRI getEndpoint();
	
	/**
	 * Optional default graph of the endpoint to be queried
	 * @return
	 */
	public Optional<IRI> getDefaultGraph();
	
	/**
	 * DCTerms description of the source
	 * @return
	 */
	public Map<IRI, List<Value>> getDcterms();
	
	public static String constructEndpointUrl(FederationSource source) {
		String endpoint = source.getEndpoint().stringValue();
		// add default graph URI parameter if necessary
		if(source.getDefaultGraph().isPresent()) {
//			try {
//				endpoint += "?default-graph-uri="+URLEncoder.encode(source.getDefaultGraph().get().stringValue(), "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				throw new RuntimeException(e);
//			}
			endpoint += "?default-graph-uri="+source.getDefaultGraph().get().stringValue();
		}
		return endpoint;
	}
	
	/**
	 * Créé un Repository pour la source indiquée
	 * 
	 * @param sources
	 * @return
	 */
	public static Repository createFederationRepositoryFromSource(String endpoint){
		Repository repository = new SPARQLRepository(endpoint);
		
		((SPARQLRepository)repository).setAdditionalHttpHeaders(new HashMap<String, String>() {{ 
			put("Accept", "application/json,application/xml;q=0.9");
		}} );
		
		repository.initialize();
		return repository;
	}
	
}
