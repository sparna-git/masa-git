package fr.humanum.masa.explorateur;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultParser;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultParserRegistry;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.springframework.stereotype.Service;

@Service
public class ExplorateurService {

	
	private Repository repository;

	
	public void getResult(String url, String queryExpand, OutputStream out) throws ClientProtocolException, IOException{
		
		if(repository == null) {
			this.repository = new SPARQLRepository(url);
			this.repository.initialize();
		}
		try(RepositoryConnection conn = repository.getConnection()) {
			
			   TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryExpand);
			   SPARQLResultsXMLWriter sparqlWriter = new SPARQLResultsXMLWriter(out);
			   tupleQuery.evaluate(sparqlWriter);
			}
//		HttpClient client = HttpClientBuilder.create().build();
//		HttpPost post = new HttpPost(url);
//		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//		urlParameters.add(new BasicNameValuePair("query", queryExpand));
//		post.setEntity(new UrlEncodedFormEntity(urlParameters));
//		HttpResponse response=client.execute(post);
//		return response.getEntity().getContent();
	}
	
	
}
