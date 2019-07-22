package fr.humanum.openarchaeo.federation.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.util.FileUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FederationSourceRdfSupplier implements Supplier<List<FederationSource>>{

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private File sourceDefinitionFile;


	public FederationSourceRdfSupplier(File sourceDefinitionFile){
		this.sourceDefinitionFile=sourceDefinitionFile;
	}

	private Model getModel() throws FileNotFoundException{
		InputStream in=new FileInputStream(sourceDefinitionFile);
		Model model=ModelFactory.createDefaultModel();
		model.read(in,null, FileUtils.guessLang(sourceDefinitionFile.getName(), Lang.RDFXML.getName()));
		return model;
	}

	@Override
	public List<FederationSource> get() {

		String queryString = "prefix sd: <http://www.w3.org/ns/sparql-service-description#>"+
				             "prefix void: <http://rdfs.org/ns/void#>"+ 
				             "prefix fed-config: <https://masa.hypotheses.org/federation/config#>"+ 
						     "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+

							 "select ?graph ?sourceIRI ?endpoint ?defaultGraph"+
							 " where {"+
									" ?graph a sd:NamedGraph ;"+
									" 	sd:name ?sourceIRI;"+
									" 	void:sparqlEndpoint ?endpoint;"+
									" OPTIONAL{	?graph fed-config:sparqlGraph ?defaultGraph. } "+
							" }";
										
		Query query = QueryFactory.create(queryString) ;
		List<FederationSource> result = new ArrayList<FederationSource>();
		

		Model m = null;
		try {
			m = getModel();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try (QueryExecution qexec = QueryExecutionFactory.create(query, m)) {
			ResultSet results = qexec.execSelect() ;
			
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution() ;   
				log.debug("Reading a source result : "+soln);
				
				// sourceIRI
				Resource rSource=soln.getResource("sourceIRI");
				IRI source = SimpleValueFactory.getInstance().createIRI(rSource.getURI());				
				
				// endpoint
				IRI endpoint = SimpleValueFactory.getInstance().createIRI(soln.getResource("endpoint").getURI()) ;
				
				// default graph
				IRI defaultGraph =null;
				if(soln.getResource("defaultGraph")!=null){
					defaultGraph = SimpleValueFactory.getInstance().createIRI(soln.getResource("defaultGraph").getURI()) ;
				}
				
				SimpleFederationSource sfs=new SimpleFederationSource(source, endpoint, defaultGraph);
				
				// now query for the DCTerms description
				String dctermsQueryString = 
						 "PREFIX sd: <http://www.w3.org/ns/sparql-service-description#>"+"\n"+
					     "PREFIX dcterms: <http://purl.org/dc/terms/> "+"\n"+
						 "SELECT ?dcProperty ?dcValue"+"\n"+
						 "WHERE {"+"\n"+
						 "   ?graph sd:name <"+rSource.getURI()+"> ."+"\n"+
						 "   ?graph ?dcProperty ?dcValue . "+"\n"+
						 "   FILTER(STRSTARTS(STR(?dcProperty), \"http://purl.org/dc/terms/\"))"+"\n"+
						 "} ORDER BY ?dcProperty ?dcValue";									
				
				Query dctermsQuery = QueryFactory.create(dctermsQueryString) ;
				try(QueryExecution dctermsQExec = QueryExecutionFactory.create(dctermsQuery, m)) {
					ResultSet resultsDcTerms = dctermsQExec.execSelect();
					Map<IRI, List<org.eclipse.rdf4j.model.Value>> dcterms = new HashMap<>();
					while (resultsDcTerms.hasNext()) {
						QuerySolution dcSolution = resultsDcTerms.nextSolution() ;
						log.debug("  Reading a DC property : "+dcSolution);
						// DC property
						IRI dcProperty = SimpleValueFactory.getInstance().createIRI(dcSolution.getResource("dcProperty").getURI());
						// DC value
						Literal l = dcSolution.getLiteral("dcValue");
						org.eclipse.rdf4j.model.Literal rdf4jLiteral;
						if(l.getLanguage() != null) {
							rdf4jLiteral = SimpleValueFactory.getInstance().createLiteral(l.getLexicalForm(), l.getLanguage());
						} else if(l.getDatatype() != null) {
							rdf4jLiteral = SimpleValueFactory.getInstance().createLiteral(l.getLexicalForm(), SimpleValueFactory.getInstance().createIRI(l.getDatatypeURI()));
						} else {
							rdf4jLiteral = SimpleValueFactory.getInstance().createLiteral(l.getLexicalForm());
						}
						
						if(dcterms.containsKey(dcProperty)) {
							dcterms.get(dcProperty).add(rdf4jLiteral);
						} else {
							dcterms.put(dcProperty, new ArrayList<org.eclipse.rdf4j.model.Value>(Collections.singletonList(rdf4jLiteral)));
						}
					}
					
					sfs.setDcterms(dcterms);
				}
				
				result.add(sfs);
			}			
		}
		
		return result;
	}



}
