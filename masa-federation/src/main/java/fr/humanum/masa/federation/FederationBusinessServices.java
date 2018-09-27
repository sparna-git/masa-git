package fr.humanum.masa.federation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.jena.query.Query;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
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

import fr.humanum.masa.Perform;
import fr.humanum.masa.federation.index.DomainPathRangeIriFetcher;
import fr.humanum.masa.federation.index.IndexService;
import fr.humanum.masa.federation.index.LuceneDocumentBuilder;
import fr.humanum.masa.federation.index.RdfsLabelFetcher;
import fr.humanum.masa.federation.index.SearchResult;
import fr.humanum.masa.federation.source.FederationSource;
import fr.humanum.masa.federation.source.FederationSourceRdfSupplier;

@Service
public class FederationBusinessServices {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private ExtConfigService extConfigService;
	private Set<FederationSource> federationSources;
	private IndexService indexService;

	@Inject
	public FederationBusinessServices(ExtConfigService extConfigService, IndexService indexService) {
		this.extConfigService = extConfigService;
		this.indexService = indexService;
	}
	
	@PostConstruct
	public void init() {
		FederationSourceRdfSupplier frs = new FederationSourceRdfSupplier(extConfigService);
		this.federationSources=frs.get();
	}
	
	public void getResultToXml(String queryString,Repository repository, OutputStream out) throws IOException {
		try (RepositoryConnection conn = repository.getConnection()) {
			Perform.on(conn).query(queryString, new SPARQLResultsXMLWriter(out));
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
			federation.addMember(createFederationRepositoryFromSource(aSource));
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
	 * Créé un Repository pour la source indiquée
	 * 
	 * @param sources
	 * @return
	 */
	public Repository createFederationRepositoryFromSource(FederationSource source){
		log.debug("Building repository with endpoint "+source.getEndpoint());
		RepositorySupplier rs = new RepositorySupplier(
				source.getEndpoint().stringValue(),
				source.getDefaultGraph().map(iri -> iri.stringValue()).orElse(null)
		);
		return rs.getRepository();
	}
	
	/**
	 * Renvoie toutes les sources paramétrées dans la fédération
	 * @return
	 */
	public Set<FederationSource> getFederationSources() {
		return this.federationSources;
	}
	
	/**
	 * Filtre les sources à utiliser pour créer une fédération en fonction des clauses
	 * FROM de la query.
	 * 
	 * @param q
	 * @return
	 */
	public Set<FederationSource> filterFederationSources(Query q) {
		// if query does not have FROM clauses, we return all the sources
		if(q.getGraphURIs() == null || q.getGraphURIs().isEmpty()){
			return this.federationSources;
		} else {
			log.debug("Clause from trouvée");
			Set<FederationSource> filteredFederationSource = new HashSet<FederationSource>();
			List<String> sourcesList = q.getGraphURIs();
			for (String source : sourcesList) {
				FederationSource fs = findSource(source);
				if(fs != null) {
					log.debug("source found :"+ source);
					filteredFederationSource.add(fs);
				}
			}
			return filteredFederationSource;
		}
	}
	
	public FederationSource findSource(String sourceIri) {
		for (FederationSource aSource : federationSources) {
			if(aSource.getSourceIri().equals(SimpleValueFactory.getInstance().createIRI(sourceIri))){
				return aSource;
			}
		}
		return null;
	}
	
	public Query removeFromClauses(Query q) {
		List<String> querySources = q.getGraphURIs();
		List<String> sourcesToRemove = querySources.stream().filter(s -> { 
			for (FederationSource aSource : this.federationSources) {
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
	public List<QueryExample> getExampleQueries() {
		File exampleQueryFile = this.extConfigService.getExampleQueriesFile();
		
		if(exampleQueryFile != null && exampleQueryFile.exists()){
			ObjectMapper objectMapper=new ObjectMapper();
			//add this line  
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);    
			try {
				return objectMapper.readValue(exampleQueryFile , new TypeReference<List<QueryExample>>(){});
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			log.warn("Exemple queries config file is not setup or does not exists. It value it : "+exampleQueryFile);
			return null;
		}
	}
	
	public List<SearchResult> autocomplete(
			String key,
			IRI domain,
			IRI property,
			IRI range,
			int limit
	) throws IOException {		
		String indexId = (domain+"_"+property+"_"+range).replaceAll("\\W+", "");
		return this.autocomplete(key, indexId, limit);
	}
	
	public List<SearchResult> autocomplete(
			String key,
			String indexId,
			int limit
	) throws IOException {		
		return this.indexService.autocomplete(key, indexId, limit);
	}
	
	public Set<String> getIndexIds() throws IOException {
		Set<String> result = this.indexService.getDistinctIndexIds();
		log.debug("Retrieved index IDs "+result);
		return result;
	}

	
	public void reindex(IRI source) throws IOException {
		// creer un repository pour la source indiquee
		FederationSource fs = findSource(source.stringValue());
		Repository r = createFederationRepositoryFromSource(fs);
		
		SimpleValueFactory f = SimpleValueFactory.getInstance();
		LuceneDocumentBuilder b1 = new LuceneDocumentBuilder(
    			"http://www.mae.u-paris10.fr/sanct/ontologie#Sanctuaire_http://www.mae.u-paris10.fr/sanct/ontologie#positionTopographique_http://www.mae.u-paris10.fr/sanct/ontologie#PositionTopographique".replaceAll("\\W+", ""),
    			new DomainPathRangeIriFetcher(f.createIRI("http://www.mae.u-paris10.fr/sanct/ontologie#Sanctuaire"), "<http://www.mae.u-paris10.fr/sanct/ontologie#positionTopographique>", f.createIRI("http://www.mae.u-paris10.fr/sanct/ontologie#PositionTopographique")),
    			new RdfsLabelFetcher("fr")
    	);
    	
    	this.indexService.reIndexSource(
    			// identifiant de la source
    			"http://vocabularies.unesco.org/sparql".replaceAll("\\W+", ""),
    			// repository
    			r
    	);
	}
}
