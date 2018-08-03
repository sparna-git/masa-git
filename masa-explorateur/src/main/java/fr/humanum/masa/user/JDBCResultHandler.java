package fr.humanum.masa.user;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface JDBCResultHandler {

	public void handleResultSet(ResultSet result) throws SQLException ;
}
