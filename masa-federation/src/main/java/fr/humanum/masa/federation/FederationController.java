package fr.humanum.masa.federation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.repository.Repository;
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
import fr.humanum.masa.federation.source.SimpleFederationSourceJsonOutput;

@Controller
public class FederationController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 

	private final FederationBusinessServices federationService;


	@Inject
	public FederationController(FederationBusinessServices federationService) throws FileNotFoundException, IOException {
		this.federationService=federationService;
	}


	@RequestMapping(value = {"/","home"},method=RequestMethod.GET)
	public ModelAndView sparqlHome(
			HttpServletRequest request,
			HttpServletResponse response
			) throws FileNotFoundException, IOException{

		//create a list with all queries
		FederationData data=new FederationData();
		data.setQueries(federationService.getExampleQueries());

		//get all sources
		data.setFederationSources(federationService.readAllFederationSources());
		return new ModelAndView("home",FederationData.KEY,data);
	}


	@RequestMapping(value = {"sparql"},method={RequestMethod.POST,RequestMethod.GET})
	public void getXMLResult(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query
			)
	throws Exception {
		log.debug("Requête SPARQL à exécuter : \n"+query);
		Set<FederationSource> sourcesToQuery = federationService.filterFederationSources(QueryFactory.create(query));

		String queryWithoutFromClauses = federationService.removeFromClauses(QueryFactory.create(query)).toString(Syntax.syntaxSPARQL_11);
		log.debug("Requête après suppression des clauses FROM : \n"+queryWithoutFromClauses);
		
		Repository federationRepository = federationService.createFederationRepositoryFromSources(sourcesToQuery);		
		response.setContentType(TupleQueryResultFormat.SPARQL.getDefaultMIMEType());
		federationService.getResultToXml(queryWithoutFromClauses,federationRepository,response.getOutputStream());
		log.debug("Fin d'exécution de la requête");
	}


	@RequestMapping(value = {"/api/sources"},method=RequestMethod.GET)
	public void  descriptionSource(
			HttpServletRequest request,
			HttpServletResponse response
	) throws JsonGenerationException, JsonMappingException, IOException {
		
		Set<SimpleFederationSourceJsonOutput> fsoList = federationService.readAllFederationSources().stream()
				.map(fed -> SimpleFederationSourceJsonOutput.fromFederationSource(fed))
				.collect(Collectors.toSet());
		
		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json"); 
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(response.getWriter(), fsoList);
	}



}
