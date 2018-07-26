package fr.humanum.masa.federation;

import java.util.List;
import java.util.Set;

import fr.humanum.masa.federation.source.FederationSource;
import fr.humanum.masa.federation.source.FederationSparqlQueriesExample;

public class FederationData {

	public static final String KEY=FederationData.class.getCanonicalName();
	
	protected List<FederationSparqlQueriesExample> queries;
	
	protected Set<FederationSource> federationSources;
	
	
	

	public Set<FederationSource> getFederationSources() {
		return federationSources;
	}

	public void setFederationSources(Set<FederationSource> federationSources) {
		this.federationSources = federationSources;
	}

	public List<FederationSparqlQueriesExample> getQueries() {
		return queries;
	}

	public void setQueries(List<FederationSparqlQueriesExample> queries) {
		this.queries = queries;
	}
	
	
	
}
