package fr.humanum.masa.federation.index;

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
		// retrieve the list of IRIs
        List<IRI> iris = this.iriFetcher.fetchLabels(repository);
        // fetch the labels for the list of IRIs
        this.labelFetcher.fetchLabels(iris, repository, new LabelEntryHandler() {

			@Override
			public void handle(LabelEntry entry) {
				log.debug("Processing "+entry);
				Document doc = new Document();
				
				doc.add(new StringField(IndexFields.SOURCE_FIELD, sourceId, Field.Store.YES));
				// we need to store it to retrieve distinct index values
				doc.add(new StringField(IndexFields.INDEX_FIELD, indexId, Field.Store.YES));
				doc.add(new StringField(IndexFields.IRI_FIELD, entry.getIri().stringValue(), Field.Store.YES));
				doc.add(new TextField(IndexFields.LABEL_FIELD, entry.getLabel().stringValue(), Field.Store.YES));
				if(entry.getPrefLabel() != null) {
					doc.add(new TextField(IndexFields.PREF_LABEL_FIELD, entry.getPrefLabel().stringValue(), Field.Store.YES));
				}
				
				String pk = buildPk(indexId, entry.getIri().stringValue(), entry.getLabel().stringValue());
				doc.add(new StringField(IndexFields.PRIMARY_KEY_FIELD, pk, Field.Store.NO));
				
				handler.handleDocument(doc);				
			}
        	
        });
	}
	
    public static String buildPk(String index, String iri, String label) {
    	return index+"_"+iri+"_"+label;
    }
	
	public interface LuceneDocumentHandler {
		public void handleDocument(Document doc);
	}
	
}
