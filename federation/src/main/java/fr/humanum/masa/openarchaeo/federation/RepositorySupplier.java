package fr.humanum.masa.openarchaeo.federation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class RepositorySupplier {

	protected String sparqlServiceUrl; 
	protected String defaultGraphUri;
	
	public RepositorySupplier (String sparqlServiceUrl, String defaultGraphUri){
		super();
		this.sparqlServiceUrl = sparqlServiceUrl;
		this.defaultGraphUri = defaultGraphUri;
	}
	
	public Repository getRepository() {	
		String endpoint = sparqlServiceUrl;
		// add default graph URI parameter if necessary
		if(this.defaultGraphUri != null) {
			try {
				endpoint += "?default-graph-uri="+URLEncoder.encode(this.defaultGraphUri, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		Repository repository = new SPARQLRepository(endpoint);
		
		((SPARQLRepository)repository).setAdditionalHttpHeaders(new HashMap<String, String>() {{ 
			put("Accept", "application/json,application/xml;q=0.9");
		}} );
		
		repository.initialize();
		return repository;
	}

	public String getSparqlServiceUrl() {
		return sparqlServiceUrl;
	}
}
