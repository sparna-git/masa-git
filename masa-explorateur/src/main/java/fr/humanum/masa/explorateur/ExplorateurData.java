package fr.humanum.masa.explorateur;

public class ExplorateurData {

	public String KEY= this.getClass().getCanonicalName();
	
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
