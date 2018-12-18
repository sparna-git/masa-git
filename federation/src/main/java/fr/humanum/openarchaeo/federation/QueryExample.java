package fr.humanum.openarchaeo.federation;

import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryExample {

	@JsonProperty
	protected Map<String, String> titles;
	
	@JsonProperty
	protected String[] query;
	
	public String getSparqlQuery() {
		return String.join("\\n",query);
	}
	
	public String getSparqlQueryForJavascript() {
		return String.join("\\n",query).replace("\"", "&quot;").replace("'", "\\'").replace("<", "&lt;").replace(">", "&gt;");
	}

	public Map<String, String> getTitles() {
		return titles;
	}

	public void setTitles(Map<String, String> titles) {
		this.titles = titles;
	}

	public String [] getQuery() {
		return query;
	}

	public void setQuery(String [] query) {
		this.query = query;
	}

	@Override
	public String toString() {
		return "QueryExample [titles=" + titles + ", query=" + Arrays.toString(query) + "]";
	}
	
}
