package fr.humanum.openarchaeo.federation;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.springframework.stereotype.Service;

import fr.humanum.openarchaeo.OpenArchaeoException;

@Service("extConfigService")
public class ExtConfigService {

	public static String APPLICATION_PROPERTIES_FILE 	= "config.properties";
	
	private static String EXT_DIRECTORY_PROPERTY 		= "ext.directory.federation";
	private static String APPLICATION_QUERY_FILE 		= "query_examples.json";
	public static String QUERY_EXPANSION_CONFIG_FILE 	= "query_expansion.ttl";
	private static String APPLICATION_SOURCES_FILE		= "sources.ttl";
	public static String PERIODO_CONFIG_FILE 			= "periodo.jsonld";
	public static String COSTFED_CONFIG_FILE			= "costfed.properties";

	public static String LUCENE_INDEX_DIRECTORY			= "lucene.index.directory";
	public static String REFERENTIELS_REPOSITORY_URL	= "referentiels.repository.url";
	
	private File extFolder;
	private Properties properties;

	public ExtConfigService() {
		if(System.getProperty(EXT_DIRECTORY_PROPERTY) == null) {
			throw new RuntimeException("System Property '"+EXT_DIRECTORY_PROPERTY+"' was not found. Please set this system property to point to the ext folder containing configuration files (java -D"+EXT_DIRECTORY_PROPERTY+"=/path/to/ext/folder ...).");
		}
		String extPath = System.getProperty(EXT_DIRECTORY_PROPERTY);
		this.extFolder = new File(extPath);
		
		if(!this.extFolder.exists()) {
			throw new RuntimeException("Configured '"+EXT_DIRECTORY_PROPERTY+"' does not exist : "+extPath);
		}
		
		File f = findFile(APPLICATION_PROPERTIES_FILE);
		this.properties = new Properties();
		try {
			this.properties.load(new FileInputStream(f));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Properties getApplicationProperties() {
		return properties;
	}
	
	public String getLuceneIndexDirectory() {
		return getApplicationProperties().getProperty(LUCENE_INDEX_DIRECTORY);
	}

	public File getSourceFile(){
		return findMandatoryFile(APPLICATION_SOURCES_FILE);
	}
	
	public File getPeriodoFile(){
		return findMandatoryFile(PERIODO_CONFIG_FILE);
	}

	public File getExampleQueriesFile(){
		return findFile(APPLICATION_QUERY_FILE);
	}
	
	public File getCostfedFile(){
		return findMandatoryFile(COSTFED_CONFIG_FILE);
	}

	public File findFile(String path) {
		File environmentFile = new File(this.extFolder, path);
		if (environmentFile.exists()) {
			return environmentFile;
		} else {
			return null;
		}
	}
	
	public File findMandatoryFile(String path) {
		return OpenArchaeoException.failIfNull(findFile(path), "Required file was not found in ext.directory : "+path);		
	}


}
