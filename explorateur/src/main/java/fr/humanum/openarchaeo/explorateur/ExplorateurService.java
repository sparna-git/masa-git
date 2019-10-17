package fr.humanum.openarchaeo.explorateur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
		TABLE(Collections.singletonList(new SparqlFetchExtraPropertyPostProcessor(
				PathParser.parse("<http://www.w3.org/2004/02/skos/core#prefLabel>", ModelFactory.createDefaultModel()),
				"this",
				"thisLabel",
				true,
				true
		))),
		RAWRESPONSE(Collections.singletonList(new SparqlFetchExtraPropertyPostProcessor(
				PathParser.parse("<http://www.w3.org/2004/02/skos/core#prefLabel>", ModelFactory.createDefaultModel()),
				"this",
				"thisLabel",
				true,
				true
		))),
		GCHART(null),
		PIVOT(null),
		TIMELINE(Arrays.asList(new SparqlPostProcessor[] { 
			new SparqlFetchExtraPropertyPostProcessor(
					PathParser.parse("<http://www.w3.org/2004/02/skos/core#prefLabel>", ModelFactory.createDefaultModel()),
					"this",
					"thisLabel",
					true,
					true
			),
			new SparqlFetchExtraPropertyPostProcessor(					
				PathParser.parse("<http://www.cidoc-crm.org/cidoc-crm/P8i_witnessed>", ModelFactory.createDefaultModel()),
				"this",
				"event",
				false,
				false
			),
			new SparqlFetchExtraPropertyPostProcessor(					
				PathParser.parse("<http://www.cidoc-crm.org/cidoc-crm/P4_has_time-span>/<http://www.cidoc-crm.org/cidoc-crm/P82a_begin_of_the_begin>", ModelFactory.createDefaultModel()),
				"event",
				"begin",
				true,
				false
			),
			new SparqlFetchExtraPropertyPostProcessor(					
				PathParser.parse("<http://www.cidoc-crm.org/cidoc-crm/P4_has_time-span>/<http://www.cidoc-crm.org/cidoc-crm/P82b_end_of_the_end>", ModelFactory.createDefaultModel()),
				"event",
				"end",
				true,
				false
			)
		})),
		LEAFLET(Arrays.asList(new SparqlPostProcessor[] { 
			new SparqlFetchExtraPropertyPostProcessor(
				PathParser.parse("(<http://www.cidoc-crm.org/cidoc-crm/P53_has_former_or_current_location>|(<http://www.ics.forth.gr/isl/CRMsci/O19i_was_object_found_by>/<http://www.ics.forth.gr/isl/CRMsci/S19_Encounter_Event>/<http://www.cidoc-crm.org/cidoc-crm/P53_has_former_or_current_location>))/<http://www.w3.org/2003/01/geo/wgs84_pos#lat>", ModelFactory.createDefaultModel()),
				"this",
				"latitude",
				true,
				false
			),
			new SparqlFetchExtraPropertyPostProcessor(
				PathParser.parse("(<http://www.cidoc-crm.org/cidoc-crm/P53_has_former_or_current_location>|(<http://www.ics.forth.gr/isl/CRMsci/O19i_was_object_found_by>/<http://www.ics.forth.gr/isl/CRMsci/S19_Encounter_Event>/<http://www.cidoc-crm.org/cidoc-crm/P53_has_former_or_current_location>))/<http://www.w3.org/2003/01/geo/wgs84_pos#long>", ModelFactory.createDefaultModel()),
				"this",
				"longitude",
				true,
				false
			),
			new SparqlFetchExtraPropertyPostProcessor(
				PathParser.parse("<http://www.w3.org/2004/02/skos/core#prefLabel>", ModelFactory.createDefaultModel()),
				"this",
				"pointLabel",
				true,
				false
			),
			new SparqlBindWktPostProcessor("this", "point")
		}));
		
		private List<SparqlPostProcessor> postProcessors;
		
		private View(List<SparqlPostProcessor> postProcessors) {
			this.postProcessors = postProcessors;
		}

		public List<SparqlPostProcessor> getPostProcessors() {
			return postProcessors;
		}
	}
	
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
		if(view.getPostProcessors() != null) {			
			for (SparqlPostProcessor pp : view.getPostProcessors()) {
				log.debug("Applying post-processor : "+pp+"...");
				result = pp.postProcess(result);
			}
		}
		
		return result;
	}
	
	public String addSourcesToQuery(String query, List<String> sources) {
		if(sources == null || sources.size() == 0) {
			log.debug("No sources provided, don't add sources to query");
			return query;
		}
		
		// String [] sources=sourceParam.split("[ ]");		
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
