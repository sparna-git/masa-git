package fr.humanum.openarchaeo.federation.index;

import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainPathRangeSparqlBuilder {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	protected IRI domain;
	protected String path;
	protected IRI range;
	
	public DomainPathRangeSparqlBuilder(IRI domain, String path, IRI range) {
		this.domain = domain;
		this.path = path;
		this.range = range;
	}
	
	/**
	 * Constructor without a range
	 * @param domain
	 * @param path
	 */
	public DomainPathRangeSparqlBuilder(IRI domain, String path) {
		this.domain = domain;
		this.path = path;
	}

	public String generateSparql() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT ?this"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  ?something a <"+domain.stringValue()+"> ."+"\n");
		sb.append("  ?something "+path+" ?this ."+"\n");
		if(this.range != null) {
			sb.append("  ?this a <"+range.stringValue()+"> ."+"\n");
		}
		sb.append("}");
		
		log.debug("Generated domain/path/range SPARQL query :\n"+sb.toString());
		
		return sb.toString();
	}
	
}
