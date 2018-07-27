package fr.humanum.masa.federation;

import java.util.HashMap;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class RepositorySupplier {

	protected Repository repository;
	protected String sparqlServiceUrl; 
	
	
	
	public RepositorySupplier (String sparqlServiceUrl){
		super();
		this.sparqlServiceUrl = sparqlServiceUrl;
	}
	
	public Repository getRepository() {	
		if(repository == null) {
			this.repository = new SPARQLRepository(sparqlServiceUrl);
			((SPARQLRepository)this.repository).setAdditionalHttpHeaders(new HashMap<String, String>() {{ 
				put("Accept", "application/json,application/xml;q=0.9");
			}} );
			this.repository.initialize();
		}
		return repository;
	}

	public String getSparqlServiceUrl() {
		return sparqlServiceUrl;
	}
}
