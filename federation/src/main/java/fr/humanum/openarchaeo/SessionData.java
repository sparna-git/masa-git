package fr.humanum.openarchaeo;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

public class SessionData {

	public static final String KEY = SessionData.class.getCanonicalName();

	// The user Locale
	protected Locale userLocale;
	
	/**
	 * Stores this data into session
	 * @param session
	 */
	public void store(HttpSession session) {
		session.setAttribute(KEY, this);
	}
	
	/**
	 * Retrieves the SessionData object stored into the session.
	 * 
	 * @param session
	 * @return
	 */
	public static SessionData get(HttpSession session) {
		return (SessionData)session.getAttribute(KEY);
	}
	

	public Locale getUserLocale() {
		return userLocale;
	}

	public void setUserLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}
	
}
