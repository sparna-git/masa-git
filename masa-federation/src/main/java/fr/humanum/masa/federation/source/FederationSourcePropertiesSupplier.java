package fr.humanum.masa.federation.source;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;

public class FederationSourcePropertiesSupplier implements Supplier<Set<FederationSource>> {

	private Properties properties;
	
	public FederationSourcePropertiesSupplier(Properties properties) {
		super();
		this.properties = properties;
	}

	@Override
	public Set<FederationSource> get() {
		String endpointUrl = properties.getProperty("masa.endpoint.url");
		
		// get end point url property and foreach we create a repository and save it in a federation object
		System.out.println("Init repository with federation Get endpoint url...");
		String[] listEndPointUrl = endpointUrl.trim().split("[,]");
		
		Set<FederationSource> result = new HashSet<FederationSource>();
		for (String aEndpointUrl : listEndPointUrl) {
			result.add(new SimpleFederationSource(aEndpointUrl, null, aEndpointUrl));
		}
		
		return result;
	}

	
	
}
