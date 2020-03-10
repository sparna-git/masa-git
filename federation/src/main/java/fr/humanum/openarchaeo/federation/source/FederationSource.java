package fr.humanum.openarchaeo.federation.source;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

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

	
	public static boolean areQueryingSameEndpoint(List<? extends FederationSource> sources) {
		return (sources.stream().map(s -> s.getEndpoint().stringValue()).collect(Collectors.toSet()).size() == 1);
	}
}
