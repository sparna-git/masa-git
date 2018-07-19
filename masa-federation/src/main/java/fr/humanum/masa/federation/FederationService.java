package fr.humanum.masa.federation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.inject.Inject;

import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.federation.Federation;
import org.springframework.stereotype.Service;

@Service
public class FederationService {

	private Federation federation;
	private Repository repository;
	private ExtConfigService extConfigService;


	@Inject
	public FederationService(ExtConfigService extConfigService) {
		this.extConfigService=extConfigService;
	}
	
	public void getResultToXml(String queryString,Repository repository, OutputStream out) throws IOException {

		try (RepositoryConnection conn = repository.getConnection()) {
			
			SPARQLResultsXMLWriter sparqlWriter = new SPARQLResultsXMLWriter(out);
			
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			tupleQuery.evaluate(sparqlWriter);

		}
		
	}
	
	public Properties getProperties(String fileName) throws FileNotFoundException, IOException{
		return extConfigService.getProperties(fileName);
	}
	
	public void initializeRepositoryWithFederation(String endpointUrl){
		
		if(federation==null){
			this.federation=new Federation();
		}
		//get end point url property and foreach we create a repository and save it in a federation object
		System.out.println("Init repository with federation Get endpoint url...");
		String [] listEndPointUrl=endpointUrl.trim().split("[,]");

		//add repositories in a federation object
		System.out.println("Create federation");
		for (String url : listEndPointUrl) {
			RepositorySupplier rs=new RepositorySupplier(url);
			federation.addMember(rs.getRepository());
		}
		System.out.println("Initialize federation");
		federation.initialize();

		//create new repository with federation
		System.out.println("Create repository");
		this.repository=new SailRepository(federation);
		System.out.println("Initialize repository");
		this.repository.initialize();	
		System.out.println("End Initialization of repository with federation");
	}
	
	public Repository getRepository(){
		return repository;
	}

}
