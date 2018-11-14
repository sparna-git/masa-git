package fr.humanum.masa.openarchaeo.federation.index;

import org.eclipse.rdf4j.model.IRI;

/**
 * A result of a search in the index.
 * @author thomas
 *
 */
public class SearchResult {

	protected IRI iri;
	protected String label;
	protected String prefLabel;
	
	public SearchResult(IRI iri, String label, String prefLabel) {
		super();
		this.iri = iri;
		this.label = label;
		this.prefLabel = prefLabel;
	}
	
	public IRI getIri() {
		return iri;
	}
	public void setIri(IRI iri) {
		this.iri = iri;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}

	@Override
	public String toString() {
		return "SearchResult [iri=" + iri + ", label=" + label + ", prefLabel=" + prefLabel + "]";
	}	
	
	
	
}
