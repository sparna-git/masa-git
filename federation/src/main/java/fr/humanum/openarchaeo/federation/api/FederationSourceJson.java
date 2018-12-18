package fr.humanum.openarchaeo.federation.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

import fr.humanum.openarchaeo.federation.source.FederationSource;


public class FederationSourceJson  {

	protected String sourceString;
	protected String endpoint;
	protected String defaultGraph;
	
	protected Map<String, List<LanguageValue>> description;
	
	public static FederationSourceJson fromFederationSource(FederationSource originalSource) {
		// translate DCTerms
		Map<String, List<LanguageValue>> description = new HashMap<>();
		for (Map.Entry<IRI, List<Literal>> entry : originalSource.getDcterms().entrySet()) {
			description.put(
					entry.getKey().stringValue().substring(entry.getKey().stringValue().lastIndexOf('/')+1),
					entry.getValue().stream().map(l -> new LanguageValue(l.getLabel(), l.getLanguage().orElse(null))).collect(Collectors.toList())
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
	
	public FederationSourceJson(String sourceString, String endpoint, String defaultGraph, Map<String, List<LanguageValue>> description) {
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
	
	static class LanguageValue {
		private String language;
		private String value;
		
		public LanguageValue(String value, String language) {
			super();
			this.language = language;
			this.value = value;
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
	}

	public Map<String, List<LanguageValue>> getDescription() {
		return description;
	}

	public void setDescription(Map<String, List<LanguageValue>> description) {
		this.description = description;
	}
	
	

}
