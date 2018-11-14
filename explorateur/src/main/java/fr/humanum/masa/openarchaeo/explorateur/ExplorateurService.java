package fr.humanum.masa.openarchaeo.explorateur;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.algebra.walker.ElementWalker_New;
import org.apache.jena.sparql.expr.ExprVisitorBase;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.humanum.masa.openarchaeo.expand.SparqlExpander;
import fr.humanum.masa.openarchaeo.expand.SparqlExpansionConfig;
import fr.humanum.masa.openarchaeo.expand.SparqlExpansionConfigOwlSupplier;
import fr.humanum.masa.openarchaeo.expand.SparqlExpansionVisitor;

@Service
public class ExplorateurService {
	
	private Logger log= LoggerFactory.getLogger(this.getClass().getName());
	
	private ExtConfigService extConfigService;
	private SparqlExpansionConfig expansionConfig;
	private DisplayProperties displayProperties;
	private FederationService federationService;
	
	@Inject
	public ExplorateurService(ExtConfigService extConfigService, FederationService federationService) {
		super();
		this.extConfigService = extConfigService;
		this.federationService = federationService;
	}
	
	@PostConstruct
	public void init() {
		try {
			File expansionConfigFile = extConfigService.findMandatoryFile(ExtConfigService.QUERY_EXPANSION_CONFIG_FILE);
			Model m = ModelFactory.createDefaultModel();
			RDFDataMgr.read(m, new FileInputStream(expansionConfigFile), Lang.TURTLE);
			SparqlExpansionConfigOwlSupplier configSupplier = new SparqlExpansionConfigOwlSupplier(m);
			this.expansionConfig = configSupplier.get();
			this.displayProperties=new DisplayProperties();
		} catch (FileNotFoundException ignore) {
			// Can be safely ignored since we checked for a required file
			ignore.printStackTrace();
		}
	}
	
	
	public void getResult(String queryExpand, OutputStream out) {
		SPARQLResultsXMLWriter sparqlWriter = new SPARQLResultsXMLWriter(out);
		this.federationService.query(queryExpand, sparqlWriter);
	}
	
	public String expandQuery(String query) {
		log.debug("Expanding query : \n"+query+"...");
		SparqlExpander expander = new SparqlExpander(this.expansionConfig);
		String result = expander.expand(query);
		log.debug("Expanded query to : \n"+result+"...");
		return result;
	}
	
	public String addTimelineVariableToQuery(String sparql,String startDateProperty, String endDateProperty) {
		return displayProperties.getStartAndEndDate(sparql, startDateProperty, endDateProperty).toString(Syntax.syntaxSPARQL_11);
	}
	
	public String addSourceToQuery(String query, String sourceParam) {
		if(sourceParam == null || sourceParam.equals("")) {
			log.debug("No sources provided, don't add sources to query");
			return query;
		}
		
		String [] sources=sourceParam.split("[ ]");		
		Query q=QueryFactory.create(query);
		for (String string : sources) {
			log.debug("Adding source "+string+" to query");
			q.getNamedGraphURIs().add(string);
		}
		log.debug("Done adding sources");
		return q.toString(Syntax.syntaxSPARQL_11);		
	}
	
	public String getSources() throws ClientProtocolException, IOException {
		return federationService.getSources();
	}	
	
	/**
	 * Renvoie les queries d'exemple paramétrées dans l'application.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<QueryExample> getExampleQueries() {
		File exampleQueryFile = this.extConfigService.findFile(ExtConfigService.QUERY_EXAMPLE_CONFIG_FILE);
		
		if(exampleQueryFile != null && exampleQueryFile.exists()){
			ObjectMapper objectMapper=new ObjectMapper();
			//add this line  
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);    
			try {
				return objectMapper.readValue(exampleQueryFile , new TypeReference<List<QueryExample>>(){});
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			log.warn("Exemple queries config file is not setup or does not exists.");
			return null;
		}
	}
	
}
