package fr.humanum.openarchaeo.federation.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.humanum.openarchaeo.federation.FederationBusinessServices;

@Controller
@RequestMapping(
		value = {"admin"}
)
public class AdminController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	private final FederationBusinessServices federationService;
	

	public AdminController(FederationBusinessServices federationService) {
		super();
		this.federationService = federationService;
	}

	@RequestMapping(value = { "", "/" })
	public ModelAndView admin(
			@RequestParam(value = "message", required = false) String message,
			HttpServletRequest request,
			HttpServletResponse response	
	) throws IOException {

		AdminData data = new AdminData();
		//get all sources
		if(message != null && !"".equals(message)) {
			data.setMessage(data.new Message("success", message));
		}
		data.setFederationSources(federationService.getFederationSources());	
		//get all index fields
		Set<String> indexIds = federationService.getIndexIds();
		
		// nicer labels
		Map<String, String> indexIdsWithLabels = indexIds.stream().collect(Collectors.toMap(s -> s, s -> { return s.replaceAll("httpwwwopenarchaeofrexplorateuronto", "").replaceAll("_", " "); }));		
		data.setIndexIds(indexIdsWithLabels);
		
		ModelAndView model=new ModelAndView("admin", AdminData.class.getName(), data);
		return model;
	}
	
	@RequestMapping(value = "/sources/reIndex")
	public ModelAndView reIndexSource(
			@RequestParam(value = "source", required = true) String sourceIri,
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {
		log.debug("Re-index source "+sourceIri);
		long start = System.currentTimeMillis();
		federationService.reindex(SimpleValueFactory.getInstance().createIRI(sourceIri));
		long end = System.currentTimeMillis();
		
		ModelAndView model=new ModelAndView("redirect:/admin");
		model.addObject("message", "Source réindexée en "+Math.ceil((end-start)/1000)+" secondes : "+sourceIri);
		
		return model;
	}

}
