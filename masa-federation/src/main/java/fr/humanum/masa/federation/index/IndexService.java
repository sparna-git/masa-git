package fr.humanum.masa.federation.index;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.humanum.masa.federation.index.LuceneDocumentBuilder.LuceneDocumentHandler;

/**
 * Main index service that encapsulates the method to (re-)index a source and trigger an autocomplete search.
 * The structure of the index is as follow :
 * <ul>
 *   <li><code>IndexFields.SOURCE_FIELD</code> 	: identifier of the source of the index entry (URI of the source, as defined in the federation);</li>
 *   <li><code>IndexFields.INDEX_FIELD</code> 	: identifier of the index to which the entry belongs;</li>
 *   <li><code>IndexFields.IRI_FIELD</code> 	: IRI of the entity (might appear in multiple indexes;</li>
 *   <li><code>IndexFields.LABEL_FIELD</code> 	: the label being indexed for autocomplete search, can be the label or a synonym, a note, etc.;</li>
 *   <li><code>IndexFields.PREF_LABEL_FIELD</code> 	: the preferred label of the IRI, only a single one will exist per IRI;</li>
 *   <li><code>IndexFields.PRIMARY_KEY_FIELD</code> 	: the primary key of the index entry, composed of : index + IRI + label (same IRI
 *   can appear in multiple index, a given IRI can have multiple associated label;</li>
 * </ul>
 * 
 * @author thomas
 *
 */
