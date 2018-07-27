package fr.humanum.masa.federation.source;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FederationSparqlQueriesExample {

	@JsonProperty
	protected String title;
	
	@JsonProperty
	protected String [] query;
	
	
	protected String sprqlQuery;
	

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

	public String getSprqlQuery() {
		return String.join("\\n",query);
	}

	
	
	

	
}
