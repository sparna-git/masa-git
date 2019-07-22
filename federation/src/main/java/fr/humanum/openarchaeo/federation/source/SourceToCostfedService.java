package fr.humanum.openarchaeo.federation.source;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.humanum.openarchaeo.federation.costfed.Endpoints;
import fr.humanum.openarchaeo.federation.repository.RepositorySupplier;

@Service
public class SourceToCostfedService {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private Endpoints costFedEndpoints;	
	
	public SourceToCostfedService(Endpoints costFedEndpoints) {
		super();
		this.costFedEndpoints = costFedEndpoints;
	}
	
	public void synchronize(List<FederationSource> sources) throws IOException {
		log.info("Synchronizing sources to costfed...");
		for (FederationSource aSource : sources) {
			String endpointUrl = RepositorySupplier.constructEndpointUrl(aSource);
			log.debug("Looking for "+endpointUrl);
			String costfedId = costFedEndpoints.findEndpointByUrl(endpointUrl);
			if(costfedId == null) {
				log.debug("Endpoint "+endpointUrl+" not found - creating it");
		        
		        Properties props = new Properties();
		        props.setProperty(Endpoints.ADDRESS_PROP, endpointUrl);
		        props.setProperty(Endpoints.ENABLE_PROP, "true");
		        props.setProperty(Endpoints.SUMMARY_PROGRESS_PROP, "0");
				String newId = costFedEndpoints.add(props);
				log.debug("Created endpoint "+newId);
			}
			
		}
	}
	
}
