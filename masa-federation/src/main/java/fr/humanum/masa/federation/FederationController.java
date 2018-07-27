package fr.humanum.masa.federation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.humanum.masa.federation.source.FederationSource;
import fr.humanum.masa.federation.source.FederationSourceRdfSupplier;
import fr.humanum.masa.federation.source.SimpleFederationSourceJsonOutput;

@Controller
public class FederationController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 

	private final FederationService federationService;
	//private Properties properties;
	private final ExtConfigService extConfigService;


	@Inject
	public FederationController(FederationService federationService, ExtConfigService extConfigService) throws FileNotFoundException, IOException {
		this.federationService=federationService;
		//this.properties=extConfigService.getApplicationProperties();
		this.extConfigService=extConfigService;
	}


	@RequestMapping(value = {"/","home"},method=RequestMethod.GET)
	public ModelAndView sparqlHome(
			HttpServletRequest request,
			HttpServletResponse response
			) throws FileNotFoundException, IOException{
		
		//create a list with all queries
		FederationData data=new FederationData();
		data.setQueries(extConfigService.getApplicationQueries());
		
		//get all sources
		FederationSourceRdfSupplier frs=new FederationSourceRdfSupplier(extConfigService);
		Set<FederationSource> federationSources=frs.get();
		data.setFederationSources(federationSources);
		return new ModelAndView("home",FederationData.KEY,data);
	}


	@RequestMapping(value = {"sparql"},method={RequestMethod.POST,RequestMethod.GET})
	public void getXMLResult(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query
			)
	throws Exception {
		log.debug("Requête SPARQL à exécuter : "+query);

		Query q=  QueryFactory.create(query) ; 

		FederationSourceRdfSupplier frs=new FederationSourceRdfSupplier(extConfigService);
		Set<FederationSource> federationSource=frs.get();

		if(q.getNamedGraphURIs()!=null && !q.getNamedGraphURIs().isEmpty()){
			log.debug("Clause from trouvée");
			Set<FederationSource> filteredFederationSource=new HashSet<FederationSource>();
			List<String> sourcesList=q.getNamedGraphURIs();
			for (String source : sourcesList) {
				federationSource.forEach(fs->{
					if(fs.getSourceIri().equals(SimpleValueFactory.getInstance().createIRI(source))){
						log.debug("source found :"+ source);
						filteredFederationSource.add(fs);
					}
				});
			}
			federationSource=filteredFederationSource;
		}

		federationService.initializeRepositoryWithFederation(federationSource);		
		response.setContentType(TupleQueryResultFormat.SPARQL.getDefaultMIMEType());
		federationService.getResultToXml(query,federationService.getRepository(),response.getOutputStream());
		log.debug("Fin d'exécution de la requête");
	}


	@RequestMapping(value = {"/api/sources"},method=RequestMethod.GET)
	public void  descriptionSource(
			HttpServletRequest request,
			HttpServletResponse response
			) throws JsonGenerationException, JsonMappingException, IOException{
				response.addHeader("Content-Encoding", "UTF-8");	
				response.setContentType("application/json"); 
				ObjectMapper mapper = new ObjectMapper();
				PrintWriter out = response.getWriter();
				FederationSourceRdfSupplier frs=new FederationSourceRdfSupplier(extConfigService);
				Set<FederationSource> federationSources=frs.get();
				Set<SimpleFederationSourceJsonOutput> fsoList=new HashSet<SimpleFederationSourceJsonOutput>();
				federationSources.forEach(fed->{
					SimpleFederationSourceJsonOutput fedJsonOut=new SimpleFederationSourceJsonOutput(
							fed.getSourceIri().toString(),
							fed.getEndpoint().toString(), 
							fed.getDefaultGraph().get().toString(),
							fed.getLabels()
							);
					fsoList.add(fedJsonOut);
				});
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				mapper.writeValue(out, fsoList);
	}



}
