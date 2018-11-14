package fr.humanum.masa.openarchaeo.federation.index;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;

/**
 * Fetches SKOS label in a different repository using the SERVICE clause. This is meant to fetch the referential labels
 * that are stored in another repository.
 * 
 * @author thomas
 *
 */
public class SkosServiceLabelFetcher extends BatchSparqlLabelFetcher implements LabelFetcher {

	protected String lang;
	protected String service;
	
	public SkosServiceLabelFetcher(String service) {
		this(service, null);
	}
	
	public SkosServiceLabelFetcher(String service, String lang) {
		super();
		this.service = service;
		this.lang = lang;
	}

	@Override
	protected String generateSparqlFetch(List<IRI> iris) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+"\n");
		sb.append("SELECT ?iri ?label ?prefLabel"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  SERVICE <"+service+"> {"+"\n");
		sb.append("    { ?iri skos:altLabel ?label . ?iri skos:prefLabel ?prefLabel . " + ((this.lang != null)?"FILTER(lang(?label) = '"+lang+"' && lang(?prefLabel) = '"+lang+"')":"")+" }"+"\n");
		sb.append("    UNION"+"\n");
		sb.append("    { ?iri skos:prefLabel ?label . " +((this.lang != null)?"FILTER(lang(?label) = '"+lang+"')":"")+" }"+"\n");
		sb.append("    VALUES ?iri {");
		for (IRI iri : iris) {
			sb.append("<"+iri.stringValue()+"> ");
		}
		sb.append("}"+"\n");
		sb.append("  }"+"\n"); // end SERVICE clause
		sb.append("}"+"\n");
		
		return sb.toString();
	}

	
	
}
