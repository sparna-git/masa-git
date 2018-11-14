package fr.humanum.masa.openarchaeo.explorateur;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryExample {

	@JsonProperty
	protected String title;
	
	@JsonProperty
	protected String[] query;
	
	public String getSparqlQuery() {
		return String.join("\\n",query);
	}
	
	public String getSparqlQueryForJavascript() {
		return String.join("\\n",query).replace("\"", "&quot;").replace("'", "\\'").replace("<", "&lt;").replace(">", "&gt;");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String [] getQuery() {
		return query;
	}

	public void setQuery(String [] query) {
		this.query = query;
	}
	
}
