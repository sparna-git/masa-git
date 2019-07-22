package fr.humanum.openarchaeo.federation.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.humanum.openarchaeo.federation.FederationBusinessServices;
import fr.humanum.openarchaeo.federation.period.PeriodJson;

@Controller
@RequestMapping(value = {"/api"})
public class ApiController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 

	private final FederationBusinessServices federationService;

	@Inject
	public ApiController(FederationBusinessServices federationService) throws FileNotFoundException, IOException {
		this.federationService=federationService;
	}
	
	@RequestMapping(value = {""},method=RequestMethod.GET)
	public ModelAndView apiHome(
			HttpServletRequest request,
			HttpServletResponse response
			) throws FileNotFoundException, IOException{
		return new ModelAndView("api");
	}

	@RequestMapping(value = {"/sources"},method=RequestMethod.GET)
	public void  sources(
			HttpServletRequest request,
			HttpServletResponse response
	) throws JsonGenerationException, JsonMappingException, IOException {
		
		List<FederationSourceJson> fsoList = federationService.getFederationSources().stream()
				.map(fed -> FederationSourceJson.fromFederationSource(fed))
				.collect(Collectors.toList());
		
		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json");		
		writeJson(fsoList, response.getWriter());
	}

	
	@RequestMapping(value = {"/autocomplete"}, params = { "domain", "property", "range", "key" },method=RequestMethod.GET)
	public void  autocomplete(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="key",required=true) String key,
			@RequestParam(value="domain",required=true) String domain,
			@RequestParam(value="property",required=true) String property,
			@RequestParam(value="range",required=true) String range,
			@RequestParam(value="sources",required=false) String sources
	) throws JsonGenerationException, JsonMappingException, IOException {
		
		String indexId = (domain+"_"+property+"_"+range).replaceAll("\\W+", "");
		autocomplete(
			request,
			response,
			key,
			indexId,
			sources
		);
	}
	
	@RequestMapping(value = {"/autocomplete"}, params = { "index", "key" }, method=RequestMethod.GET)
	public void  autocomplete(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="key",required=true) String key,
			@RequestParam(value="index",required=true) String index,
			@RequestParam(value="sources",required=false) String sources
	) throws JsonGenerationException, JsonMappingException, IOException {
		
		List<String> sourcesList = (sources != null && !sources.isEmpty())?Arrays.asList(sources.split(" ")):null;
		List<SearchResultJson> results = federationService.autocomplete(
				key,
				index,
				sourcesList,
				5
		)
		.stream()
		.map(sr -> SearchResultJson.fromSearchResult(sr))
		.collect(Collectors.toList());
		
		
		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json");		
		writeJson(results, response.getWriter());
	}
	
	@RequestMapping(value = {"/list"}, params = { "domain", "property", "range" },method=RequestMethod.GET)
	public void  list(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="domain",required=true) String domain,
			@RequestParam(value="property",required=true) String property,
			@RequestParam(value="range",required=true) String range
	) throws JsonGenerationException, JsonMappingException, IOException {
		
		String indexId = (domain+"_"+property+"_"+range).replaceAll("\\W+", "");
		list(
			request,
			response,
			indexId
		);
	}
	
	@RequestMapping(value = {"/periods"}, params = { "lang" },method=RequestMethod.GET)
	public void  periodo(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="lang",required=true) String lang
	) throws JsonGenerationException, JsonMappingException, IOException {
		
		List<PeriodJson> results = this.federationService.getPeriods(lang);
		
		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json");		
		writeJson(results, response.getWriter());
	}
	
	@RequestMapping(value = {"/list"}, params = { "index" }, method=RequestMethod.GET)
	public void  list(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value="index",required=true) String index
	) throws JsonGenerationException, JsonMappingException, IOException {
		
		List<SearchResultJson> results = federationService.list(
				index
		)
		.stream()
		.map(sr -> SearchResultJson.fromSearchResult(sr))
		.collect(Collectors.toList());		
		
		response.addHeader("Content-Encoding", "UTF-8");	
		response.setContentType("application/json");		
		writeJson(results, response.getWriter());
	}
	
	protected void writeJson(Object o, PrintWriter out) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(out, o);
	}
	
}
