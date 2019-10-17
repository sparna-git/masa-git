package fr.humanum.openarchaeo;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import fr.humanum.openarchaeo.explorateur.FederationSourceJson;

public class SessionData {

	public static final String KEY = SessionData.class.getCanonicalName();

	// The user Locale
	protected Locale userLocale;
	
	protected List<FederationSourceJson> sources;
	
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
	
	public FederationSourceJson findSourceById(String source) {
		for (FederationSourceJson aSource : this.sources) {
			if(aSource.getSourceString().equals(source)) {
				return aSource;
			}
		}
		return null;
	}

	public Locale getUserLocale() {
		return userLocale;
	}

	public void setUserLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}

	public List<FederationSourceJson> getSources() {
		return sources;
	}

	public void setSources(List<FederationSourceJson> sources) {
		this.sources = sources;
	}

}
