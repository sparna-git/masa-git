package fr.humanum.openarchaeo.federation.index;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.query.BindingSet;

/**
 * A Label from the RDF data.
 * @author thomas
 *
 */
public class LabelEntry {

	protected IRI iri;
	protected Literal label;
	protected Literal prefLabel;
	
	public static LabelEntry fromBindingSet(BindingSet bindingSet) {
		LabelEntry entry = new LabelEntry();
		entry.setIri((IRI)bindingSet.getBinding("iri").getValue());
		entry.setLabel((Literal)bindingSet.getBinding("label").getValue());
		if(bindingSet.hasBinding("prefLabel")) {
			entry.setPrefLabel((Literal)bindingSet.getBinding("prefLabel").getValue());
		}
		return entry;
	}
	
	public IRI getIri() {
		return iri;
	}
	public void setIri(IRI iri) {
		this.iri = iri;
	}
	public Literal getLabel() {
		return label;
	}
	public void setLabel(Literal label) {
		this.label = label;
	}
	public Literal getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(Literal prefLabel) {
		this.prefLabel = prefLabel;
	}

	@Override
	public String toString() {
		return "LabelEntry [iri=" + iri + ", label=" + label + ", prefLabel=" + prefLabel + "]";
	}
}
