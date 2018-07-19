package fr.humanum.masa.federation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;

@Service
public class ExtConfigService {

  
  private Properties properties;

  private String extPath ;
  
  public ExtConfigService() {
	  this.extPath = System.getProperty("ext.directory");
  }

  public Properties getProperties(String fileName) throws FileNotFoundException, IOException{
	  if(properties==null){
		  File f=new File(extPath+"/"+fileName);
		  Properties p=new Properties();
		  p.load(new FileInputStream(f));
		  properties=p;
	  }
	  return properties;
  }
 
}
