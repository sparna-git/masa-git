package fr.humanum.masa.explorateur;

import java.io.OutputStream;

import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.springframework.stereotype.Service;

@Service
public class ExplorateurService {
	
	public void getResult(String url, String queryExpand, OutputStream out) {

		Repository repository = new SPARQLRepository(url);
		repository.initialize();
			
		try(RepositoryConnection conn = repository.getConnection()) {
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryExpand);
			SPARQLResultsXMLWriter sparqlWriter = new SPARQLResultsXMLWriter(out);
			tupleQuery.evaluate(sparqlWriter);
		}
	}
	
	
}
