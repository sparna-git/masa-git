package fr.humanum.masa.federation.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class SimpleFederationSourceJsonOutput  {

	protected String sourceString;
	protected String endpoint;
	protected String defaultGraph;
	protected Map<String, String> labels;
	
	
	
	public SimpleFederationSourceJsonOutput(String sourceString, String endpoint, String defaultGraph, Map<String, String> labels) {
		super();
		this.sourceString = sourceString;
		this.endpoint = endpoint;
		this.defaultGraph = defaultGraph;
		this.labels = labels;
	}
	
	/**
	 * Constructor with a single label without language
	 * @param endpointUrl
	 * @param defaultGraphUri
	 * @param noLangLabel
	 */
	public SimpleFederationSourceJsonOutput(String sourceString, String endpoint, String defaultGraph, String noLangLabel) {
		this(sourceString, endpoint, defaultGraph, (Map<String, String>)null);
		this.labels = new HashMap<String, String>();
		labels.put(null, noLangLabel);
	}

	
	public String getSourceString() {
		return this.sourceString;
	}

	
	public String getEndpoint() {
		return this.endpoint;
	}

	
	public String getDefaultGraph() {
		return this.defaultGraph;
	}

	
	public Map<String, String> getLabels() {
		return this.labels;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setDefaultGraph(String defaultGraph) {
		this.defaultGraph = defaultGraph;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

}
