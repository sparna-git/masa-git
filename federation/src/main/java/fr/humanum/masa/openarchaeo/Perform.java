package fr.humanum.masa.openarchaeo;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Perform {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 

	protected RepositoryConnection connection;

	public Perform(RepositoryConnection connection) {
		super();
		this.connection = connection;
	}
	
	public static Perform on(RepositoryConnection c) {
		return new Perform(c);
	}
	
	
	
	public void query(String sparql, TupleQueryResultHandler handler, Map<String, Value> bindings) {
		log.debug("Sending SPARQL query :\n"+sparql+"\nWith bindings : "+bindings);
		TupleQuery q = this.connection.prepareTupleQuery(sparql);
		if(bindings != null) {
			for (Map.Entry<String, Value> aBinding : bindings.entrySet()) {
				q.setBinding(aBinding.getKey(), aBinding.getValue());
			}
		}
		
		q.evaluate(handler);
	}
	
	public void query(String sparql, TupleQueryResultHandler handler) {
		query(sparql, handler, null);
	}
	
	public void query(String sparql, TupleQueryResultHandler handler, String bindingKey, Value bindingValue) {
		query(sparql, handler, new HashMap<String, Value>() {{ put(bindingKey, bindingValue); }});
	}
	
	public Model construct(String sparql, Map<String, Value> bindings) {
		log.debug("Sending SPARQL query :\n"+sparql+"\nWith bindings : "+bindings);
		GraphQuery q = this.connection.prepareGraphQuery(sparql);
		if(bindings != null) {
			for (Map.Entry<String, Value> aBinding : bindings.entrySet()) {
				q.setBinding(aBinding.getKey(), aBinding.getValue());
			}
		}
		
		StatementCollector collector = new StatementCollector();
		q.evaluate(collector);
		return new LinkedHashModel(collector.getStatements());
	}
	
	public void update(String sparql, Map<String, Value> bindings) {
		log.debug("Sending SPARQL update :\n"+sparql+"\nWith bindings : "+bindings);
		Update u = this.connection.prepareUpdate(sparql);
		if(bindings != null) {
			for (Map.Entry<String, Value> aBinding : bindings.entrySet()) {
				u.setBinding(aBinding.getKey(), aBinding.getValue());
			}
		}
		
		u.execute();
	}
	
	public void update(String sparql, String bindingKey, Value bindingValue) {
		update(sparql, new HashMap<String, Value>() {{ put(bindingKey, bindingValue); }});
	}
	
	public void update(String sparql) {
		this.update(sparql, null);
	}
	
}
