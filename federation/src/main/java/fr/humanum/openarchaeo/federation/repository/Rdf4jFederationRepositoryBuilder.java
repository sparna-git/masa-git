package fr.humanum.openarchaeo.federation.repository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
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
	public Repository buildRepository(List<FederationSource> sources, String query) {
		if(sources.size() == 1) {
			log.debug("Single source, creating a simple Repository");
			return new RepositorySupplier(sources.get(0)).getRepository();
		} else {
			log.debug("Building RDF4J Federation with the following endpoints :");
			sources.stream().forEach(e -> log.debug("  "+e));
			
			boolean containsReferentielRepository = sources.stream().anyMatch(s -> s.getEndpoint().stringValue().equals(this.referentielRepository));
			
			List<String> effectiveEndpoints = sources.stream().map(s -> RepositorySupplier.constructEndpointUrl(s)).collect(Collectors.toList());
			if(!containsReferentielRepository) {
				log.debug("  "+this.referentielRepository+" added automatically in the federation");
				effectiveEndpoints.add(this.referentielRepository);
			}			
			
			Federation federation=new Federation();
			
			//add repositories in a federation object
			for (String aSource : effectiveEndpoints) {
				SPARQLRepository repository = new SPARQLRepository(aSource);
				
				repository.setAdditionalHttpHeaders(new HashMap<String, String>() {{ 
					put("Accept", "application/json,application/xml;q=0.9");
				}} );
				repository.initialize();
				
				federation.addMember(repository);
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
