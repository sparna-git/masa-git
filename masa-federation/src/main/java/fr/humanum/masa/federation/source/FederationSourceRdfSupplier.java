package fr.humanum.masa.federation.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.impl.IRIFactoryImpl;
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
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDFS;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import fr.humanum.masa.federation.ExtConfigService;

public class FederationSourceRdfSupplier implements Supplier<Set<FederationSource>>{

	private ExtConfigService extConfig;


	public FederationSourceRdfSupplier(ExtConfigService extConfig){
		this.extConfig=extConfig;
	}

	private Model getModel() throws FileNotFoundException{
		File file=extConfig.getSourceFile();
		InputStream in=new FileInputStream(file);
		Model model=ModelFactory.createDefaultModel();
		model.read(in, FileUtils.guessLang(file.getName(), Lang.RDFXML.getName()));
		return model;
	}

	@Override
	public Set<FederationSource> get() {
		// TODO Auto-generated method stub

		String queryString = "prefix sd: <http://www.w3.org/ns/sparql-service-description#>"+
				             "prefix void: <http://rdfs.org/ns/void#>"+ 
				             "prefix fed-config: <https://masa.hypotheses.org/federation/config#>"+ 
						     "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+

							 "select DISTINCT ?sourceURI  ?endpoint ?defaultGraph where {"+

									" ?graphname a sd:NamedGraph ;"+
									" sd:name ?sourceURI;"+
									" void:sparqlEndpoint ?endpoint;"+
									" OPTIONAL{?graphname fed-config:sparqlGraph ?defaultGraph.} "+
											
								" } group by ?sourceURI ?endpoint ?defaultGraph";
										
		Query query = QueryFactory.create(queryString) ;
		Set<FederationSource> result = new HashSet<FederationSource>();
		Map<String,String> labels=new HashMap<String, String>();

		Model m=null;
		try {
			m = getModel();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try (QueryExecution qexec = QueryExecutionFactory.create(query, getModel())) {
			ResultSet results = qexec.execSelect() ;
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution() ;       
				IRI source = SimpleValueFactory.getInstance().createIRI(soln.getResource("sourceIRI").getURI()); 
				IRI defaultGraph = SimpleValueFactory.getInstance().createIRI(soln.getResource("defaultGraph").getURI()) ; 
				StmtIterator iter =m.listLiteralStatements(soln.getResource("sourceIRI"), RDFS.label, true);
				while(iter.hasNext()){
					Statement st=iter.next();
					Literal literal=st.getObject().asLiteral();
					labels.put(literal.getLanguage(), literal.getLexicalForm());
				}
				IRI endpoint = SimpleValueFactory.getInstance().createIRI(soln.getResource("endpoint").getURI()) ;
				SimpleFederationSource sfs=new SimpleFederationSource(source, endpoint, defaultGraph, labels);
				result.add(sfs);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}



}
