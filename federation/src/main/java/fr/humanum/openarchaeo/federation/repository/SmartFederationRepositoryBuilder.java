package fr.humanum.openarchaeo.federation.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.eclipse.rdf4j.repository.Repository;
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
	public Repository buildRepository(List<String> endpoints, String query) {
		log.debug("Building repository with the following endpoints :");
		endpoints.stream().forEach(e -> log.debug("  "+e));
		
		try {
			if(endpoints.size() > 1) {
				log.debug("More than 1 endpoint, building a federation");
				return buildFederationRepository(endpoints);
			} else {
				log.debug("Only 1 endpoint, see if referential properties are needed");
				
				final Query q = QueryFactory.create(query);
				HasPredicateVisitor visitorLat = new HasPredicateVisitor("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
				HasPredicateVisitor visitorLong = new HasPredicateVisitor("http://www.w3.org/2003/01/geo/wgs84_pos#long");
				q.getQueryPattern().visit(visitorLat);
				q.getQueryPattern().visit(visitorLong);
				
				if(visitorLat.isFound() || visitorLong.isFound()) {
					log.debug("Referential properties are needed, building a federation with referential repository");
					return buildFederationRepository(endpoints);
				} else {
					log.debug("No referential properties needed, building a SPARQL repository");
					return FederationSource.createFederationRepositoryFromSource(endpoints.get(0));
				}
			}
		} catch (FedXException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	public Repository buildFederationRepository(List<String> endpoints) throws FedXException {
		List<String> effectiveEndpoints;
		effectiveEndpoints = new ArrayList<>(endpoints);
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
