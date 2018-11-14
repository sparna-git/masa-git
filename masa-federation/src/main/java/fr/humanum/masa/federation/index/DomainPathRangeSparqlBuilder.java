package fr.humanum.masa.federation.index;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.humanum.masa.Perform;

public class DomainPathRangeIriFetcher implements IriFetcher {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	protected IRI domain;
	protected String path;
	protected IRI range;
	
	public DomainPathRangeIriFetcher(IRI domain, String path, IRI range) {
		super();
		this.domain = domain;
		this.path = path;
		this.range = range;
	}

	@Override
	public List<IRI> fetchLabels(Repository repository) {
		List<IRI> result = new ArrayList<>();
		
		try(RepositoryConnection c = repository.getConnection()) {
			Perform.on(c).query(this.generateSparql(), new AbstractTupleQueryResultHandler() {
				@Override
				public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
					Binding b = bindingSet.getBinding(bindingSet.getBindingNames().iterator().next());
					result.add((IRI)b.getValue());
				}
			});
		}
		
		return result;
	}
	
	public String generateSparql() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DISTINCT ?this"+"\n");
		sb.append("WHERE {"+"\n");
		sb.append("  ?something a <"+this.domain.stringValue()+"> ."+"\n");
		sb.append("  ?something "+this.path+" ?this ."+"\n");
		sb.append("  ?this a <"+this.range.stringValue()+"> ."+"\n");
		sb.append("}");
		
		return sb.toString();
	}
	
}
