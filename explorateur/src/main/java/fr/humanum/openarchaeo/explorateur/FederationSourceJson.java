package fr.humanum.openarchaeo.explorateur;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.vocabulary.DCTERMS;


public class FederationSourceJson  {

	protected String sourceString;
	protected String endpoint;
	protected String defaultGraph;
	
	protected Map<String, List<LanguageValue>> description;
	
	
	
	public FederationSourceJson() {
		super();
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
		
		public LanguageValue() {
			super();
		}
		
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
	
	public String getTitle(String lang) {
		List<String> titles = getDctermsValues(DCTERMS.TITLE.getLocalName(), lang);
		if(titles != null && titles.size() > 0) {
			return titles.get(0);
		} else {
			return null;
		}
	}
	
	public String getAbstract(String lang) {
		List<String> abstracts = getDctermsValues(DCTERMS.ABSTRACT.getLocalName(), lang);
		if(abstracts != null && abstracts.size() > 0) {
			return abstracts.get(0);
		} else {
			return null;
		}
	}
	
	public Map<String, String> getDescriptionInLang(String lang) {
		TreeMap<String, String> result = new TreeMap<String, String>();
		for (Map.Entry<String, List<LanguageValue>> e : this.description.entrySet()) {
			if(!e.getKey().equals(DCTERMS.TITLE.getLocalName()) && !e.getKey().equals(DCTERMS.ABSTRACT.getLocalName())) {
				List<String> lValues = getDctermsValues(e.getKey(), lang);
				if(lValues != null && lValues.size() > 0) {
					result.put(e.getKey(), lValues.get(0));
				}
			}
		}
		return result;
	}
	
	
	
	private List<String> getDctermsValues(String dcProperty, String lang) {
		List<LanguageValue> values = this.description.get(dcProperty);
		if(values == null) {
			return null;
		} else {
			return values.stream().filter(v -> v.getLanguage() != null && v.getLanguage().equals(lang)).map(v -> v.getValue()).collect(Collectors.toList());
		}
	}
	
	

}
