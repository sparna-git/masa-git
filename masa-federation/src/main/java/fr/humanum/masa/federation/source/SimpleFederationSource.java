package fr.humanum.masa.federation.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.rdf4j.model.IRI;

import com.fasterxml.jackson.annotation.JsonIgnoreType;


public class SimpleFederationSource implements FederationSource {

	protected IRI sourceIri;
	protected IRI endpoint;
	protected IRI defaultGraph;
	protected Map<String, String> labels;
	
	
	
	public SimpleFederationSource(IRI sourceIri, IRI endpoint, IRI defaultGraph, Map<String, String> labels) {
		super();
		this.sourceIri = sourceIri;
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
	public SimpleFederationSource(IRI sourceIri, IRI endpoint, IRI defaultGraph, String noLangLabel) {
		this(sourceIri, endpoint, defaultGraph, (Map<String, String>)null);
		this.labels = new HashMap<String, String>();
		labels.put(null, noLangLabel);
	}

	@Override
	public IRI getSourceIri() {
		return this.sourceIri;
	}

	@Override
	public IRI getEndpoint() {
		return this.endpoint;
	}

	@Override
	public Optional<IRI> getDefaultGraph() {
		return Optional.ofNullable(this.defaultGraph);
	}

	@Override
	public Map<String, String> getLabels() {
		return this.labels;
	}

	public void setEndpoint(IRI endpoint) {
		this.endpoint = endpoint;
	}

	public void setDefaultGraph(IRI defaultGraph) {
		this.defaultGraph = defaultGraph;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

}
