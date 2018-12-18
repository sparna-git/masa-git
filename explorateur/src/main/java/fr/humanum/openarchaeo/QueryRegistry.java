package fr.humanum.openarchaeo;

import java.util.HashMap;
import java.util.Map;

public class QueryRegistry {

	
	protected Map<String, String> queriesById = new HashMap<String, String>();
	
	public QueryRegistry(Map<String, String> queriesById) {
		super();
		this.queriesById = queriesById;
	}

	public String getSPARQLQuery(String queryId) {
		return queriesById.get(queryId);
	}
	
}
