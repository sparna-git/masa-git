package fr.humanum.masa.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.humanum.masa.Utils;

/**
 * Class d'accès aux données pour une lecture/écriture dans la  base 
 * @author clarvie
 *
 */

public class UserDaoImpl implements UserDaoIfc, UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	protected DataSource dataSource;
	
	
	public UserDaoImpl(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
		
	}
	
	//creer un utilisateur
	@Override
	public void createUser(User entry){

		String requete="INSERT INTO USER(username, email, password, role) VALUES('"
				+entry.name+"','"
				+entry.email+"','"
				+entry.mdp+"','"
				+entry.role+"')";
		JDBCQuerySet query=new JDBCQuerySet();
		query.querySet(dataSource, requete);
	}
	
	//supprimer un utilisateur
	@Override
	public void deleteUser(String email) {
		// TODO Auto-generated method stub
		String requete="DELETE from USER where email='"+email+"'";
		JDBCQuerySet query=new JDBCQuerySet();
		query.querySet(dataSource, requete);
	}
	//Mettre à jour un utilisateur
	@Override
	public void updateUser(String email, User user) {
		// TODO Auto-generated method stub
		String requete=null;
		if(user.mdp!=null){
			requete="UPDATE USER SET  password='"+user.mdp+"' where email='"+email+"'";
		}else{
			requete="UPDATE USER SET username='"+user.name+"', role='"+user.role+"' where email='"+email+"'";
		}
		JDBCQuerySet query=new JDBCQuerySet();
		query.querySet(dataSource, requete);

	}
	
	//Lister tous les utilisateurs
	@Override
	public List<User> listAlluser() {

		ArrayList<User> ListeRetour = new ArrayList <User>();
		String requete="SELECT username, email, role from USER";
		JDBCQuerySet query=new JDBCQuerySet();
		query.querySelectHandler(dataSource, requete, new JDBCResultHandler() {			
			@Override
			public void handleResultSet(ResultSet resultSet) throws SQLException {
					User user=new User();
					user.setName(resultSet.getString("username"));
					user.setEmail(resultSet.getString("email"));
					user.setRole(resultSet.getString("role"));
					ListeRetour.add(user);
					
			}

		});
		return ListeRetour;		

	}
	
	// Récupérer les données d'un utilisateur à partir de son email
	 
	@Override
	public User getUser(String email) {		
		String requete=null;
		User user=new User();
		requete="SELECT username, email, role from USER where email='"+email+"'";
		JDBCQuerySet query=new JDBCQuerySet();
		query.querySelectHandler(dataSource, requete, new JDBCResultHandler() {			
			@Override
			public void handleResultSet(ResultSet resultSet) throws SQLException {
					user.setName(resultSet.getString("username"));
					user.setEmail(resultSet.getString("email"));
					user.setRole(resultSet.getString("role"));
	
			}

		});
		
		

		return user;			
	}


	@Override
	public String login(String email, String clearPassword) {

		String hashedPass = Utils.hashPassword(clearPassword);

		String request="select email from user where email='"+email+"' and password='"+hashedPass+"'";
		log.debug("Trying to find account, email:[{}], password:[{}]", email, hashedPass);

		String result=null;
		try(Connection connection = dataSource.getConnection()) { 
			try(Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(request);
				while (rs.next()) {
					result=rs.getString("email");
				}			
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	// on rend la variable comme une variable de classe pour pouvoir y accéder depuis la classe anonyme
	UserDetails userDetails = null;
	@Override
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		String requete = "SELECT username, password, email, role from USER where email='"+email+"'";
		JDBCQuerySet query = new JDBCQuerySet();

		query.querySelectHandler(dataSource, requete, new JDBCResultHandler() {			
			@Override
			public void handleResultSet(ResultSet resultSet) throws SQLException {
				UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(resultSet.getString("email"));		
				userDetails = userBuilder.authorities(resultSet.getString("role")).disabled(false).accountExpired(false).accountLocked(false).credentialsExpired(false).build();
			}

		});
		return userDetails;		
	}

}
