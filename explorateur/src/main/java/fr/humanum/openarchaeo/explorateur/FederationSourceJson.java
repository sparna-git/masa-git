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
		List<String> titles = getValues("dcterms:"+DCTERMS.TITLE.getLocalName(), lang);
		if(titles != null && titles.size() > 0) {
			return titles.get(0);
		} else {
			return null;
		}
	}
	
	public String getShortDesc(String lang) {
		List<String> abstracts = getValues("dcterms:"+DCTERMS.DESCRIPTION.getLocalName(), lang);
		if(abstracts != null && abstracts.size() > 0) {
			return abstracts.get(0);
		} else {
			return null;
		}
	}
	
	public List<String> getSpatial(String lang) {
		return getValues("dcterms:"+DCTERMS.SPATIAL.getLocalName(), lang);
	}
	
	public List<String> getKeywords(String lang) {
		return getValues("dcat:keyword", lang);
	}
	
	public String getStartDate(String lang) {
		List<String> values = getValues("schema:startDate", lang);
		if(values != null && values.size() > 0) {
			return values.get(0);
		} else {
			return null;
		}
	}
	
	public String getEndDate(String lang) {
		List<String> values = getValues("schema:endDate", lang);
		if(values != null && values.size() > 0) {
			return values.get(0);
		} else {
			return null;
		}
	}
	
	public Map<String, List<String>> getDescriptionInLang(String lang) {
		TreeMap<String, List<String>> result = new TreeMap<String, List<String>>();
		for (Map.Entry<String, List<LanguageValue>> e : this.description.entrySet()) {
			if(
					!e.getKey().equals("dcterms:"+DCTERMS.TITLE.getLocalName())
					&&
					!e.getKey().equals("dcterms:"+DCTERMS.DESCRIPTION.getLocalName())
					&&
					!e.getKey().equals("dcterms:"+DCTERMS.SPATIAL.getLocalName())
					&&
					!e.getKey().equals("dcat:keyword")
					&&
					!e.getKey().equals("dcterms:"+DCTERMS.CONFORMS_TO.getLocalName())
					&&
					!e.getKey().equals("dcat:theme")
					&&
					!e.getKey().equals("schema:startDate")
					&&
					!e.getKey().equals("schema:endDate")
			) {
				List<String> lValues = getValues(e.getKey(), lang);
				if(lValues != null && lValues.size() > 0) {
					result.put(e.getKey(), lValues);
				}
			}
		}
		return result;
	}
	
	public String displayValueList(List<String> values) {
		return values.stream().map(s -> {
			if(s.startsWith("http")) {
				return "<a href=\""+s+"\">"+s+"</a>";
			} else if(s.startsWith("mailto:")) {
				return "<a href=\""+s+"\">"+s.substring("mailto:".length())+"</a>";
			} else {
				return s;
			}
		}).collect(Collectors.joining(", "));
	}
	
	
	private List<String> getValues(String key, String lang) {
		List<LanguageValue> values = this.description.get(key);
		if(values == null) {
			return null;
		} else {
			return values.stream()
					.filter(v -> 
						(v.getLanguage() == null || v.getLanguage().equals(""))
						||
						(v.getLanguage() != null && v.getLanguage().equals(lang))
					)
					.map(v -> v.getValue())
					.collect(Collectors.toList());
		}
	}
	
	

}
