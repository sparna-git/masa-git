package fr.humanum.openarchaeo.federation.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class OpenArchaeoIndexAnalyzer extends Analyzer {

	/**
	 *  Uses a custom analyzer so we can do EdgeNGramFiltering for autocomplete
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		if(fieldName.equals(IndexFields.LABEL_FIELD)) {
		    Tokenizer source = new StandardTokenizer();
		    TokenStream result = new StandardFilter(source);
		    // lowercasing
		    result = new LowerCaseFilter(result);
		    // remove accents
		    result = new ASCIIFoldingFilter(result);
		    // reconcatenate tokens together to be able to do autocompletion on multiple words (2 or 3 words max)
		    ShingleFilter sf = new ShingleFilter(result, 2, 3);
		    // still output single words
		    sf.setOutputUnigrams(true);
		    // here is the magic for autocomplete : edgeNGrams starting at 3 letters
		    result = new EdgeNGramTokenFilter(result, 3, 20);
		    return new TokenStreamComponents(source, result);
		} else {
			// everything else is keyword, indexed as is
			Tokenizer source = new KeywordTokenizer();
		    return new TokenStreamComponents(source);
		}
	}

}
