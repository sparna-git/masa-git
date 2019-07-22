package fr.humanum.openarchaeo.federation.repository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import fr.humanum.openarchaeo.federation.source.FederationSource;

public class RepositorySupplier {

	protected FederationSource source;
	
	public RepositorySupplier (FederationSource source){
		super();
		this.source = source;
	}
	
	public static String constructEndpointUrl(FederationSource source) {
		String endpoint = source.getEndpoint().stringValue();
		// add default graph URI parameter if necessary
		if(source.getDefaultGraph().isPresent()) {
			try {
				endpoint += "?default-graph-uri="+URLEncoder.encode(source.getDefaultGraph().get().stringValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return endpoint;
	}
	
	public Repository getRepository() {	
		String endpoint = constructEndpointUrl(this.source);
		Repository repository = new SPARQLRepository(endpoint);
		
		((SPARQLRepository)repository).setAdditionalHttpHeaders(new HashMap<String, String>() {{ 
			put("Accept", "application/json,application/xml;q=0.9");
		}} );
		
		repository.initialize();
		return repository;
	}

}
