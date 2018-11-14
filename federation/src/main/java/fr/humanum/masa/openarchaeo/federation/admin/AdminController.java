package fr.humanum.masa.openarchaeo.federation.admin;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.humanum.masa.openarchaeo.federation.FederationBusinessServices;

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
		data.setIndexIds(federationService.getIndexIds());
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
		federationService.reindex(SimpleValueFactory.getInstance().createIRI(sourceIri));
		
		ModelAndView model=new ModelAndView("redirect:/admin");
		model.addObject("message", "Source réindexée : "+sourceIri);
		
		return model;
	}


}
