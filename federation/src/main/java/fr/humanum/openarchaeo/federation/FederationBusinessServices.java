package fr.humanum.openarchaeo.federation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.jena.query.Query;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultWriter;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultWriterFactory;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultWriterRegistry;
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

import fr.humanum.openarchaeo.Perform;
import fr.humanum.openarchaeo.federation.index.IndexService;
import fr.humanum.openarchaeo.federation.index.SearchResult;
import fr.humanum.openarchaeo.federation.period.PeriodJson;
import fr.humanum.openarchaeo.federation.period.PeriodServiceIfc;
import fr.humanum.openarchaeo.federation.source.FederationSource;
import fr.humanum.openarchaeo.federation.source.FederationSourceRdfSupplier;

@Service
public class FederationBusinessServices {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private ExtConfigService extConfigService;
	private List<FederationSource> federationSources;
	private IndexService indexService;
	private PeriodServiceIfc periodsService;

	@Inject
	public FederationBusinessServices(ExtConfigService extConfigService, IndexService indexService, PeriodServiceIfc periodsService) {
		this.extConfigService = extConfigService;
		this.indexService = indexService;
		this.periodsService = periodsService;
	}
	
	@PostConstruct
	public void init() {
		FederationSourceRdfSupplier frs = new FederationSourceRdfSupplier(extConfigService);
		this.federationSources=frs.get();
	}
	
	public void getResult(String queryString, Repository repository, OutputStream out, String mimeType) throws IOException {
		TupleQueryResultWriterRegistry registry = TupleQueryResultWriterRegistry.getInstance();
		TupleQueryResultWriterFactory f = registry.get(
				registry.getFileFormatForMIMEType(mimeType).orElse(TupleQueryResultFormat.SPARQL)
		).orElse(registry.get(TupleQueryResultFormat.SPARQL).get());
		
		TupleQueryResultWriter writer = f.getWriter(out);
		
		try (RepositoryConnection conn = repository.getConnection()) {
			Perform.on(conn).query(queryString, writer);
		}
	}
	

	/**
	 * Créé un Repository avec les sources indiquées.
	 * 
	 * @param sources
	 * @return
	 */
	public Repository createFederationRepositoryFromSources(List<FederationSource> sources){
		log.debug("Création de la  féderation avec "+sources.size()+" sources");
		if(sources.size() == 1) {
			log.debug("Une seule source, création d'un repository simple");
			return createFederationRepositoryFromSource(sources.get(0));
		}
		
		Federation federation=new Federation();
		
		//add repositories in a federation object
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
	public List<FederationSource> getFederationSources() {
		return this.federationSources;
	}
	
	/**
	 * Filtre les sources à utiliser pour créer une fédération en fonction des clauses
	 * FROM de la query.
	 * 
	 * @param q
	 * @return
	 */
	public List<FederationSource> filterFederationSources(Query q) {
		// if query does not have FROM clauses, we return all the sources
		if(q.getNamedGraphURIs() == null || q.getNamedGraphURIs().isEmpty()){
			log.debug("No named graphs declared, querying all sources");
			return this.federationSources;
		} else {
			log.debug("Found some named graphs declared");
			List<FederationSource> filteredFederationSource = new ArrayList<FederationSource>();
			for (String source : q.getNamedGraphURIs()) {
				FederationSource fs = findSource(source);
				if(fs != null) {
					log.debug("Found source :"+ fs.getSourceIri());
					filteredFederationSource.add(fs);
				} else {
					log.warn("Oups, found an unknown source :"+ source);
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
		List<String> querySources = q.getNamedGraphURIs();
		List<String> sourcesToRemove = querySources.stream().filter(s -> { 
			for (FederationSource aSource : this.federationSources) {
				if(aSource.getSourceIri().stringValue().equals(s)) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		
		for (String source : sourcesToRemove) {
			q.getNamedGraphURIs().remove(source);
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
				List<QueryExample> result = objectMapper.readValue(exampleQueryFile , new TypeReference<List<QueryExample>>(){});
				log.debug("Read query examples :\n"+result);
				return result;
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
			String indexId,
			int limit
	) throws IOException {		
		return this.indexService.autocomplete(key, indexId, "fr", limit);
	}
	
	public List<SearchResult> list(
			String indexId
	) throws IOException {		
		List<SearchResult> result = this.indexService.getEntries(indexId);
		// sort the list alphabetically
		Collections.sort(result, new Comparator<SearchResult>() {

			@Override
			public int compare(SearchResult r1, SearchResult r2) {
				return r1.getLabel().compareToIgnoreCase(r2.getLabel());
			}
			
		});
		return result;
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
    	
    	this.indexService.reIndexSource(
    			// identifiant de la source
    			source.stringValue().replaceAll("\\W+", ""),
    			// repository
    			r
    	);
	}
	
	public List<PeriodJson> getPeriods(String lang) {
		return this.periodsService.getPeriods(lang);
	}
}
