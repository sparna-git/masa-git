package fr.humanum.masa.openarchaeo.explorateur;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Represents the MASA federation, and encapsulates all the calls made to the Federation component.
 * @author thomas
 *
 */
@Service
public class FederationService {

	private Logger log= LoggerFactory.getLogger(this.getClass().getName());
	
	private ExtConfigService extConfigService;
	
	private String endpointUrl;
	private Repository federationRepository;
	
	@Inject
	public FederationService(ExtConfigService extConfigService) {
		super();
		this.extConfigService = extConfigService;
	}
	
	@PostConstruct
	public void init() {
		this.endpointUrl = extConfigService.getApplicationProperties().getProperty(ExtConfigService.FEDERATION_SERVICE_URL);
		this.federationRepository = new SPARQLRepository(this.endpointUrl);
		this.federationRepository.initialize();
	}
	
	public void query(String query, TupleQueryResultHandler handler) {			
		try(RepositoryConnection conn = federationRepository.getConnection()) {
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
			tupleQuery.evaluate(handler);
		}
	}
	
	public String getSources() throws ClientProtocolException, IOException {
		String federationApiSourcesUrl=extConfigService.getApplicationProperties().getProperty(ExtConfigService.FEDERATION_SERVICE_API_SOURCES);
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet req = new HttpGet(federationApiSourcesUrl);
		HttpResponse resp = client.execute(req);
		return EntityUtils.toString(resp.getEntity());
	}	
	
}
