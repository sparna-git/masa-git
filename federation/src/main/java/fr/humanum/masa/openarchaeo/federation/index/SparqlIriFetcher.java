package fr.humanum.masa.openarchaeo.federation.index;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.humanum.masa.openarchaeo.Perform;

/**
 * Abstract class that executes a SPARQL query to
 * @author thomas
 *
 */
public class SparqlIriFetcher implements IriFetcher {

	protected String sparql;
	
	public SparqlIriFetcher(String sparql) {
		super();
		this.sparql = sparql;
	}

	@Override
	public List<IRI> fetchIrisToIndex(Repository repository) {
		List<IRI> result = new ArrayList<>();
		
		try(RepositoryConnection c = repository.getConnection()) {
			Perform.on(c).query(this.sparql, new AbstractTupleQueryResultHandler() {
				@Override
				public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
					Binding b = bindingSet.getBinding(bindingSet.getBindingNames().iterator().next());
					result.add((IRI)b.getValue());
				}
			});
		}
		
		return result;
	}

	public String getSparql() {
		return sparql;
	}

	public void setSparql(String sparql) {
		this.sparql = sparql;
	}

}
