package fr.humanum.masa.federation.source;

import java.util.Map;

/**
 * Represents a data source within a federation
 * @author thomas
 *
 */
public interface FederationSource {

	public String getEndpointUrl();
	
	public String getDefaultGraphUri();
	
	public Map<String, String> getLabels();
	
}
