package fr.humanum.openarchaeo.federation.geonames;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.humanum.openarchaeo.federation.source.SimpleFederationSource;

public class SimpleFederationSourceGeonamesExpansion implements UnaryOperator<SimpleFederationSource> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public SimpleFederationSource apply(SimpleFederationSource source) {
		List<Value> spatials = source.getDcterms().get(DCTERMS.SPATIAL);
		List<Value> newSpatials = new ArrayList<Value>();
		
		if(spatials != null) {
			spatials.stream().forEach(s -> {
					if(s instanceof IRI) {
						IRI sourceIri = (IRI)s;
						String sourceIriString = sourceIri.toString();
						if(sourceIriString.startsWith("http://sws.geonames.org") || sourceIriString.startsWith("https://sws.geonames.org") ) {
							log.debug("Will process Geonames reference "+sourceIriString);
							String geonameId = sourceIriString.substring(sourceIri.toString().lastIndexOf("/", sourceIriString.length()-2)+1, sourceIriString.length()-1);
							log.debug("Extracted geonameId "+geonameId);
							
							String geonamesHierarchyApiUrl = "http://api.geonames.org/hierarchyJSON?geonameId="+geonameId+"&username=openarchaeo";
							log.debug("Will call Geonames API "+geonamesHierarchyApiUrl);
							try {
								GeonamesHierarchyResult geonamesResult = mapper.readValue(new URL(geonamesHierarchyApiUrl), GeonamesHierarchyResult.class);
								
								for(int i=0;i < geonamesResult.geonames.size();i++) {
									// skip the first one, "Earth"
									if(i != 0) {
										Geoname g = geonamesResult.geonames.get(i);
										log.debug("Inserting value '"+g.getName()+"'");
										newSpatials.add(SimpleValueFactory.getInstance().createLiteral(g.getName()));
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						// not an IRI, we don't process it anymore to avoid duplicates if the same value
						// is inserted as both URI and Literal
						// newSpatials.add(s);
					}
			});
			
			source.getDcterms().put(DCTERMS.SPATIAL, newSpatials);
		}
		
		return source;
	}

	
	
	
}
