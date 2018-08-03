package fr.humanum.masa.user;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class MasaAuthenticationProvider implements AuthenticationProvider {

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@Autowired
	private UserDaoIfc userDao;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {


		// retrieve provided username and password
		String useremail = authentication.getName().trim();
		String password = (String) authentication.getCredentials();
		if (password.isEmpty()) {
			throw new BadCredentialsException("Mot de passe vide");
		}

		// vérifier si l'utilisateur existe avec ce login et mot de passe
		String useremailFromDB = userDao.login(useremail, password);
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (useremailFromDB == null) {
			// s'il n'existe pas, envoyer une exception
			log.debug("User not found");
			throw new BadCredentialsException("Impossible de trouver le compte avec le login et mot de passe indiqués");
		} else {
			log.debug("User found");
		}

		// lire les données de l'utilisateur
		User user = userDao.getUser(useremailFromDB);
		authorities.add(new SimpleGrantedAuthority(user.role));
		
		// renvoyer un retour à Spring pour dire que l'utilisateur s'est bien connecté.
		return new UsernamePasswordAuthenticationToken(useremail, password, authorities);
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return true;
	}


}
