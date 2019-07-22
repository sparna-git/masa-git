package fr.humanum.openarchaeo.federation.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CostfedFederationRepositoryBuilder implements FederationRepositoryBuilder {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private File propertiesFile;
	private String referentielRepository;
	
	
	
	public CostfedFederationRepositoryBuilder(File propertiesFile, String referentielRepository) {
		super();
		this.propertiesFile = propertiesFile;
		this.referentielRepository = referentielRepository;
	}

	@Override
	public Repository buildRepository(List<String> endpoints, String query) {
		log.debug("Building Costfed repository with the following endpoints :");
		endpoints.stream().forEach(e -> log.debug("  "+e));
		
		List<String> effectiveEndpoints;
		if(endpoints.contains(this.referentielRepository)) {
			effectiveEndpoints = endpoints;
		} else {
			log.debug("  "+this.referentielRepository+" added automatically in the federation");
			effectiveEndpoints = new ArrayList<>(endpoints);
			effectiveEndpoints.add(this.referentielRepository);
		}
//		FedXSailRepository costfedRep = FedXFactory.initializeFederation(this.propertiesFile.getAbsolutePath(), new DefaultEndpointListProvider(effectiveEndpoints));
//		costfedRep.initialize();
//		return costfedRep;
		
		return null;
	}

}
