package fr.humanum.masa.openarchaeo.federation.index;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;

public class SkosLabelFetcher extends BatchSparqlLabelFetcher implements LabelFetcher {

	protected String lang;
	
	public SkosLabelFetcher() {
	}
	
	public SkosLabelFetcher(String lang) {
		super();
		this.lang = lang;
	}

	@Override
	protected String generateSparqlFetch(List<IRI> iris) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+"\n");
		sb.append("SELECT ?iri ?label ?prefLabel"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  { ?iri skos:altLabel ?label . ?iri skos:prefLabel ?prefLabel . "+((this.lang != null)? "FILTER(lang(?label) = '"+lang+"' && lang(?prefLabel) = '"+lang+"')":"")+"}"+"\n");
		sb.append("  UNION"+"\n");
		sb.append("  { ?iri skos:prefLabel ?label . " +((this.lang != null)?"FILTER(lang(?label) = '"+lang+"')":"")+"}"+"\n");
		sb.append("  VALUES ?iri {");
		for (IRI iri : iris) {
			sb.append("<"+iri.stringValue()+"> ");
		}
		sb.append("}"+"\n");
		sb.append("}"+"\n");
		
		return sb.toString();
	}

	
	
}
