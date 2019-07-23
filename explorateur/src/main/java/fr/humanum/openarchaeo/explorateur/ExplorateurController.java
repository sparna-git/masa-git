package fr.humanum.openarchaeo.explorateur;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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

import fr.humanum.openarchaeo.SessionData;
import fr.sparna.i18n.StrictResourceBundleControl;


@Controller
public class ExplorateurController {

	private Logger log= LoggerFactory.getLogger(this.getClass().getName());

	private final ExplorateurService explorateurService;
	private final ExtConfigService extConfigService;

	@Inject
	public ExplorateurController(ExplorateurService explorateurService, ExtConfigService extConfigService) throws FileNotFoundException, IOException {
		this.explorateurService=explorateurService;
		this.extConfigService = extConfigService;
	}

	@RequestMapping(value = {"home"},method=RequestMethod.GET)
	public ModelAndView home(
			HttpServletRequest request,
			HttpServletResponse response
			){

		ModelAndView model=new ModelAndView("home");
		model.addObject("content", this.readHomePage(SessionData.get(request.getSession()).getUserLocale()));
		model.addObject("legalNotice", this.readLegalNotice(SessionData.get(request.getSession()).getUserLocale()));
		return model;
	}
	
	@RequestMapping(value = {"sourcesSelect","/"},method=RequestMethod.GET)
	public ModelAndView sourcesSelect(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {

		log.debug("Récupération des sources");
		List<FederationSourceJson> sourcesDefinition = explorateurService.getSources();
		log.debug("Récupération des sources terminée");
		
		ModelAndView model=new ModelAndView("sourcesSelect");
		model.addObject("sourcesDefinition", sourcesDefinition);
		model.addObject("legalNotice", this.readLegalNotice(SessionData.get(request.getSession()).getUserLocale()));
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
		model.addObject("legalNotice", this.readLegalNotice(SessionData.get(request.getSession()).getUserLocale()));
		return model;
	}

	@RequestMapping(value = {"expand"},method={RequestMethod.GET, RequestMethod.POST})
	public void expand(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query,
			@RequestParam(value="sources",required=false) String sourceParam,
			@RequestParam(value="view",required=true) String view
	) throws IOException{

		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json"); 
		
		log.debug("Expand query  : 1. add sources, 2. translate to CIDOC-CRM, 3. add extra properties");
		
		//ajout de la source à la requete si source != null
		query=explorateurService.addSourceToQuery(query,sourceParam);
		
		//Expand query
		String queryExpand=explorateurService.expandQuery(query);
		
		// add extra properties
		ExplorateurService.View viewValue = ExplorateurService.View.valueOf(view.toUpperCase());
		queryExpand = explorateurService.addPropertiesToQuery(queryExpand, viewValue);
		
		ExplorateurData data= new ExplorateurData();
		data.setQuery(query);
		data.setExpandQuery(queryExpand);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(response.getOutputStream(), data);
		log.debug("Final query after expansion : "+"\n"+queryExpand);
	}
	
	private String readLegalNotice(Locale locale) {
		// retrieve resource bundle for path to home page
		ResourceBundle b = ResourceBundle.getBundle(
				"fr.humanum.openarchaeo.explorateur.i18n.OpenArchaeo",
				locale,
				new StrictResourceBundleControl()
		);

		try {
			return IOUtils.toString(new FileInputStream(this.extConfigService.findMandatoryFile(b.getString("window.footer.legalNotice.content"))), Charset.forName("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private String readHomePage(Locale locale) {
		// retrieve resource bundle for path to home page
		ResourceBundle b = ResourceBundle.getBundle(
				"fr.humanum.openarchaeo.explorateur.i18n.OpenArchaeo",
				locale,
				new StrictResourceBundleControl()
		);

		try {
			return IOUtils.toString(new FileInputStream(this.extConfigService.findMandatoryFile(b.getString("home.content"))), Charset.forName("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}