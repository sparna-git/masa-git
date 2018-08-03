package fr.humanum.masa.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class JDBCQuerySet {

	public void querySet(DataSource dataSource, String requete){

		try(Connection connection = dataSource.getConnection()) { 
			Statement stmt = null;
			try
			{
				connection.setAutoCommit(false);
				stmt = connection.createStatement();
				stmt.executeUpdate(requete);
				connection.commit();

			} catch (SQLException e) {
				System.out.println("Exception Message " + e.getLocalizedMessage());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {

						e.printStackTrace();
					}
				}
			}
		}catch (SQLException e1) {
			e1.printStackTrace();

		}

	}

	public void querySelectHandler(DataSource dataSource, String requete, JDBCResultHandler handler){

		try(Connection connection = dataSource.getConnection()) { 
			Statement stmt = null;
			ResultSet result=null;
			try
			{
				connection.setAutoCommit(false);
				stmt = connection.createStatement();
				result=stmt.executeQuery(requete);
				while (result.next()) {
					handler.handleResultSet(result);
				}				
				connection.commit();

			} catch (SQLException e) {
				System.out.println("Exception Message " + e.getLocalizedMessage());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}catch (SQLException e1) {
			e1.printStackTrace();

		}

	}

}
