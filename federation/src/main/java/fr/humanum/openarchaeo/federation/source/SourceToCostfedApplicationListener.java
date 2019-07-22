package fr.humanum.openarchaeo.federation.source;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import fr.humanum.openarchaeo.federation.ExtConfigService;

// uncomment to activate
// @Service
public class SourceToCostfedApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private SourceToCostfedService synchService;
	private File sourceDefinitionFile;
	
	public SourceToCostfedApplicationListener(ExtConfigService extConfig, SourceToCostfedService synchService) {
		super();
		this.synchService = synchService;
		this.sourceDefinitionFile = extConfig.getSourceFile();
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("Called listener to synchronize sources to costfed.");
		FederationSourceRdfSupplier supplier = new FederationSourceRdfSupplier(this.sourceDefinitionFile);
		try {
			synchService.synchronize(supplier.get());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
