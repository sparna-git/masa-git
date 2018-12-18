package fr.humanum.openarchaeo.federation.index;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;

public interface IriFetcher {

	/**
	 * Fetches the IRIs to index from the provided repository;
	 * 
	 * @param repository
	 * @return
	 */
	public List<IRI> fetchIrisToIndex(Repository repository);
	
}
