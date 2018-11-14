package fr.humanum.masa.openarchaeo.federation.index;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;

public class CidocCrmAppellationLabelFetcher extends BatchSparqlLabelFetcher implements LabelFetcher {

	protected String lang;
	
	public CidocCrmAppellationLabelFetcher() {
		this(null);
	}
	
	public CidocCrmAppellationLabelFetcher(String lang) {
		super();
		this.lang = lang;
	}

	@Override
	protected String generateSparqlFetch(List<IRI> iris) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("PREFIX crm: <http://www.cidoc-crm.org/cidoc-crm/>"+"\n");
		sb.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+"\n");
		sb.append("SELECT ?iri ?label ?prefLabel"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  ?iri (crm:P1_is_identified_by|crm:P87_is_identified_by|crm:P131_is_identified_by)/rdfs:label ?label ."+"\n");
		if(this.lang != null) {
			sb.append("  FILTER(lang(?label) = '"+lang+"')"+"\n");
		}
		sb.append("  BIND(?label AS ?prefLabel)"+"\n");
		sb.append("  VALUES ?iri {");
		for (IRI iri : iris) {
			sb.append("<"+iri.stringValue()+"> ");
		}
		sb.append("}"+"\n");
		sb.append("}"+"\n");
		
		return sb.toString();
	}

	
	
}
