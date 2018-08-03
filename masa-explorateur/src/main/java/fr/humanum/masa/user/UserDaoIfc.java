package fr.humanum.masa.user;

import java.util.List;

public interface UserDaoIfc {
	
	public void createUser(User entry);
	
	public List<User> listAlluser();
	
	public void deleteUser(String nom);

	public void updateUser(String nom, User user);
	
	public User getUser(String nom);
	
	/**
	 * Returns the username
	 * 
	 * @param nom
	 * @param clearPassword
	 * @return
	 */
	public String login(String email, String clearPassword);
}
