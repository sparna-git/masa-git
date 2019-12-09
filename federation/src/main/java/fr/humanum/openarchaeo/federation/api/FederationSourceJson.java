package fr.humanum.openarchaeo.federation.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.humanum.openarchaeo.federation.source.FederationSource;


public class FederationSourceJson  {

	protected String sourceString;
	protected String endpoint;
	protected String defaultGraph;
	
	protected Map<String, List<LiteralOrResourceValue>> description;
	
	public static FederationSourceJson fromFederationSource(FederationSource originalSource) {
		// translate DCTerms
		Map<String, List<LiteralOrResourceValue>> description = new HashMap<>();
		for (Map.Entry<IRI, List<Value>> entry : originalSource.getDcterms().entrySet()) {
			description.put(
					buildKey(entry.getKey()),
					entry.getValue().stream()
					.map(l -> {
						if(l instanceof Literal) {
							return new LiteralOrResourceValue(
							l.stringValue(),
							((Literal)l).getLanguage().orElse(null),
							((Literal)l).getDatatype().toString());
						} else {
							return new LiteralOrResourceValue(l.stringValue());
						}
					}
							
					)
					.collect(Collectors.toList())
			);
		}
		
		FederationSourceJson fedJsonOut=new FederationSourceJson(
				originalSource.getSourceIri().toString(),
				originalSource.getEndpoint().toString(), 
				(originalSource.getDefaultGraph().isPresent())?originalSource.getDefaultGraph().get().stringValue():null,
				description
		);
		return fedJsonOut;
	}
	
	public static String buildKey(IRI propertyIri) {
		if(propertyIri.stringValue().startsWith(DCTERMS.NAMESPACE)) {
			return "dcterms:"+propertyIri.stringValue().substring(propertyIri.stringValue().lastIndexOf('/')+1);
		} else if(propertyIri.stringValue().startsWith(DCAT.NAMESPACE)) {
			return "dcat:"+propertyIri.stringValue().substring(propertyIri.stringValue().lastIndexOf('#')+1);
		} else if(propertyIri.stringValue().startsWith("http://schema.org/")) {
			return "schema:"+propertyIri.stringValue().substring(propertyIri.stringValue().lastIndexOf('/')+1);
		} else {
			return propertyIri.stringValue();
		}
	}
	
	public FederationSourceJson(String sourceString, String endpoint, String defaultGraph, Map<String, List<LiteralOrResourceValue>> description) {
		super();
		this.sourceString = sourceString;
		this.endpoint = endpoint;
		this.defaultGraph = defaultGraph;
		this.description = description;
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

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setDefaultGraph(String defaultGraph) {
		this.defaultGraph = defaultGraph;
	}
	
	static class LiteralOrResourceValue {
		@JsonProperty("@language")
		private String language;
		@JsonProperty("@value")
		private String value;
		@JsonProperty("@type")
		private String datatype;
		@JsonProperty("@id")
		private String id;
		
		public LiteralOrResourceValue(String value, String language, String datatype) {
			super();
			this.language = language;
			this.value = value;
			this.datatype = datatype;
		}
		
		public LiteralOrResourceValue(String id) {
			super();
			this.id = id;
		}
		
		
		public String getLanguage() {
			return language;
		}
		public void setLanguage(String language) {
			this.language = language;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getDatatype() {
			return datatype;
		}
		public void setDatatype(String datatype) {
			this.datatype = datatype;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}

	public Map<String, List<LiteralOrResourceValue>> getDescription() {
		return description;
	}

	public void setDescription(Map<String, List<LiteralOrResourceValue>> description) {
		this.description = description;
	}
	
	

}
