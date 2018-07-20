package fr.humanum.masa.federation.source;

import java.util.HashMap;
import java.util.Map;

public class SimpleFederationSource implements FederationSource {

	protected String endpointUrl;
	protected String defaultGraphUri;
	protected Map<String, String> labels;
	
	
	
	public SimpleFederationSource(String endpointUrl, String defaultGraphUri, Map<String, String> labels) {
		super();
		this.endpointUrl = endpointUrl;
		this.defaultGraphUri = defaultGraphUri;
		this.labels = labels;
	}
	
	/**
	 * Constructor with a single label without language
	 * @param endpointUrl
	 * @param defaultGraphUri
	 * @param noLangLabel
	 */
	public SimpleFederationSource(String endpointUrl, String defaultGraphUri, String noLangLabel) {
		super();
		this.endpointUrl = endpointUrl;
		this.defaultGraphUri = defaultGraphUri;
		this.labels = new HashMap<String, String>();
		labels.put(null, noLangLabel);
	}

	@Override
	public String getEndpointUrl() {
		return this.endpointUrl;
	}

	@Override
	public String getDefaultGraphUri() {
		return this.defaultGraphUri;
	}

	@Override
	public Map<String, String> getLabels() {
		return this.labels;
	}

	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	public void setDefaultGraphUri(String defaultGraphUri) {
		this.defaultGraphUri = defaultGraphUri;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

}
