package fr.humanum.openarchaeo.explorateur;

import java.util.Iterator;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlFetchExtraProperty {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	public String fetchExtraProperty(
			String inputSparql,
			final Path extraPropertyPath,
			final String initialPropertyVariable,
			final String extraPropertyVariable,
			boolean optional
	) {

		final Query query = QueryFactory.create(inputSparql);
		FetchExtraPropertyVisitor visitor = new FetchExtraPropertyVisitor(extraPropertyPath, initialPropertyVariable, extraPropertyVariable, optional);
		query.getQueryPattern().visit(visitor);
		if(visitor.isAddedExtraProperty()) {
			// Ajout des variables supplementaires au select                             
			query.addResultVar(extraPropertyVariable);
		}

		String result = query.toString(Syntax.syntaxSPARQL_11);
		return result;
	}
	
	class FetchExtraPropertyVisitor extends ElementVisitorBase {
		protected Path extraPropertyPath;
		protected String initialPropertyVariable;
		protected String extraPropertyVariable;
		protected boolean optional = false;
		
		private boolean addedExtraProperty = false;
		
		public FetchExtraPropertyVisitor(
				Path extraPropertyPath,
				String initialPropertyVariable,
				String extraPropertyVariable,
				boolean optional
		) {
			super();
			this.extraPropertyPath = extraPropertyPath;
			this.initialPropertyVariable = initialPropertyVariable;
			this.extraPropertyVariable = extraPropertyVariable;
			this.optional = optional;
		}
		
		
		public FetchExtraPropertyVisitor(
				Path extraPropertyPath,
				String initialPropertyVariable,
				String extraPropertyVariable
		) {
			this(extraPropertyPath, initialPropertyVariable, extraPropertyVariable, false);
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
							elementToAdd = new ElementPathBlock();
							((ElementPathBlock)elementToAdd).addTriple(new TriplePath(
									t.getSubject(),
									extraPropertyPath,
									NodeFactory.createVariable(extraPropertyVariable))
							);
							if(this.optional) {
								elementToAdd = new ElementOptional(elementToAdd);
							}
						}
					}							
				}
			}
			
			if(elementToAdd != null) {
				log.debug("Added extra property to fetch in SPARQL : ?"+initialPropertyVariable+" "+extraPropertyPath.toString()+" ?"+extraPropertyVariable);
				if(this.optional) {
					log.debug("Extra criteria is optional");
				}
				el.addElement(elementToAdd);
				this.addedExtraProperty = true;
				
			}
		}

		public boolean isAddedExtraProperty() {
			return addedExtraProperty;
		}
	}
}
