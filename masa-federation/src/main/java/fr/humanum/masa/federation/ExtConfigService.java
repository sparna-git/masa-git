package fr.humanum.masa.federation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.humanum.masa.federation.source.FederationSparqlQueriesExample;

@Service
public class ExtConfigService {

	private String EXT_DIRECTORY_PROPERTY = "ext.directory";
	private String APPLICATION_QUERY_FILE = "query.json";
	private String APPLICATION_SOURCES_FILE="source.ttl";

	private String extPath;
	private List<FederationSparqlQueriesExample> queries;

	public ExtConfigService() {
		if(System.getProperty(EXT_DIRECTORY_PROPERTY) == null) {
			throw new RuntimeException("System Property '"+EXT_DIRECTORY_PROPERTY+"' was not found. Please set this system property to point to the ext folder containing configuration files (java -D"+EXT_DIRECTORY_PROPERTY+"=/path/to/ext/folder ...).");
		}
		this.extPath = System.getProperty(EXT_DIRECTORY_PROPERTY);
	}

	public List<FederationSparqlQueriesExample> getApplicationQueries() throws FileNotFoundException, IOException {
		if(queries==null){
			byte[] mapData = Files.readAllBytes(Paths.get(extPath+"/"+APPLICATION_QUERY_FILE));

			ObjectMapper objectMapper=new ObjectMapper();
			//add this line  
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);    
			List<FederationSparqlQueriesExample> listOfQueries = objectMapper.readValue(mapData , new TypeReference<List<FederationSparqlQueriesExample>>(){});
			this.queries=listOfQueries;
		}
		return queries;
	}

	public File getSourceFile(){
		return new File(extPath+"/"+APPLICATION_SOURCES_FILE);
	}

	public String getQueriesFilePath(){
		return (extPath+"/"+APPLICATION_QUERY_FILE);
	}



}
