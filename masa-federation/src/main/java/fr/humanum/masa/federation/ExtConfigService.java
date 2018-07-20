package fr.humanum.masa.federation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;

@Service
public class ExtConfigService {

  private String EXT_DIRECTORY_PROPERTY = "ext.directory";
  private String APPLICATION_PROPERTIES_FILE = "config.properties";
  
  private String extPath;
  private Properties properties;
  
  public ExtConfigService() {
	  if(System.getProperty(EXT_DIRECTORY_PROPERTY) == null) {
		  throw new RuntimeException("System Property '"+EXT_DIRECTORY_PROPERTY+"' was not found. Please set this system property to point to the ext folder containing configuration files (java -D"+EXT_DIRECTORY_PROPERTY+"=/path/to/ext/folder ...).");
	  }
	  this.extPath = System.getProperty(EXT_DIRECTORY_PROPERTY);
  }

  public Properties getApplicationProperties() throws FileNotFoundException, IOException {
	  if(properties==null){
		  File f=new File(extPath+"/"+APPLICATION_PROPERTIES_FILE);
		  this.properties = new Properties();
		  this.properties.load(new FileInputStream(f));
	  }
	  return properties;
  }
 
}
