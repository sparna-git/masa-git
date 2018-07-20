package fr.humanum.masa.federation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.federation.Federation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.humanum.masa.federation.source.FederationSource;

@Service
public class FederationService {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
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
	
	public void initializeRepositoryWithFederation(Set<FederationSource> sources){
		
		if(federation==null){
			this.federation=new Federation();
			
			//add repositories in a federation object
			log.debug("Création de la  féderation");
			for (FederationSource aSource : sources) {
				log.debug("Ajout de l' endpoint URL à la féderation : "+aSource.getEndpointUrl());
				RepositorySupplier rs=new RepositorySupplier(aSource.getEndpointUrl());
				federation.addMember(rs.getRepository());
			}

			//create new repository with federation
			log.debug("Création du  repository");
			this.repository=new SailRepository(federation);
			log.debug("Initialisation du repository");
			this.repository.initialize();	
			log.debug("Initialisation du repository avec la fédération terminée");
		}


	}
	
	public Repository getRepository(){
		return repository;
	}

}
