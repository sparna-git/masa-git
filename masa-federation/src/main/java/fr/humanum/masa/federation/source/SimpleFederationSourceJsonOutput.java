package fr.humanum.masa.federation.source;

import java.util.Map;


public class SimpleFederationSourceJsonOutput  {

	protected String sourceString;
	protected String endpoint;
	protected String defaultGraph;
	protected Map<String, String> labels;
	
	public static SimpleFederationSourceJsonOutput fromFederationSource(FederationSource originalSource) {
		SimpleFederationSourceJsonOutput fedJsonOut=new SimpleFederationSourceJsonOutput(
				originalSource.getSourceIri().toString(),
				originalSource.getEndpoint().toString(), 
				(originalSource.getDefaultGraph().isPresent())?originalSource.getDefaultGraph().get().stringValue():null,
				originalSource.getLabels()
		);
		return fedJsonOut;
	}
	
	public SimpleFederationSourceJsonOutput(String sourceString, String endpoint, String defaultGraph, Map<String, String> labels) {
		super();
		this.sourceString = sourceString;
		this.endpoint = endpoint;
		this.defaultGraph = defaultGraph;
		this.labels = labels;
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
