package fr.humanum.masa.openarchaeo.federation.referentiels;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;

public interface IriHarvester {

	/**
	 * Harvests the given list of Iris to store them in the referential repository.
	 */
	public void harvest(List<IRI> iris);
	
}
