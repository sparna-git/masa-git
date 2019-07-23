package fr.humanum.openarchaeo.federation.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.humanum.masa.openarchaeo.expand.SparqlExpander;
import fr.humanum.masa.openarchaeo.expand.SparqlExpansionConfigOwlSupplier;
import fr.humanum.openarchaeo.federation.ExtConfigService;
import fr.humanum.openarchaeo.federation.referentiels.IriHarvester;

public class LuceneDocumentBuilderFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	/**
	 * Prefixes to be interpreted in the given specifications for lucene document builder (in config file
	 */
	protected Map<String, String> prefixes = new HashMap<>();
	/**
	 * Value Factory
	 */
	protected ValueFactory factory = SimpleValueFactory.getInstance();
	/**
	 * Default label fetcher to be used when no label fetcher is explicitely given (reads rdfs:label, skos:prefLabel, etc.)
	 */
	protected LabelFetcher defaultLabelFetcher;
	/**
	 * The object to perform SPARQL expansion
	 */
	protected SparqlExpander sparqlExpander;
	protected IriHarvester referentielHarvester;
	
	public LuceneDocumentBuilderFactory(
			Map<String, String> prefixes,
			LabelFetcher defaultLabelFetcher,
			ExtConfigService extConfigService,
			IriHarvester iriHarvester
	) {
		super();
		log.debug("Init LuceneDocumentBuilderFactory with defaultLabelFetcher : "+defaultLabelFetcher.getClass().getSimpleName());
		this.prefixes = prefixes;
		this.defaultLabelFetcher = defaultLabelFetcher;
		this.referentielHarvester = iriHarvester;
		
		try {
			File expansionConfigFile = extConfigService.findMandatoryFile(ExtConfigService.QUERY_EXPANSION_CONFIG_FILE);
			Model m = ModelFactory.createDefaultModel();
			RDFDataMgr.read(m, new FileInputStream(expansionConfigFile), Lang.TURTLE);
			SparqlExpansionConfigOwlSupplier configSupplier = new SparqlExpansionConfigOwlSupplier(m);
			// strict expansion to check we didn't made any mistake in the configuration
			this.sparqlExpander = new SparqlExpander(configSupplier.get(), true);
		} catch (FileNotFoundException ignore) {
			// Can be safely ignored since we checked for a required file
			ignore.printStackTrace();
		}
		
	}

	public LuceneDocumentBuilder newLuceneDocumentBuilder(
		String domain,
		String path,
		String range
	) {
		return newLuceneDocumentBuilder(domain, path, range, this.defaultLabelFetcher);
	}
	
	public LuceneDocumentBuilder newLuceneDocumentBuilder(String spec) {
		String[] strings = spec.split(" ");
		return newLuceneDocumentBuilder(strings[0], strings[1], strings[2], this.defaultLabelFetcher);
	}
	
	public LuceneDocumentBuilder newLuceneDocumentBuilder(String spec, boolean fetchIris, boolean ignoreRange, LabelFetcher labelFetcher) {
		String[] strings = spec.split(" ");
		return newLuceneDocumentBuilder(strings[0], strings[1], strings[2], labelFetcher, fetchIris, ignoreRange);
	}
	
	public LuceneDocumentBuilder newLuceneDocumentBuilder(
			String domain,
			String path,
			String range,
			LabelFetcher labelFetcher
		) {
		return newLuceneDocumentBuilder(
				domain,
				path,
				range,
				labelFetcher,
				false,
				false
		);
	}
	
	public LuceneDocumentBuilder newLuceneDocumentBuilder(
			String domain,
			String path,
			String range,
			LabelFetcher labelFetcher,
			boolean fetchIris,
			boolean ignoreRange
		) {
			log.debug("Creating a LuceneDocumentBuilder for domain : "+domain+", path : "+path+", range : "+range+" and labelFetcher : "+labelFetcher.getClass().getSimpleName());
			
			String expandedDomain = expandPrefixes(domain, false);
			String expandedPath = expandPrefixes(path, true);
			String expandedRange = expandPrefixes(range, false);
			
			DomainPathRangeSparqlBuilder builder;
			if(ignoreRange) {
				builder = new DomainPathRangeSparqlBuilder(
						factory.createIRI(expandedDomain),
						expandedPath
				);
			} else {
				builder = new DomainPathRangeSparqlBuilder(
						factory.createIRI(expandedDomain),
						expandedPath,
						factory.createIRI(expandedRange)
				);
			}
			String sparql = builder.generateSparql();
			
			// expand SPARQL on CIDOC-CRM
			String expandedSparql = this.sparqlExpander.expand(sparql);
			log.debug("Generated domain/path/range SPARQL query :\n"+expandedSparql);
			IriFetcher iriFetcher = (fetchIris)?new IriRetriever(new SparqlIriFetcher(expandedSparql), this.referentielHarvester):new SparqlIriFetcher(expandedSparql);
			
			LuceneDocumentBuilder b = new LuceneDocumentBuilder(
	    			buildIndexId(expandedDomain, expandedPath, expandedRange),
	    			iriFetcher,
	    			labelFetcher
	    	);
			
			return b;
		}
	
	protected String buildIndexId(String domain, String path, String range) {
		return (domain+"_"+path+"_"+range).replaceAll("\\W+", "");
	}
	
	protected String expandPrefixes(String s, boolean addQuotes) {
		StringBuffer result = new StringBuffer();
		result.append(s);
		
		for (Map.Entry<String, String> aPrefixEntry : this.prefixes.entrySet()) {
			StringBuffer temp = new StringBuffer();
			Pattern p = Pattern.compile(aPrefixEntry.getKey()+"\\:(\\w*)");
            Matcher m = p.matcher(result.toString());
            while (m.find()) {
            	m.appendReplacement(temp, (addQuotes?"<":"")+aPrefixEntry.getValue()+"$1"+(addQuotes?">":""));
            }
            // append last part of String
            m.appendTail(temp);
            
            result = temp;
		}
		
		return result.toString();
	}
	
	public Map<String, String> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(Map<String, String> prefixes) {
		this.prefixes = prefixes;
	}

}
