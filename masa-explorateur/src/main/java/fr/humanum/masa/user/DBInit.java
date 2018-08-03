package fr.humanum.masa.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

public class DBInit {

	protected DataSource dataSource;
	protected List<String> sqlCommands;
	
	public DBInit(DataSource dataSource, List<String> sqlCommands) {
		super();
		this.dataSource = dataSource;
		this.sqlCommands = sqlCommands;
	}

	@EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
		System.out.println("ContextRefreshedEvent received");
        this.initDB();
    }
	
	public void initDB() {
		try(Connection connection = dataSource.getConnection()) {			
			try(Statement batch = connection.createStatement()) {
				for (String aCommand : sqlCommands) {
					System.out.println(aCommand);
					batch.addBatch(aCommand);
				}
				batch.executeBatch();
				System.out.println("DB Initialisation done.");
			}			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
	}	
	
}
