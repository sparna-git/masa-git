package fr.humanum.masa.federation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.humanum.masa.federation.source.FederationSource;
import fr.humanum.masa.federation.source.FederationSourceRdfSupplier;

@Controller
public class FederationController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	private final FederationService federationService;
	private Properties properties;
	private final ExtConfigService extConfigService;


	@Inject
	public FederationController(FederationService federationService, ExtConfigService extConfigService) throws FileNotFoundException, IOException {
		this.federationService=federationService;
		this.properties=extConfigService.getApplicationProperties();
		this.extConfigService=extConfigService;
	}


	@RequestMapping(value = {"/","home"},method=RequestMethod.GET)
	public ModelAndView sparqlHome(
			HttpServletRequest request,
			HttpServletResponse response
			){
		ModelAndView model=new ModelAndView("home");
		return model;
	}
	
	
	@RequestMapping(value = {"sparql"},method={RequestMethod.POST,RequestMethod.GET})
	public void getXMLResult(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query
			){

		try {
			
			log.debug("Requête SPARQL à exécuter : "+query);
			
			Query q=  QueryFactory.create(query) ; 
			
			FederationSourceRdfSupplier frs=new FederationSourceRdfSupplier(extConfigService);
			Set<FederationSource> federationSource=frs.get();
			Set<FederationSource> filteredFederationSource=new HashSet<FederationSource>();
			
			if(q.getNamedGraphURIs()!=null){
				List<String> sourcesList=q.getNamedGraphURIs();
				for (String source : sourcesList) {
					federationSource.forEach(fs->{
						if(fs.getDefaultGraphUri().equals(source)){
							filteredFederationSource.add(fs);
						}
					});
				}
				
				federationSource=filteredFederationSource;
			}
			
			//create federation with sources
			federationService.initializeRepositoryWithFederation(federationSource);
			
			response.setContentType(TupleQueryResultFormat.SPARQL.getDefaultMIMEType());
			federationService.getResultToXml(query,federationService.getRepository(),response.getOutputStream());
			log.debug("Fin d'exécution de la requête");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}

}
