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
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.util.FileUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.humanum.openarchaeo.federation.geonames.SimpleFederationSourceGeonamesExpansion;

public class FederationSourceRemoteMetadataSupplier implements Supplier<List<? extends FederationSource>> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private File sourceDefinitionFile;


	public FederationSourceRemoteMetadataSupplier(File sourceDefinitionFile){
		this.sourceDefinitionFile=sourceDefinitionFile;
	}

	private Model getModel() throws FileNotFoundException{
		InputStream in=new FileInputStream(sourceDefinitionFile);
		Model model=ModelFactory.createDefaultModel();
		model.read(in,null, FileUtils.guessLang(sourceDefinitionFile.getName(), Lang.RDFXML.getName()));
		return model;
	}

	@Override
	public List<? extends FederationSource> get() {

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
		List<SimpleFederationSource> result = new ArrayList<SimpleFederationSource>();
		

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
				
				SimpleFederationSource sfs = new SimpleFederationSource(source, endpoint, defaultGraph);
				Map<IRI, List<org.eclipse.rdf4j.model.Value>> metadata = new HashMap<>();
				
				String remoteQueryString = 
						 "PREFIX sd: <http://www.w3.org/ns/sparql-service-description#>"+"\n"+
					     "PREFIX dcterms: <http://purl.org/dc/terms/> "+"\n"+
					     "PREFIX dcat: <http://www.w3.org/ns/dcat#> "+"\n"+
					     "PREFIX schema: <http://schema.org/> "+"\n"+
					     "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "+"\n"+
						 "SELECT ?dcProperty ?dcValue"+"\n"+
						 "WHERE {"+"\n"+
						 "   {"+"\n"+
						 "     {"+"\n"+
						 "     <"+rSource.getURI()+"> ?dcProperty ?dcValue . "+"\n"+
						 "     FILTER(STRSTARTS(STR(?dcProperty), \"http://purl.org/dc/terms/\")||STRSTARTS(STR(?dcProperty), \"http://www.w3.org/ns/dcat#\"))"+"\n"+
						 "     }"+"\n"+
						 "     UNION"+"\n"+
						 "     {"+"\n"+
						 "     <"+rSource.getURI()+"> dcat:distribution/dcterms:license ?dcValue . "+"\n"+
						 "     BIND(dcterms:license AS ?dcProperty) "+"\n"+
						 "     }"+"\n"+
						 "     UNION"+"\n"+
						 "     {"+"\n"+
						 "     <"+rSource.getURI()+"> dcterms:temporal/schema:startDate ?dcValue . "+"\n"+
						 "     BIND(schema:startDate AS ?dcProperty) "+"\n"+
						 "     }"+"\n"+
						 "     UNION"+"\n"+
						 "     {"+"\n"+
						 "     <"+rSource.getURI()+"> dcterms:temporal/schema:endDate ?dcValue . "+"\n"+
						 "     BIND(schema:endDate AS ?dcProperty) "+"\n"+
						 "     }"+"\n"+
						 "     UNION"+"\n"+
						 "     {"+"\n"+
						 "     <"+rSource.getURI()+"> dcterms:subject/skos:prefLabel ?dcValue . "+"\n"+
						 "     BIND(dcterms:subject AS ?dcProperty) "+"\n"+
						 "     }"+"\n"+
						 "   }"+"\n"+
						 "} ORDER BY ?dcProperty ?dcValue";		
				
				log.debug("Will execute remote query :\n"+remoteQueryString);
				Query dctermsQuery = QueryFactory.create(remoteQueryString) ;
				
				try(QueryExecution dctermsQExec = QueryExecutionFactory.sparqlService(endpoint.toString(), dctermsQuery)) {
					ResultSet resultRemote = dctermsQExec.execSelect();
					readResultSet(resultRemote, metadata);					
				}
				
				// now query for the local DCTerms description
				String localQueryString = 
						 "PREFIX sd: <http://www.w3.org/ns/sparql-service-description#>"+"\n"+
					     "PREFIX dcterms: <http://purl.org/dc/terms/> "+"\n"+
					     "PREFIX dcat: <http://www.w3.org/ns/dcat#> "+"\n"+
					     "PREFIX schema: <http://schema.org/> "+"\n"+
					     "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "+"\n"+
						 "SELECT ?dcProperty ?dcValue"+"\n"+
						 "WHERE {"+"\n"+
						 "   ?graph sd:name <"+rSource.getURI()+"> ."+"\n"+
						 "   {"+"\n"+
						 "   {"+"\n"+
						 "     ?graph ?dcProperty ?dcValue . "+"\n"+
						 "     FILTER(STRSTARTS(STR(?dcProperty), \"http://purl.org/dc/terms/\")||STRSTARTS(STR(?dcProperty), \"http://www.w3.org/ns/dcat#\"))"+"\n"+
						 "   }"+"\n"+
						 "   UNION"+"\n"+
						 "   {"+"\n"+
						 "     ?graph dcterms:subject/skos:prefLabel ?dcValue . "+"\n"+
						 "     BIND(dcterms:subject AS ?dcProperty) "+"\n"+
						 "   }"+"\n"+
						 "   }"+"\n"+
						 "} ORDER BY ?dcProperty ?dcValue";	
				
				log.debug("Will execute local query :\n"+localQueryString);
				try(QueryExecution dctermsQExec = QueryExecutionFactory.create(localQueryString, m)) {
					ResultSet resultLocal = dctermsQExec.execSelect();
					readResultSet(resultLocal, metadata);					
				}
				
				sfs.setDcterms(metadata);
				result.add(sfs);
			}			
		}
		
		// now expand on dct:spatial using the Geonames API
		result = result.stream().map(new SimpleFederationSourceGeonamesExpansion()).collect(Collectors.toList());
		
		return result;
	}


	private void readResultSet(ResultSet rs, Map<IRI, List<org.eclipse.rdf4j.model.Value>> result) {
		
		while (rs.hasNext()) {
			QuerySolution dcSolution = rs.nextSolution() ;
			log.debug("  Reading a source property : "+dcSolution);
			// DC property
			IRI dcProperty = SimpleValueFactory.getInstance().createIRI(dcSolution.getResource("dcProperty").getURI());
			// DC value
			RDFNode l = dcSolution.get("dcValue");
			org.eclipse.rdf4j.model.Value rdf4jValue = null;
			if(l.isLiteral()) {
				if(l.asLiteral().getLanguage() != null && !l.asLiteral().getLanguage().equals("")) {
					rdf4jValue = SimpleValueFactory.getInstance().createLiteral(l.asLiteral().getLexicalForm(), l.asLiteral().getLanguage());
				} else if(l.asLiteral().getDatatype() != null) {
					rdf4jValue = SimpleValueFactory.getInstance().createLiteral(l.asLiteral().getLexicalForm(), SimpleValueFactory.getInstance().createIRI(l.asLiteral().getDatatypeURI()));
				} else {
					rdf4jValue = SimpleValueFactory.getInstance().createLiteral(l.asLiteral().getLexicalForm());
				}
			} else if(l.isURIResource()) {
				rdf4jValue = SimpleValueFactory.getInstance().createIRI(l.asResource().getURI());
			}
			
			// can be null in case of blank node
			if(rdf4jValue != null) {
				if(result.containsKey(dcProperty)) {
					result.get(dcProperty).add(rdf4jValue);
				} else {
					result.put(dcProperty, new ArrayList<org.eclipse.rdf4j.model.Value>(Collections.singletonList(rdf4jValue)));
				}
			}
		}
	}
}
