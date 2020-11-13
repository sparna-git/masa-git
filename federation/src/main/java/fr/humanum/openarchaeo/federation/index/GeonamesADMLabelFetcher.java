package fr.humanum.openarchaeo.federation.index;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;

/**
 *
 */
public class GeonamesADMLabelFetcher extends BatchSparqlLabelFetcher implements LabelFetcher {

	protected String lang;
	protected String service;
	
	public GeonamesADMLabelFetcher(String service) {
		this(service, "fr");
	}
	
	public GeonamesADMLabelFetcher(String service, String lang) {
		super();
		this.service = service;
		this.lang = lang;
	}

	@Override
	protected String generateSparqlFetch(List<IRI> iris) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+"\n");
		sb.append("PREFIX gn: <http://www.geonames.org/ontology#>"+"\n");
		sb.append("SELECT DISTINCT ?iri ?label ?prefLabel"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  SERVICE <"+service+"> {"+"\n");
		sb.append("    ?iri gn:parentCountry/gn:name ?countryLabel ."+"\n");
		sb.append("    OPTIONAL { ?iri gn:parentADM1/gn:name ?adm1 . } BIND(IF(BOUND(?adm1),CONCAT(' > ', ?adm1),'') AS ?adm1Label)"+"\n");
		sb.append("    OPTIONAL { ?iri gn:parentADM2/gn:name ?adm2 . } BIND(IF(BOUND(?adm2),CONCAT(' > ', ?adm2),'') AS ?adm2Label)"+"\n");
		sb.append("    OPTIONAL { ?iri gn:parentADM3/gn:name ?adm3 . } BIND(IF(BOUND(?adm3),CONCAT(' > ', ?adm3),'') AS ?adm3Label)"+"\n");
		sb.append("    OPTIONAL { ?iri gn:parentADM4/gn:name ?adm4 . } BIND(IF(BOUND(?adm4),CONCAT(' > ', ?adm4),'') AS ?adm4Label)"+"\n");
		sb.append("    BIND(CONCAT(?countryLabel, ?adm1Label, ?adm2Label, ?adm3Label, ?adm4Label) AS ?label)"+"\n");
		sb.append("    VALUES ?iri {");
		for (IRI iri : iris) {
			// sb.append("<"+ReferentielRepositoryIriHarvester.toGeonamesHttpsIri(iri)+"> ");
			sb.append("<"+iri+"> ");
		}
		sb.append("}"+"\n");
		sb.append("  }"+"\n"); // end SERVICE clause
		sb.append("}"+"\n");

		return sb.toString();
	}

	
}
