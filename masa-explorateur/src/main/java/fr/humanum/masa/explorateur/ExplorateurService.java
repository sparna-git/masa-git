package fr.humanum.masa.explorateur;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

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

import fr.humanum.masa.expand.SparqlExpansionConfig;
import fr.humanum.masa.expand.SparqlExpansionConfigOwlSupplier;
import fr.humanum.masa.expand.SparqlExpansionVisitor;

@Service
public class ExplorateurService {
	
	private Logger log= LoggerFactory.getLogger(this.getClass().getName());
	
	private ExtConfigService extConfigService;
	private SparqlExpansionConfig expansionConfig;
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
		Query q = QueryFactory.create(query);
		SparqlExpansionVisitor expansionVisitor = new SparqlExpansionVisitor(this.expansionConfig);
		ElementWalker_New.walk(q.getQueryPattern(), expansionVisitor, new ExprVisitorBase());
		String result = q.toString(Syntax.syntaxSPARQL_11);
		log.debug("Expanded query to : \n"+result+"...");
		return result;
	}
	
	public String addSourceToQuery(String query, String source) {
		if(source == null) {
			return query;
		}
		
		log.debug("ajout de la source à la requête");
		Query q=QueryFactory.create(query);
		q.getNamedGraphURIs().add(source);
		log.debug("ajout de la source à la requête terminé");
		return q.toString(Syntax.syntaxSPARQL_11);		
	}
	
	public void getSources(OutputStream out) throws ClientProtocolException, IOException {
		String sources = federationService.getSources();
		IOUtils.copy(new ByteArrayInputStream(sources.getBytes()), out);
	}	
	
}
