package fr.humanum.openarchaeo.federation.geonames;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeonamesHierarchyResult {

	protected List<Geoname> geonames;
	
	public List<Geoname> getGeonames() {
		return geonames;
	}
	
	public void setGeonames(List<Geoname> geonames) {
		this.geonames = geonames;
	}

	@Override
	public String toString() {
		return "GeonamesHierarchyResult [geonames=" + geonames + "]";
	}
	
}
