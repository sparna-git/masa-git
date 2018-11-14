package fr.humanum.masa.openarchaeo.federation.admin;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class SessionManager {

	public static final String KEY = SessionManager.class.getCanonicalName();

	public static final String USER_DETAILS = "USER_DETAILS";
	
	public UserDetails getUserDetails() {
		Object userDetails = RequestContextHolder.currentRequestAttributes().getAttribute(USER_DETAILS, RequestAttributes.SCOPE_GLOBAL_SESSION);
        if (userDetails != null) {
            assert userDetails instanceof UserDetails;
            return (UserDetails) userDetails;
        }
        return null;
	}

	public void setUserDetails(UserDetails userDetails) {
		RequestContextHolder.currentRequestAttributes().setAttribute(USER_DETAILS, userDetails, RequestAttributes.SCOPE_GLOBAL_SESSION);
	}
	
	public void killSession() {		
		RequestContextHolder.currentRequestAttributes().removeAttribute(USER_DETAILS, RequestAttributes.SCOPE_GLOBAL_SESSION);	
	}
	
}
