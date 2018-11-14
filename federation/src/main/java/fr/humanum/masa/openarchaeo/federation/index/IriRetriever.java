package fr.humanum.masa.openarchaeo.federation.index;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.humanum.masa.openarchaeo.federation.referentiels.IriHarvester;

public class IriRetriever implements IriFetcher {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	protected IriFetcher delegate;
	protected IriHarvester harvester;
	
	public IriRetriever(IriFetcher delegate, IriHarvester harvester) {
		super();
		this.delegate = delegate;
		this.harvester = harvester;
	}

	@Override
	public List<IRI> fetchIrisToIndex(Repository repository) {
		List<IRI> result = this.delegate.fetchIrisToIndex(repository);
		// harvests the IRI for which we need to read a label
		this.harvester.harvest(result);
		return result;
	}
	
}
