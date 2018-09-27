package fr.humanum.masa.federation.index;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;

public class RdfsLabelFetcher extends BatchSparqlLabelFetcher implements LabelFetcher {

	protected String lang;
	
	public RdfsLabelFetcher(String lang) {
		super();
		this.lang = lang;
	}

	@Override
	protected String generateSparqlFetch(List<IRI> iris) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n");
		sb.append("SELECT ?iri ?label ?prefLabel"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  ?iri rdfs:label ?label . FILTER(lang(?label) = '"+lang+"') BIND(?label AS ?prefLabel)"+"\n");
		sb.append("  VALUES ?iri {");
		for (IRI iri : iris) {
			sb.append("<"+iri.stringValue()+"> ");
		}
		sb.append("}"+"\n");
		sb.append("}"+"\n");
		
		return sb.toString();
	}

	
	
}
