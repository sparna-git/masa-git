package fr.humanum.masa.federation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.jena.query.Query;
import org.apache.jena.query.Syntax;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.algebra.evaluation.federation.AbstractFederatedServiceResolver;
import org.eclipse.rdf4j.query.algebra.evaluation.federation.FederatedService;
import org.eclipse.rdf4j.query.algebra.evaluation.federation.FederatedServiceResolver;
import org.eclipse.rdf4j.query.resultio.QueryResultFormat;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultWriter;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLParser;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriterFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.federation.Federation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.humanum.masa.federation.source.FederationSource;
import fr.humanum.masa.federation.source.FederationSourceRdfSupplier;
import fr.humanum.masa.federation.source.FederationSparqlQueriesExample;

@Service
public class FederationBusinessServices {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private ExtConfigService extConfigService;

	
	

	@Inject
	public FederationBusinessServices(ExtConfigService extConfigService) {
		this.extConfigService = extConfigService;
	}
	
	public void getResultToXml(String queryString,Repository repository, OutputStream out) throws IOException {

		try (RepositoryConnection conn = repository.getConnection()) {
			SPARQLResultsXMLWriter sparqlWriter = new SPARQLResultsXMLWriter(out);
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			tupleQuery.evaluate(sparqlWriter);
			
		}
	}
	

	/**
	 * Créé un Repository avec les sources indiquées.
	 * 
	 * @param sources
	 * @return
	 */
	public Repository createFederationRepositoryFromSources(Set<FederationSource> sources){
		Federation federation=new Federation();
		
		//add repositories in a federation object
		log.debug("Création de la  féderation");
		log.debug("size sources : "+sources.size());
		for (FederationSource aSource : sources) {
			log.debug("Ajout de l'endpoint URL à la féderation : "+aSource.getEndpoint());
			RepositorySupplier rs=new RepositorySupplier(aSource.getEndpoint().stringValue());
			federation.addMember(rs.getRepository());
		}		
		
		// all repositories in the federation are distinct
		// federation.setDistinct(true);

		// our federation will be read-only for sure
		federation.setReadOnly(true);
		
		//create new repository with federation
		log.debug("Création et initialisation du repository");
		Repository repository=new SailRepository(federation);
		repository.initialize();	
		log.debug("Initialisation du repository avec la fédération terminée");
		
		return repository;
	}
	
	/**
	 * Renvoie toutes les sources paramétrées dans la fédération
	 * @return
	 */
	public Set<FederationSource> readAllFederationSources() {
		FederationSourceRdfSupplier frs=new FederationSourceRdfSupplier(extConfigService);
		Set<FederationSource> federationSources=frs.get();
		return federationSources;
	}
	
	/**
	 * Filtre les sources à utiliser pour créer une fédération en fonction des clauses
	 * FROM de la query.
	 * 
	 * @param q
	 * @return
	 */
	public Set<FederationSource> filterFederationSources(Query q) {
		Set<FederationSource> allSources = readAllFederationSources();
		// if query does not have FROM clauses, we return all the sources
		if(q.getGraphURIs() == null || q.getGraphURIs().isEmpty()){
			return allSources;
		} else {
			log.debug("Clause from trouvée");
			Set<FederationSource> filteredFederationSource = new HashSet<FederationSource>();
			List<String> sourcesList = q.getGraphURIs();
			for (String source : sourcesList) {
				allSources.forEach(fs->{
					if(fs.getSourceIri().equals(SimpleValueFactory.getInstance().createIRI(source))){
						log.debug("source found :"+ source);
						filteredFederationSource.add(fs);
					}
				});
			}
			return filteredFederationSource;
		}
	}
	
	public Query removeFromClauses(Query q) {
		Set<FederationSource> allSources = readAllFederationSources();
		List<String> querySources = q.getGraphURIs();
		List<String> sourcesToRemove = querySources.stream().filter(s -> { 
			for (FederationSource aSource : allSources) {
				if(aSource.getSourceIri().stringValue().equals(s)) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		
		for (String source : sourcesToRemove) {
			q.getGraphURIs().remove(source);
		}
		
		return q;
	}
	
	/**
	 * Renvoie les queries d'exemple paramétrées dans l'application.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<FederationSparqlQueriesExample> getExampleQueries() {
		File exampleQueryFile = this.extConfigService.getExampleQueriesFile();
		
		if(exampleQueryFile != null && exampleQueryFile.exists()){
			ObjectMapper objectMapper=new ObjectMapper();
			//add this line  
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);    
			try {
				return objectMapper.readValue(exampleQueryFile , new TypeReference<List<FederationSparqlQueriesExample>>(){});
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			log.warn("Exemple queries config file is not setup or does not exists. It value it : "+exampleQueryFile);
			return null;
		}
	}

}
