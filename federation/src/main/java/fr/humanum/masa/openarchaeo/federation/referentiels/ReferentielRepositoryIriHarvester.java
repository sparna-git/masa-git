package fr.humanum.masa.openarchaeo.federation.referentiels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReferentielRepositoryIriHarvester implements IriHarvester {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	protected String referentielRepositoryUrl;
	private Repository referentialRepository;
	
	public ReferentielRepositoryIriHarvester(String referentielRepositoryUrl) {
		super();
		this.referentielRepositoryUrl = referentielRepositoryUrl;
		this.init();
	}
	
	private void init() {
		this.referentialRepository = new HTTPRepository(referentielRepositoryUrl);
		this.referentialRepository.initialize();
	}

	@Override
	public void harvest(List<IRI> iris) {
		log.debug("Harvesting "+iris.size()+" IRIs...");
		try(RepositoryConnection c = this.referentialRepository.getConnection()) {
			for (IRI anIri : iris) {
				log.debug("  Harvesting IRI "+anIri);
				String updateString = generateSparqlLoad(anIri);
				Update update = c.prepareUpdate(updateString);
				update.execute();
			}
		}
		
	}
	
	public String generateSparqlLoad(IRI anIri) {
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("LOAD <"+anIri+">"+"\n");
			sb.append("INTO GRAPH <urn:graph?iri="+URLEncoder.encode(anIri.toString(), "UTF-8")+"> "+"\n");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
}
