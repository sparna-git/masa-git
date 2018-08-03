package fr.humanum.masa.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.humanum.masa.SessionManager;
import fr.humanum.masa.Utils;

@Controller
public class UserManagementController {

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

	@RequestMapping(value = "admin")
	public ModelAndView createUser(
			) {

		UserData data=new UserData();
		ModelAndView model=new ModelAndView("admin", UserData.KEY, data);
		return model;

	}


}
