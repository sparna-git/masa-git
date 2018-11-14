package fr.humanum.masa.openarchaeo.federation.api;

import fr.humanum.masa.openarchaeo.federation.index.SearchResult;

/**
 * A result of a search in the index.
 * 
 * @author thomas
 *
 */
public class SearchResultJson {

	protected String uri;
	protected String label;
	protected String prefLabel;
	
	public static SearchResultJson fromSearchResult(SearchResult sr) {
		return new SearchResultJson(sr.getIri().stringValue(), sr.getLabel(), sr.getPrefLabel());
	}
	
	
	public SearchResultJson(String uri, String label, String prefLabel) {
		super();
		this.uri = uri;
		this.label = label;
		this.prefLabel = prefLabel;
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getPrefLabel() {
		return prefLabel;
	}	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}
	
	
}
