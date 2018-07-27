package fr.humanum.masa.explorateur;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
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

	@RequestMapping(value = {"home","/"},method=RequestMethod.GET)
	public ModelAndView home(
			HttpServletRequest request,
			HttpServletResponse response
			){

		ModelAndView model=new ModelAndView("home");
		return model;
	}

	@RequestMapping(value = {"sparql"},method=RequestMethod.POST)
	public ModelAndView sparql(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="source",required=true) String source
			){

		ModelAndView model=new ModelAndView("sparql");
		model.addObject("source", source);
		return model;
	}

	@RequestMapping(value = {"expand"},method={RequestMethod.GET, RequestMethod.POST})
	public void expand(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="query",required=true) String query,
			@RequestParam(value="source",required=false) String source
			) throws IOException{

		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json"); 
		ObjectMapper mapper = new ObjectMapper();
		///Expand query
		log.debug("Extension de la requête simple");



		//ajout dee la source à la requete si source !=null
		if(source!=null){
			query=addSourceToQuery(query,source);
		}

		String queryExpand=explorateurService.expandQuery(query);
		ExplorateurData data= new ExplorateurData();
		data.setQuery(query);
		data.setExpandQuery(queryExpand);
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
		String federationServiceUrl=properties.getProperty("federation.service.url");
		explorateurService.getResult(federationServiceUrl, query, response.getOutputStream());
		log.debug("Récupération du résultat terminée");
	}

	@RequestMapping(value = {"sources"},method=RequestMethod.GET)
	public void getSources(HttpServletRequest request,HttpServletResponse response) throws ClientProtocolException, IOException
	{
		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json"); 
		log.debug("Récupération des sources");
		String federationApiSourcesUrl=properties.getProperty("federation.service.api.sources");
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet req = new HttpGet(federationApiSourcesUrl);
		HttpResponse resp = client.execute(req);
		IOUtils.copy(resp.getEntity().getContent(),response.getOutputStream());
		log.debug("Récupération des sources terminée");
	}

	public String addSourceToQuery(String query, String source) throws ClientProtocolException, IOException
	{
		log.debug("ajout de la source à la requête");
		Query q=QueryFactory.create(query);
		q.getNamedGraphURIs().add(source);
		log.debug("ajout de la source à la requête terminé");
		return q.toString(Syntax.syntaxSPARQL_11);
	}

}
