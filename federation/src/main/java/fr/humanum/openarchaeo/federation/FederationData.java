package fr.humanum.openarchaeo.federation;

import java.util.List;

import fr.humanum.openarchaeo.federation.source.FederationSource;

public class FederationData {

	public static final String KEY=FederationData.class.getCanonicalName();
	
	protected List<QueryExample> queries;
	
	protected List<? extends FederationSource> federationSources;
	
	
	

	public List<? extends FederationSource> getFederationSources() {
		return federationSources;
	}

	public void setFederationSources(List<? extends FederationSource> federationSources) {
		this.federationSources = federationSources;
	}

	public List<QueryExample> getQueries() {
		return queries;
	}

	public void setQueries(List<QueryExample> queries) {
		this.queries = queries;
	}
	
	
	
}
