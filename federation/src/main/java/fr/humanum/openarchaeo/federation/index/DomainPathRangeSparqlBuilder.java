package fr.humanum.openarchaeo.federation.index;

import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainPathRangeSparqlBuilder {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	protected IRI domain;
	protected String path;
	protected IRI range;
	protected boolean autoFixGeonamesUris = false;
	
	public DomainPathRangeSparqlBuilder(IRI domain, String path, IRI range, boolean autoFixGeonamesUris) {
		this.domain = domain;
		this.path = path;
		this.range = range;
		this.autoFixGeonamesUris = autoFixGeonamesUris;
	}
	
	/**
	 * Constructor without a range
	 * @param domain
	 * @param path
	 */
	public DomainPathRangeSparqlBuilder(IRI domain, String path, boolean autoFixGeonamesUris) {
		this.domain = domain;
		this.path = path;
		this.autoFixGeonamesUris = autoFixGeonamesUris;
	}

	public String generateSparql() {
		StringBuffer sb = new StringBuffer();
		
//		if(this.autoFixGeonamesUris) {
//			sb.append("SELECT DISTINCT ( IF( STRSTARTS(STR(?this), 'http://sws.geonames.org/'), IRI(CONCAT('https://sws.geonames.org/', STRAFTER(STR(?this), 'http://sws.geonames.org/'), '/')), ?this) AS ?fixedThis)"+"\n");
//		} else {
//			sb.append("SELECT DISTINCT ?this"+"\n");
//		}
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
