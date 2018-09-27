package fr.humanum.masa.federation.index;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneDocumentBuilderFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	protected Map<String, String> prefixes = new HashMap<>();
	protected ValueFactory factory = SimpleValueFactory.getInstance();
	protected LabelFetcher defaultLabelFetcher;
	
	public LuceneDocumentBuilderFactory(Map<String, String> prefixes, LabelFetcher defaultLabelFetcher) {
		super();
		log.debug("Init LuceneDocumentBuilderFactory with defaultLabelFetcher : "+defaultLabelFetcher.getClass().getSimpleName());
		this.prefixes = prefixes;
		this.defaultLabelFetcher = defaultLabelFetcher;
	}
	
	public LuceneDocumentBuilderFactory(LabelFetcher defaultLabelFetcher) {
		this(new HashMap<String, String>(), defaultLabelFetcher);
	}

	public LuceneDocumentBuilder newLuceneDocumentBuilder(
		String domain,
		String path,
		String range
	) {
		return newLuceneDocumentBuilder(domain, path, range, this.defaultLabelFetcher);
	}
	
	public LuceneDocumentBuilder newLuceneDocumentBuilder(
			String domain,
			String path,
			String range,
			LabelFetcher labelFetcher
		) {
			log.debug("Creating a LuceneDocumentBuilder for domain : "+domain+", path : "+path+", range : "+range+" and labelFetcher : "+labelFetcher.getClass().getSimpleName());
			
			LuceneDocumentBuilder b = new LuceneDocumentBuilder(
	    			buildIndexId(domain, path, range),
	    			new DomainPathRangeIriFetcher(
	    					factory.createIRI(expandPrefixes(domain, false)),
	    					expandPrefixes(path, true), 
	    					factory.createIRI(expandPrefixes(range, false))
	    			),
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
	
	public static void main(String... args) throws Exception {
		LuceneDocumentBuilderFactory me = new LuceneDocumentBuilderFactory(new RdfsLabelFetcher("fr"));
		me.getPrefixes().put("crm", "http://www.cidoc-crm.org/cidoc-crm/");
		me.getPrefixes().put("dbpedia-owl", "http://dbpedia.org/ontology/");
		System.out.println(me.expandPrefixes("crm:E1-Person", false));
		System.out.println(me.expandPrefixes("crm:P2/crm:PX", true));
		System.out.println(me.expandPrefixes("dbpedia-owl:birthPlace", false));
	}
	
}
