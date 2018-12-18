package fr.humanum.openarchaeo.explorateur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.http.client.ClientProtocolException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.path.PathParser;
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

@Service
public class ExplorateurService {
	
	private Logger log= LoggerFactory.getLogger(this.getClass().getName());
	
	public static enum View {
		TABLE(Collections.singletonList(new ExtraPropertyConfig(
				"<http://www.w3.org/2004/02/skos/core#prefLabel>",
				"thisLabel",
				true
		))),
		RAWRESPONSE(Collections.singletonList(new ExtraPropertyConfig(
				"<http://www.w3.org/2004/02/skos/core#prefLabel>",
				"thisLabel",
				true
		))),
		GCHART(null),
		PIVOT(null),
		TIMELINE(Arrays.asList(new ExtraPropertyConfig[] { 
			new ExtraPropertyConfig(
				"<http://www.cidoc-crm.org/cidoc-crm/P53_has_former_or_current_location>/<http://www.w3.org/2003/01/geo/wgs84_pos#lat>",
				"latitude",
				true
			),
			new ExtraPropertyConfig(
				"<http://www.cidoc-crm.org/cidoc-crm/P53_has_former_or_current_location>/<http://www.w3.org/2003/01/geo/wgs84_pos#long>",
				"longitude",
				true
			),
			new ExtraPropertyConfig(
				"<http://www.w3.org/2004/02/skos/core#prefLabel>",
				"thisLabel",
				true
			)
		})),
		LEAFLET(Arrays.asList(new ExtraPropertyConfig[] { 
			new ExtraPropertyConfig(
				"<http://www.cidoc-crm.org/cidoc-crm/P53_has_former_or_current_location>/<http://www.w3.org/2003/01/geo/wgs84_pos#lat>",
				"latitude",
				true
			),
			new ExtraPropertyConfig(
				"<http://www.cidoc-crm.org/cidoc-crm/P53_has_former_or_current_location>/<http://www.w3.org/2003/01/geo/wgs84_pos#long>",
				"longitude",
				true
			),
			new ExtraPropertyConfig(
				"<http://www.w3.org/2004/02/skos/core#prefLabel>",
				"thisLabel",
				true
			)
		}));
		
		private List<ExtraPropertyConfig> expansionConfig;
		
		private View(List<ExtraPropertyConfig> expansionConfig) {
			this.expansionConfig = expansionConfig;
		}

		public List<ExtraPropertyConfig> getExpansionConfig() {
			return expansionConfig;
		}

		static class ExtraPropertyConfig {
			String path;
			String extraPropertyName;
			boolean optional;
			
			public ExtraPropertyConfig(String path, String extraPropertyName, boolean optional) {
				super();
				this.path = path;
				this.extraPropertyName = extraPropertyName;
				this.optional = optional;
			}
		}
	}
	
	private ExtConfigService extConfigService;
	private SparqlExpansionConfig expansionConfig;
	private SparqlFetchExtraProperty sparqlFetchExtraProperty;
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
		
		sparqlFetchExtraProperty = new SparqlFetchExtraProperty();
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
	
	public String addPropertiesToQuery(String query, View view) {
		log.debug("Adding properties to query for view "+view.name()+"...");
		
		String result = query;
		if(view.getExpansionConfig() != null) {			
			for (View.ExtraPropertyConfig c : view.getExpansionConfig()) {
				log.debug("Adding property path for variable "+c.extraPropertyName+"...");
				result = sparqlFetchExtraProperty.fetchExtraProperty(
					result,
					PathParser.parse(c.path, ModelFactory.createDefaultModel()),
					"this",
					c.extraPropertyName,
					c.optional);
			}
		}
		
		return result;
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
	
	public List<FederationSourceJson> getSources() throws ClientProtocolException, IOException {
		String json = federationService.getSources();
		
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);    
		return objectMapper.readValue(json , new TypeReference<List<FederationSourceJson>>(){});
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
