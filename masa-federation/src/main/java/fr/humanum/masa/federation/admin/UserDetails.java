package fr.humanum.masa.federation.admin;

/**
 * Données d'un utilisateur enregistrées dans sa session
 * @author Thomas Francart
 */
public class UserDetails {

	protected String name;
	
	protected String email;
	
	protected String role;
	
	public UserDetails(String name, String email, String role) {
		this.name = name;
		this.email = email;
		this.role = role;
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
