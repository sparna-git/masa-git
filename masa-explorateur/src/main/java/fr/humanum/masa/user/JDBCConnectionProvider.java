package fr.humanum.masa.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * !! PLUS UTILISEE !!
 * 
 * Classe permettant d'établir la connexion avec la base relationnelle
 * @author clarvie
 */
public class JDBCConnectionProvider {
   
   private static final String DB_DRIVER = "org.h2.Driver";
   
   private String jdbcPath;
   private String jdbcUser = "";
   private String jdbcPassword = "";

   public JDBCConnectionProvider(String jdbcPath) {
	   this.jdbcPath = jdbcPath;
   }
   
   public Connection getDBConnection() {
	   try {
		   Class.forName(DB_DRIVER);
	   } catch (ClassNotFoundException e) {
		   e.printStackTrace();
		   throw new RuntimeException(e);
	   }
	   
	   try {

//		   String INIT_SQL = "CREATE TABLE IF NOT EXISTS user ("
//		   						+ "id INT AUTO_INCREMENT, "
//		   						+ "username VARCHAR(255) UNIQUE NOT NULL, "
//		   						+ "email VARCHAR(255) UNIQUE NOT NULL, "
//		   						+ "password VARCHAR(255) NOT NULL, "
//		   						+ "espace VARCHAR(255) NOT NULL,"
//		   						+ "admin VARCHAR(255) NOT NULL, "
//		   						+ "PRIMARY KEY (id)"
//		   					+ ")\\;"
//		   					+ "CREATE TABLE IF NOT EXISTS user_role ("
//		   						+ "id INT AUTO_INCREMENT, "
//		   						+ "username VARCHAR(255) UNIQUE NOT NULL, "
//		   						+ "role VARCHAR(255), "
//		   						+ "PRIMARY KEY (id)"
//		   					+ ") ";
//		   String fullJdbcUrl = jdbcPath+";INIT="+INIT_SQL;
		   
		   String fullJdbcUrl = jdbcPath;
		   return DriverManager.getConnection(fullJdbcUrl, jdbcUser, jdbcPassword);		   
	   } catch (SQLException e) {
		  e.printStackTrace();
		  throw new RuntimeException(e);
	   }
   }

}
