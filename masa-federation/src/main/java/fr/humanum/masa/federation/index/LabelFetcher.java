package fr.humanum.masa.federation.index;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;

/**
 * Reads the labels of the provided IRI
 * @author thomas
 *
 */
public interface LabelFetcher {

	/**
	 * Fetches the labels of the provided IRIs in the given repository, and send them to the provided LabelEntryHandler
	 * @param iris
	 * @param repository
	 * @param handler
	 */
	public void fetchLabels(List<IRI> iris, Repository repository, LabelEntryHandler handler);
	
}
