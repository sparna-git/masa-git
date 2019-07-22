package fr.humanum.openarchaeo.federation.repository;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.federation.Federation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.humanum.openarchaeo.federation.source.FederationSource;

public class Rdf4jFederationRepositoryBuilder implements FederationRepositoryBuilder {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private String referentielRepository;
	
	public Rdf4jFederationRepositoryBuilder(String referentielRepository) {
		super();
		this.referentielRepository = referentielRepository;
	}



	@Override
	public Repository buildRepository(List<String> endpoints, String query) {
		if(endpoints.size() == 1) {
			log.debug("Single source, creating a simple Repository");
			return FederationSource.createFederationRepositoryFromSource(endpoints.get(0));
		} else {
			log.debug("Building RDF4J Federation with the following endpoints :");
			endpoints.stream().forEach(e -> log.debug("  "+e));
			
			List<String> effectiveEndpoints;
			if(endpoints.contains(this.referentielRepository)) {
				effectiveEndpoints = endpoints;
			} else {
				log.debug("  "+this.referentielRepository+" added automatically in the federation");
				effectiveEndpoints = new ArrayList<>(endpoints);
				effectiveEndpoints.add(this.referentielRepository);
			}			
			
			Federation federation=new Federation();
			
			//add repositories in a federation object
			for (String aSource : effectiveEndpoints) {
				federation.addMember(FederationSource.createFederationRepositoryFromSource(aSource));
			}		
			
			// all repositories in the federation are distinct
			federation.setDistinct(true);

			// our federation will be read-only for sure
			federation.setReadOnly(true);
			
			//create new repository with federation
			log.debug("Création et initialisation du repository");
			Repository repository=new SailRepository(federation);
			repository.initialize();	
			log.debug("Initialisation du repository avec la fédération terminée");
			return repository;
		}
	}

}
