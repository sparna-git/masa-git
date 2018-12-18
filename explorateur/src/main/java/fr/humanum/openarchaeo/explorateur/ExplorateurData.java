package fr.humanum.openarchaeo.explorateur;

public class ExplorateurData {

	public static final String KEY= ExplorateurData.class.getCanonicalName();
	
	protected String expandQuery;
	
	protected String query;
	


	public String getExpandQuery() {
		return expandQuery;
	}

	public void setExpandQuery(String expandQuery) {
		this.expandQuery = expandQuery;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	
	
	
}
