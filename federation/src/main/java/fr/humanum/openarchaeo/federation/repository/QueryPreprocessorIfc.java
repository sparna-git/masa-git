package fr.humanum.openarchaeo.federation.repository;

import java.util.List;

import org.eclipse.rdf4j.query.TupleQuery;

public interface QueryPreprocessorIfc {

	public TupleQuery preproces(TupleQuery tq, String originalQuery, List<String> endpoints);
	
}
