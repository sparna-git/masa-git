package fr.humanum.openarchaeo.federation.index;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a Lucene document to insert in the index. This is a combination of 2 things :
 * <ol>
 *   <li>An <code>IriFetcher</code> that fetches the list of IRIs to index;</li>
 *   <li>A <code>LabelFetcher</code> that fetches labels to index for a given IRI;</li>
 * </ol>
 * 
 * @author thomas
 *
 */
public class LuceneDocumentBuilder {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName()); 
	
	private final String indexId;
	private final IriFetcher iriFetcher;
	private final LabelFetcher labelFetcher;	
	
	public LuceneDocumentBuilder(String indexId, IriFetcher iriFetcher, LabelFetcher labelFetcher) {
		super();
		this.indexId = indexId;
		this.iriFetcher = iriFetcher;
		this.labelFetcher = labelFetcher;
	}

	public void buildLuceneDocuments(String sourceId, Repository repository, LuceneDocumentHandler handler) {
		log.info("Build Lucene documents for index "+this.indexId);
		// retrieve the list of IRIs
        List<IRI> iris = this.iriFetcher.fetchIrisToIndex(repository);
        log.info("Fetched "+iris.size()+" iris to be indexed");
        if(iris.size() == 0) {
        	log.warn("No IRIs found to be indexed in index "+indexId);
        }
        
        // fetch the labels for the list of IRIs
        int count = this.labelFetcher.fetchLabels(iris, repository, new LabelEntryHandler() {

			@Override
			public void handle(LabelEntry entry) {
				log.debug("Processing "+entry);
				Document doc = new Document();
				
				doc.add(new StringField(IndexFields.SOURCE_FIELD, sourceId, Field.Store.YES));
				// we need to store it to retrieve distinct index values
				doc.add(new StringField(IndexFields.INDEX_FIELD, indexId, Field.Store.YES));
				doc.add(new StringField(IndexFields.IRI_FIELD, entry.getIri().stringValue(), Field.Store.YES));
				doc.add(new TextField(IndexFields.LABEL_FIELD, entry.getLabel().stringValue(), Field.Store.YES));
				// we store the language of the label, if there is one
				if(entry.getLabel().getLanguage().isPresent()) {
					doc.add(new StringField(IndexFields.LANG_FIELD, entry.getLabel().getLanguage().get(), Field.Store.YES));
				} else {
					doc.add(new StringField(IndexFields.LANG_FIELD, "null", Field.Store.YES));
				}
				if(entry.getPrefLabel() != null) {
					doc.add(new TextField(IndexFields.PREF_LABEL_FIELD, entry.getPrefLabel().stringValue(), Field.Store.YES));
				}
				
				String pk = buildPk(indexId, entry.getIri().stringValue(), entry.getLabel().stringValue(), entry.getLabel().getLanguage().orElse(""));
				doc.add(new StringField(IndexFields.PRIMARY_KEY_FIELD, pk, Field.Store.NO));
				
				handler.handleDocument(doc);				
			}
        	
        });
        
        log.info("Fetched "+count+" labels to be indexed");
        if(count == 0) {
        	log.error("Could not fetch any label for index "+indexId+" (labelFetcher = "+this.labelFetcher.getClass()+")");
        }
	}
	
    public static String buildPk(String index, String iri, String label, String lang) {
    	return index+"_"+iri+"_"+label+"_"+lang;
    }
	
	public interface LuceneDocumentHandler {
		public void handleDocument(Document doc);
	}
	
}
