package fr.humanum.openarchaeo.federation.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	@Autowired
	private SessionManager sessionManager;

	
	@RequestMapping(value = { "login" }, method = RequestMethod.GET)
	public ModelAndView login(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			HttpServletRequest request,
			HttpServletResponse response
			) {

		ModelAndView model = new ModelAndView();

		if (error != null) {
			model.addObject("error", "Nom ou mot de passe incorrect!");
		}

		if (logout != null) {
			sessionManager.killSession();
			request.getSession().invalidate();
			model.addObject("msg", "Vous êtes maintenant déconnecté(e).");
		}
		model.setViewName("login");

		return model;

	}

}
