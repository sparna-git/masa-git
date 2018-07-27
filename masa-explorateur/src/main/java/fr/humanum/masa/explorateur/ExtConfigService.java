package fr.humanum.masa.explorateur;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;

import fr.humanum.masa.MasaException;

@Service
public class ExtConfigService {

	public static String EXT_DIRECTORY_PROPERTY = "ext.directory";
	public static String APPLICATION_PROPERTIES_FILE = "config_explorateur.properties";
	public static String QUERY_EXPANSION_CONFIG_FILE = "query_expansion.ttl";


	public static String FEDERATION_SERVICE_URL = "federation.service.url";
	public static String FEDERATION_SERVICE_API_SOURCES = "federation.service.api.sources";
	
	private File extFolder;
	private Properties properties;

	public ExtConfigService() throws IOException {
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
		this.properties.load(new FileInputStream(f));
	}

	public Properties getApplicationProperties() {
		return properties;
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
		return MasaException.failIfNull(findFile(path), "Required file was not found in ext.directory : "+path);		
	}

}
