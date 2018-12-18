package fr.humanum.openarchaeo.federation.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class SessionInterceptor extends HandlerInterceptorAdapter {

	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	
	@Autowired
	private MasaAdminAuthenticationProvider authenticationProvider;
	
	@Autowired
	private SessionManager sessionManager;
	
	/**
	 * Vérifie si l'utilisateur est loggué et si c'est le cas mais qu'il n'y a pas de session ouverte,
	 * créé une nouvelle sesssion.
	 */
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler)
	throws Exception {
		log.debug("SessionInterceptor preHandle");
		HttpSession session = request.getSession(false);
		if(
				request.getRemoteUser() != null
				&&
				(
					session == null
					||
					sessionManager.getUserDetails() == null
				)
		) {
			log.debug("User logged in as '"+request.getRemoteUser()+"' but no session - will create a new one.");

			// enregistrer les infos de l'utilisateur dans la session
			sessionManager.setUserDetails(new UserDetails(authenticationProvider.getAdminLogin(), authenticationProvider.getAdminLogin(), authenticationProvider.getDefaultRole()));
		}
		
		return true;		
	}

}
