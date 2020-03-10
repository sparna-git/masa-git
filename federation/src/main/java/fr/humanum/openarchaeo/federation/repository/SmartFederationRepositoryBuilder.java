package fr.humanum.openarchaeo.federation.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fluidops.fedx.Config;
import com.fluidops.fedx.FedXFactory;
import com.fluidops.fedx.FederationManager;
import com.fluidops.fedx.exception.FedXException;
import com.fluidops.fedx.structures.Endpoint;
import com.fluidops.fedx.util.EndpointFactory;

import fr.humanum.openarchaeo.federation.source.FederationSource;

public class SmartFederationRepositoryBuilder implements FederationRepositoryBuilder {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private String referentielRepository;
	
	private Repository repository;
	
	public SmartFederationRepositoryBuilder(String referentielRepository) {
		super();
		this.referentielRepository = referentielRepository;
	}

	@Override
	public Repository buildRepository(List<? extends FederationSource> sources, String query) {
		log.debug("Building repository with the following sources :");
		sources.stream().forEach(e -> log.debug("  "+e));
		
		try {
			
			RequiresFederationPredicate test = new RequiresFederationPredicate();
			boolean requiresFederation = test.test(test.new QueryOverSources(query, sources));
			
			if(requiresFederation) {
				log.debug("Federation required");
				return buildFederationRepository(sources);
			} else {
				log.debug("Federation not necessary, default graph will be set in query");
				SPARQLRepository repository = new SPARQLRepository(sources.get(0).getEndpoint().stringValue());
				
				repository.setAdditionalHttpHeaders(new HashMap<String, String>() {{ 
					put("Accept", "application/json,application/xml;q=0.9");
				}} );
				
				repository.initialize();
				
				return repository;
			}			

		} catch (FedXException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	public Repository buildFederationRepository(List<? extends FederationSource> sources) throws FedXException {
		List<String> effectiveEndpoints = sources.stream().map(s -> RepositorySupplier.constructEndpointUrl(s)).collect(Collectors.toList());
		effectiveEndpoints.add(this.referentielRepository);
		
		if(!FederationManager.isInitialized()) {
			Config.initialize();
//			Config.getConfig().set("debugQueryPlan", "true");
//			Config.getConfig().set("enableMonitoring", "true");
//			Config.getConfig().set("monitoring.logQueries", "true");
//			Config.getConfig().set("debugWorkerScheduler", "true");
//			Config.getConfig().set("joinWorkerThreads", "1");
//			Config.getConfig().set("unionWorkerThreads", "1");
		}
		
		List<Endpoint> fedxEndpoints = effectiveEndpoints.stream().map(s -> {
			try {
				return EndpointFactory.loadSPARQLEndpoint(s);
			} catch (FedXException e) {
				e.printStackTrace();
				return null;
			}
		}).collect(Collectors.toList());
		
		if(!FederationManager.isInitialized()) {			
			this.repository = FedXFactory.initializeFederation(fedxEndpoints);
			this.repository.initialize();
		} else {
			FederationManager.getInstance().removeAll();
			FederationManager.getInstance().addAll(fedxEndpoints);
		}

		return this.repository;
	}

}
