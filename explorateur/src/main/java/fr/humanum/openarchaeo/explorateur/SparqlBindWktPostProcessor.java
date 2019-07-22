package fr.humanum.openarchaeo.explorateur;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_StrConcat;
import org.apache.jena.sparql.expr.E_StrDatatype;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlBindWktPostProcessor implements SparqlPostProcessor {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String initialPropertyVariable;
	protected String extraPropertyVariable;
	
	public SparqlBindWktPostProcessor(String initialPropertyVariable, String extraPropertyVariable) {
		this.extraPropertyVariable = extraPropertyVariable;
		this.initialPropertyVariable = initialPropertyVariable;
	}
	
	public String postProcess(String inputSparql) {
		final Query query = QueryFactory.create(inputSparql);
		AddBindWktVisitor visitor = new AddBindWktVisitor(initialPropertyVariable, extraPropertyVariable);
		query.getQueryPattern().visit(visitor);
		if(visitor.isAddedExtraProperty()) {
			// Ajout des variables supplementaires au select                             
			query.addResultVar(extraPropertyVariable);
		}

		String result = query.toString(Syntax.syntaxSPARQL_11);
		return result;		
	}
	
	class AddBindWktVisitor extends ElementVisitorBase {

		protected String extraPropertyVariable;
		protected String initialPropertyVariable;
		
		private boolean addedExtraProperty = false;
		
		public AddBindWktVisitor(
				String initialPropertyVariable,
				String extraPropertyVariable
		) {
			super();
			this.extraPropertyVariable = extraPropertyVariable;
			this.initialPropertyVariable = initialPropertyVariable;
		}


		@Override
		public void visit(ElementGroup el) {

			Element elementToAdd = null;
			for (Element e : el.getElements()) {
				if(e instanceof ElementPathBlock) {
					ElementPathBlock epb = (ElementPathBlock)e;
					Iterator<TriplePath> triples = epb.patternElts();						

					while(triples.hasNext()) {
						TriplePath t = triples.next();

						// found our variable
						if(t.getSubject().getName().equals(initialPropertyVariable) && elementToAdd == null) {
							elementToAdd = new ElementBind(Var.alloc(this.extraPropertyVariable),
									new E_StrDatatype(
											new E_StrConcat(new ExprList(Arrays.asList(new Expr[] { 
													new NodeValueString("POINT("),
													new ExprVar(NodeFactory.createVariable("longitude")),
													new NodeValueString(" "),
													new ExprVar(NodeFactory.createVariable("latitude")),
													new NodeValueString(")"),
											})))
											,
											new NodeValueNode(NodeFactory.createURI(XMLSchema.STRING.stringValue()))
									)
							);
						}
					}							
				}
			}
			
			if(elementToAdd != null) {
				log.debug("Added BIND WKT element");
				el.addElement(elementToAdd);
				this.addedExtraProperty = true;
			}
		}

		public boolean isAddedExtraProperty() {
			return addedExtraProperty;
		}
	}
}
