package fr.humanum.masa.federation;

import java.io.File;

import org.springframework.stereotype.Service;

import fr.humanum.masa.MasaException;

@Service
public class ExtConfigService {

	private String EXT_DIRECTORY_PROPERTY = "ext.directory";
	private String APPLICATION_QUERY_FILE = "query.json";
	private String APPLICATION_SOURCES_FILE="source.ttl";

	private String extPath;
	private File extFolder;

	public ExtConfigService() {
		if(System.getProperty(EXT_DIRECTORY_PROPERTY) == null) {
			throw new RuntimeException("System Property '"+EXT_DIRECTORY_PROPERTY+"' was not found. Please set this system property to point to the ext folder containing configuration files (java -D"+EXT_DIRECTORY_PROPERTY+"=/path/to/ext/folder ...).");
		}
		this.extPath = System.getProperty(EXT_DIRECTORY_PROPERTY);
		this.extFolder = new File(extPath);
		
		if(!this.extFolder.exists()) {
			throw new RuntimeException("Configured '"+EXT_DIRECTORY_PROPERTY+"' does not exist : "+extPath);
		}
	}

	public File getSourceFile(){
		return findMandatoryFile(APPLICATION_SOURCES_FILE);
	}

	public File getExampleQueriesFile(){
		return findFile(APPLICATION_QUERY_FILE);
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
