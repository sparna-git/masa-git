package fr.humanum.masa.explorateur;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class ExplorateurController {

	private Logger log= LoggerFactory.getLogger(this.getClass().getName());
	private Properties properties;
	private final ExtConfigService extConfigService;
	private final ExplorateurService explorateurService;
	

	@Inject
	public ExplorateurController(ExtConfigService extConfigService, ExplorateurService explorateurService) throws FileNotFoundException, IOException {
		this.extConfigService=extConfigService;
		this.explorateurService=explorateurService;
		this.properties=this.extConfigService.getProperties("config_explorateur.properties");
	}

	@RequestMapping(value = {"home","/"},method=RequestMethod.GET)
	public ModelAndView sparql(
			HttpServletRequest request,
			HttpServletResponse response
			){

		ModelAndView model=new ModelAndView("home");
		return model;
	}
	
	@RequestMapping(value = {"expand"},method=RequestMethod.POST)
	public ModelAndView expand(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query) throws IOException{
		
		///Expand query
		log.debug("Extension de la requête simple");
		SparqlProperty sparqlProperty=new SparqlProperty(properties);
		SemanticExpander se = new SemanticExpander(sparqlProperty);
		String queryExpand=se.expand(query);
		ModelAndView model=new ModelAndView("home");
		String queryExpandReplace=queryExpand.replace("\n", " \\\n");
		model.addObject("query", query);
		model.addObject("queryExpand", queryExpand);
		model.addObject("queryExpandReplace",queryExpandReplace);
		model.addObject("getResult",true);
		log.debug("Fin d'extension de la requête simple");
		return model;
	}
	
	@RequestMapping(value = {"result"},method=RequestMethod.POST)
	public void getResult(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query) throws IOException, ClassNotFoundException{
		
		log.debug("Récupération du résultat de la requête étendue");
		String federationServiceUrl=properties.getProperty("federation.service.url");
        explorateurService.getResult(federationServiceUrl, query, response.getOutputStream());
        log.debug("Récupération du résultat terminée");
	}

}
