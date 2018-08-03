package fr.humanum.masa.user;

/**
 * Données d'un utilisateur enregistrées dans sa session
 * @author Thomas Francart
 */
public class UserDetails {

	protected String name;
	
	protected String email;
	
	protected String espaceUri;
	
	protected String espaceName;
	
	protected String role;
	
	public UserDetails(User user) {
		this.name = user.getName();
		this.email = user.getEmail();
		this.role = user.getRole();
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserDetails [name=" + name + ", email=" + email + ", superadmin=" + role + "]";
	}
	
	
}
