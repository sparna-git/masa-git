package fr.humanum.masa.federation.source;

import java.util.Map;
import java.util.Optional;

import org.eclipse.rdf4j.model.IRI;

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
	 * Multilingual labels of the source
	 * @return
	 */
	public Map<String, String> getLabels();
	
}
