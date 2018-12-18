package fr.humanum.openarchaeo.federation.admin;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public class MasaAdminAuthenticationProvider implements AuthenticationProvider {

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	private String adminLogin;
	private String adminHashedPassword;
	private String defaultRole;

	public MasaAdminAuthenticationProvider(String adminLogin, String adminHashedPassword, String defaultRole) {
		super();
		this.adminLogin = adminLogin;
		this.adminHashedPassword = adminHashedPassword;
		this.defaultRole = defaultRole;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		// retrieve provided username and password
		String useremail = authentication.getName().trim();
		String password = (String) authentication.getCredentials();
		if (password.isEmpty()) {
			throw new BadCredentialsException("Mot de passe vide");
		}

		// vérifier le;login/passwd de l'admin
		String hashedPass = DigestUtils.sha1Hex(password);
		boolean success = useremail.equals(adminLogin)&&hashedPass.equals(adminHashedPassword);
		
		if (!success) {
			// s'il n'existe pas, envoyer une exception
			log.debug("User not found");
			throw new BadCredentialsException("Impossible de trouver le compte avec le login et mot de passe indiqués");
		} else {
			log.debug("User found");
		}

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(defaultRole));
		
		// renvoyer un retour à Spring pour dire que l'utilisateur s'est bien connecté.
		return new UsernamePasswordAuthenticationToken(useremail, password, authorities);
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return true;
	}

	public String getAdminLogin() {
		return adminLogin;
	}

	public void setAdminLogin(String adminLogin) {
		this.adminLogin = adminLogin;
	}

	public String getAdminHashedPassword() {
		return adminHashedPassword;
	}

	public void setAdminHashedPassword(String adminHashedPassword) {
		this.adminHashedPassword = adminHashedPassword;
	}

	public String getDefaultRole() {
		return defaultRole;
	}

}