@Service
public class IndexService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	/**
	 * Path to Lucene index directory
	 */
	private final String directoryPath;
	
	/**
	 * Lucene index reader
	 */
	private DirectoryReader indexReader;
	
	/**
	 * Lucene index searcher
	 */
	private IndexSearcher indexSearcher;
	
	/**
	 * Lucene query parser
	 */
	private QueryParser queryParser;
	
	private List<LuceneDocumentBuilder> documentBuilders;
	
	public IndexService(
			// the lucene index directory path from the extConfigService
			@Value("#{extConfigService.luceneIndexDirectory}") String directoryPath,
			List<LuceneDocumentBuilder> documentBuilders
	) throws IOException {
		super();
		log.info("Building an IndexService with directoryPath : "+directoryPath);
		this.directoryPath = directoryPath;
		
		this.documentBuilders = documentBuilders;
		
		// open the reader/searcher a first time
		this.reOpenSearcher();
        
		// build query parser with same Analyzer as with indexing
        Analyzer queryAnalyzer = new Analyzer() {

			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
			    Tokenizer source = new StandardTokenizer();
			    TokenStream result = new StandardFilter(source);
			    result = new LowerCaseFilter(result);
			    result = new ASCIIFoldingFilter(result);
			    return new TokenStreamComponents(source, result);
			}
            
        };
    	
    	this.queryParser = new QueryParser(IndexFields.LABEL_FIELD, queryAnalyzer);
	}
	
    public void reIndexSource(String sourceId, Repository repository)
    throws CorruptIndexException, IOException {

    	// use a custom analyzer so we can do EdgeNGramFiltering for autocomplete
    	Analyzer analyzer = new Analyzer() {

			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				
				if(fieldName.equals(IndexFields.LABEL_FIELD)) {
				    Tokenizer source = new StandardTokenizer();
				    TokenStream result = new StandardFilter(source);
				    result = new LowerCaseFilter(result);
				    result = new ASCIIFoldingFilter(result);
				    // here is the magic for autocomplete : edgeNGrams starting at 3 letters
				    result = new EdgeNGramTokenFilter(result, 3, 20);
				    return new TokenStreamComponents(source, result);
				} else {
					// everything else is keyword, indexed as is
					Tokenizer source = new KeywordTokenizer();
				    return new TokenStreamComponents(source);
				}
			}
            
        };
        
    	IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    	// createOrAppend mode so that we can update document
    	iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

        // make sure the writer is closed even in case of Exception
        try(IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(directoryPath)), iwc)) {
        	// for each builder...
            for (LuceneDocumentBuilder aBuilder : documentBuilders) {
    			// build the lucene document for the provided source ID and repository
            	aBuilder.buildLuceneDocuments(sourceId, repository, new LuceneDocumentHandler() {
    				@Override
    				public void handleDocument(Document doc) {
    					// handle the produced Lucene document
    					try {
    						// will add the document if the same PK does not already exists
    						writer.updateDocument(new Term(IndexFields.PRIMARY_KEY_FIELD, doc.getField(IndexFields.PRIMARY_KEY_FIELD).stringValue()), doc);
    					} catch (IOException e) {
    						e.printStackTrace();
    					}
    				}
    			});
    		}
            
            // clean index etc - this is a costly operation
            writer.forceMerge(1);
            // re-open the searchers, otherwise they will not be synched with the updates
            this.reOpenSearcher();
        }
        
    }
    
    /**
     * Opens or reopens the reader and searcher.
     * @throws IOException
     */
    protected void reOpenSearcher() throws IOException {
    	
    	// reopen the reader/searcher
        if(this.indexReader != null) {
        	// if it was already initialized, try to reopen-it
        	// this call returns null if the reader has not changed
        	DirectoryReader newReader = DirectoryReader.openIfChanged(this.indexReader);
        	if ((newReader != null) && (this.indexReader != newReader)) {
        		log.info("Reopening new reader / searcher");
        		// if a new one was created, keep it
        		this.indexReader = newReader;
            	// initialize a new searcher from the reader
            	this.indexSearcher = new IndexSearcher(indexReader);
        	}
        } else {
        	// case where the reader/searcher was not already created
        	try {
        		log.info("Opening new reader / searcher");
	        	this.indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(directoryPath)));
	        	this.indexSearcher = new IndexSearcher(indexReader);
        	} catch (Exception e) {
    			log.warn("Lucene index directory does not exist (message : "+e.getMessage()+"). Index a source to be able to search the index.");
    		}
        }
    }
    
    /**
     * Trigger an autocomplete search for the given string in the given index
     * @param term
     * @param index
     * @param maxHits
     * @return
     * @throws IOException
     */
    public List<SearchResult> autocomplete(String term, String index, int maxHits) throws IOException {
    	String luceneSearch = term+" index:"+index;
    	log.debug("Search '"+term+"' on index '"+index+"' (lucene query : '"+luceneSearch+"')");
    	
        // Parse term to generate query
        Query query = null;
		try {
			query = this.queryParser.parse(term+" index:"+index);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

        // Search top results
        TopDocs results = indexSearcher.search(query, maxHits);
        ScoreDoc[] hits = results.scoreDocs;
        int numTotalHits = Math.toIntExact(results.totalHits);
        log.debug("Found "+ numTotalHits + " hits");
        
        List<SearchResult> suggestions = new ArrayList<SearchResult>();
        for (ScoreDoc doc : hits) {
        	log.debug("  "+doc.doc+ " score "+doc.score);
        	Document d = indexSearcher.doc(doc.doc);
        	SearchResult sr = new SearchResult(
        			SimpleValueFactory.getInstance().createIRI(d.get(IndexFields.IRI_FIELD)),
        			d.get(IndexFields.LABEL_FIELD),
        			d.get(IndexFields.PREF_LABEL_FIELD)
        	);
            suggestions.add(sr);
        }

        return suggestions;
    }
    
    /**
     * Returns all the entries in a given index
     * @param indexId
     * @return
     * @throws IOException
     */
    public List<SearchResult> getEntries(String indexId) throws IOException {		
    	List<SearchResult> result = new ArrayList<>();  
    	
    	Query query = null;
		try {
			query = this.queryParser.parse("index:"+indexId);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
    	
		indexSearcher.search(query, new SimpleCollector() {

			@Override
			public void collect(int i) throws IOException {
				Document d = indexReader.document(i);
				result.add(new SearchResult(
						SimpleValueFactory.getInstance().createIRI(d.get(IndexFields.IRI_FIELD)),
	        			d.get(IndexFields.LABEL_FIELD),
	        			d.get(IndexFields.PREF_LABEL_FIELD)
	        	));
			}

			@Override
			public boolean needsScores() {
				return false;
			}
			
		});

        return result;
    }
    
    /**
     * Returns all the distinct indexes in the Lucene index. This might be a costly operation
     * @param field
     * @return
     * @throws IOException
     */
    public Set<String> getDistinctIndexIds() throws IOException {
    	return this.getDistinctValues(IndexFields.INDEX_FIELD);
    }
    
    
    private Set<String> getDistinctValues(String field) throws IOException {
    	Set<String> values = new HashSet<>();
    	if(this.indexReader != null) {
	    	for (int i = 0; i < this.indexReader.maxDoc(); i++) {
	    		values.add(this.indexReader.document(i).get(field));
			}    
    	}
    	return values;
    }
    
    public static void main(String... strings) throws Exception {
//    	SimpleValueFactory f = SimpleValueFactory.getInstance();
////    	
////    	// Repository r = new SPARQLRepository("http://vocabularies.unesco.org/sparql");
////    	Repository r = new SPARQLRepository("http://localhost:7200/repositories/unesco");
////    	r.initialize();
////    	
////    	LuceneDocumentBuilder b1 = new LuceneDocumentBuilder(
////    			"http://www.w3.org/2004/02/skos/core#Concept_http://www.w3.org/2004/02/skos/core#broader_http://www.w3.org/2004/02/skos/core#Concept".replaceAll("\\W+", ""),
////    			new DomainPathRangeIriFetcher(f.createIRI("http://www.w3.org/2004/02/skos/core#Concept"), "<http://www.w3.org/2004/02/skos/core#broader>", f.createIRI("http://www.w3.org/2004/02/skos/core#Concept")),
////    			new SkosLabelFetcher("fr")
////    	);
//    	LuceneDocumentBuilder b2 = new LuceneDocumentBuilder(
//    			"http://www.w3.org/2004/02/skos/core#Concept_^http://www.w3.org/2004/02/skos/core#member_http://www.w3.org/2004/02/skos/core#Collection".replaceAll("\\W+", ""),
//    			new DomainPathRangeIriFetcher(f.createIRI("http://www.w3.org/2004/02/skos/core#Concept"), "^<http://www.w3.org/2004/02/skos/core#member>", f.createIRI("http://www.w3.org/2004/02/skos/core#Collection")),
//    			new SkosLabelFetcher("fr")
//    	);
//    	
//    	IndexService service = new IndexService("/home/thomas/sparna/00-Clients/MASA"+"/lucene/index", Arrays.asList(new LuceneDocumentBuilder[] { b2 }));
//    	
////    	service.reIndexSource(
////    			// identifiant de la source
////    			"http://vocabularies.unesco.org/sparql".replaceAll("\\W+", ""),
////    			// repository
////    			r,
////    			// builders
////    			Arrays.asList(new LuceneDocumentBuilder[] { b1, b2 })
////    	);
////    	
////    	// indexer.deleteTermsFromSource("http://vocabularies-test.unesco.org/sparql".replaceAll("\\W+", ""));
////    	
////    	System.out.println(service.autocomplete("vehic", "http://test.fr/onto#Site_http://test.fr/onto#a_trouve_http://test.fr/onto#Sepulture".replaceAll("\\W+", ""), 5));
////    	
//    	
//    	System.out.println(service.getDistinctValues(IndexFields.INDEX_FIELD));
    }
	
}
