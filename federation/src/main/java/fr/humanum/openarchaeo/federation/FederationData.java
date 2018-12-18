package fr.humanum.openarchaeo.federation;

import java.util.List;

import fr.humanum.openarchaeo.federation.source.FederationSource;

public class FederationData {

	public static final String KEY=FederationData.class.getCanonicalName();
	
	protected List<QueryExample> queries;
	
	protected List<FederationSource> federationSources;
	
	
	

	public List<FederationSource> getFederationSources() {
		return federationSources;
	}

	public void setFederationSources(List<FederationSource> federationSources) {
		this.federationSources = federationSources;
	}

	public List<QueryExample> getQueries() {
		return queries;
	}

	public void setQueries(List<QueryExample> queries) {
		this.queries = queries;
	}
	
	
	
}
