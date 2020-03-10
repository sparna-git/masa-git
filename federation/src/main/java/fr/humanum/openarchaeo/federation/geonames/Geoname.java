package fr.humanum.openarchaeo.federation.geonames;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geoname {

	protected String geonameId;
	protected String name;

	public String getGeonameId() {
		return geonameId;
	}

	public void setGeonameId(String geonameId) {
		this.geonameId = geonameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Geoname [geonameId=" + geonameId + "]";
	}
	
}
