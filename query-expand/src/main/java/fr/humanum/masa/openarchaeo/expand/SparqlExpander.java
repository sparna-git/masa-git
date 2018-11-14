package fr.humanum.masa.openarchaeo.expand;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.algebra.walker.ElementWalker_New;
import org.apache.jena.sparql.expr.ExprVisitorBase;

public class SparqlExpander {

	protected SparqlExpansionConfig config;
	protected boolean strict = false;

	public SparqlExpander(SparqlExpansionConfig config) {
		this(config, false);
	}
	
	public SparqlExpander(SparqlExpansionConfig config, boolean strict) {
		super();
		this.config = config;
		this.strict = strict;
	}
	
	public String expand(String sparql) {
		Query q = QueryFactory.create(sparql);
		SparqlExpansionVisitor expansionVisitor = new SparqlExpansionVisitor(this.config, strict);
		ElementWalker_New.walk(q.getQueryPattern(), expansionVisitor, new ExprVisitorBase());
		String result = q.toString(Syntax.syntaxSPARQL_11);
		return result;
	}
	
}
