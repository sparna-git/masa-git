package fr.humanum.masa.openarchaeo.explorateur;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


@Controller
public class ExplorateurController {

	private Logger log= LoggerFactory.getLogger(this.getClass().getName());

	private final ExplorateurService explorateurService;


	@Inject
	public ExplorateurController(ExplorateurService explorateurService) throws FileNotFoundException, IOException {
		this.explorateurService=explorateurService;
	}

	@RequestMapping(value = {"home"},method=RequestMethod.GET)
	public ModelAndView home(
			HttpServletRequest request,
			HttpServletResponse response
			){

		ModelAndView model=new ModelAndView("home");
		return model;
	}
	
	@RequestMapping(value = {"sourcesSelect","/"},method=RequestMethod.GET)
	public ModelAndView sourcesSelect(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {

		log.debug("Récupération des sources");
		String sourcesDefinition = explorateurService.getSources();
		log.debug("Récupération des sources terminée");
		
		ModelAndView model=new ModelAndView("sourcesSelect");
		model.addObject("sourcesDefinition", sourcesDefinition);
		return model;
	}

	@RequestMapping(value = {"explorer"},method=RequestMethod.GET)
	public ModelAndView sparql(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="sources",required=true) String sources
			){

		ModelAndView model=new ModelAndView("explorer");
		model.addObject("sources", sources);
		model.addObject("queries", this.explorateurService.getExampleQueries());
		return model;
	}

	@RequestMapping(value = {"expand"},method={RequestMethod.GET, RequestMethod.POST})
	public void expand(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query,
			@RequestParam(value="sources",required=false) String sourceParam
	) throws IOException{

		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json"); 
		
		log.debug("Extension de la requête simple");
		
		//ajout de la source à la requete si source != null
		query=explorateurService.addSourceToQuery(query,sourceParam);
		
		//Expand query
		String queryExpand=explorateurService.expandQuery(query);
		
		ExplorateurData data= new ExplorateurData();
		data.setQuery(query);
		data.setExpandQuery(queryExpand);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(response.getOutputStream(), data);
		log.debug("Fin d'extension de la requête simple");
	}


	@RequestMapping(value = {"result"},method=RequestMethod.POST)
	public void getResult(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query) throws IOException, ClassNotFoundException{

		log.debug("Récupération du résultat de la requête étendue");
		this.explorateurService.getResult(query, response.getOutputStream());
		log.debug("Récupération du résultat terminée");
	}

}
