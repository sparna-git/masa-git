package fr.humanum.masa.expand;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.algebra.walker.ElementWalker_New;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingHashMap;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprFunction2;
import org.apache.jena.sparql.expr.ExprFunctionN;
import org.apache.jena.sparql.expr.ExprVisitorBase;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlExpansionVisitor extends ElementVisitorBase {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private SparqlExpansionConfig config;
	
	private Set<String> alreadyUsedVars = new HashSet<String>();
	
	public SparqlExpansionVisitor(SparqlExpansionConfig config) {
		this.config = config;
	}

	
	
	@Override
	public void visit(ElementGroup el) {
	
		boolean untouched = true;
		
		// gather all the variables subject of an rdf:type
		VariableTypesGatherer gatherer = new VariableTypesGatherer();
		ElementWalker_New.walk(el, gatherer, new ExprVisitorBase());
		// now attempt a replacement in values and filter clauses
		ValuesExpansionVisitor vev = new ValuesExpansionVisitor(gatherer.getVariableTypes());
		FilterExpansionVisitor fev = new FilterExpansionVisitor(gatherer.getVariableTypes());
		ElementWalker_New.walk(el, vev, fev);

		
		// the new values clause to add at the end of the group
		List<ElementData> valuesClauseToAdd = new ArrayList<ElementData>();
		for (Element e : el.getElements()) {

			if(e instanceof ElementPathBlock) {
				ElementPathBlock epb = (ElementPathBlock)e;
				log.debug("Examining an ElementPathBlock with "+epb.getPattern().size()+" paths.");
				
				// the new triples to add at the end of the block
				List<TriplePath> newTriplesToAdd = new ArrayList<TriplePath>();
				
				// iterate on triples
				Iterator<TriplePath> triples = epb.patternElts();
				while(triples.hasNext()) {
					TriplePath t = triples.next();
					
					//type
					if(
							t.getPredicate().isURI()
							&&
							t.getPredicate().getURI().equals(RDF.type.toString())
							&&
							t.getObject().isURI()
							&&
							config.getTypeMapping(t.getObject().getURI()) != null
					) {							
						log.debug("Found a known type : <"+t.getObject().getURI()+">");
						// creation du triplet en prenant le sujet d'origine, le path et la creation d'une nouvelle variable "type"							
						String varName = null;
						do {
							// check already inserted vars to avoid name colisions
							varName = "t_"+UUID.randomUUID().toString().substring(0, 3);
						} while(this.alreadyUsedVars.contains(varName));
						this.alreadyUsedVars.add(varName);						
						
						Node var = NodeFactory.createVariable(varName.replaceAll("-", ""));
						newTriplesToAdd.add(new TriplePath(t.getSubject(), t.getPath(), var));
						
						//retire le triplet courant de la clause where
						triples.remove();

						ElementData valuesClause = new ElementData();
						valuesClause.add(Var.alloc(var));
						
						for(String uri: config.getTypeMapping(t.getObject().getURI())){
							BindingHashMap bhm = new BindingHashMap();
							bhm.add(Var.alloc(var),NodeFactory.createURI(uri));
							valuesClause.add(bhm);
						}

						valuesClauseToAdd.add(valuesClause);
						log.debug("Type <"+t.getObject().getURI()+"> replaced by var ?"+varName+" with values "+config.getTypeMapping(t.getObject().getURI()));
					}
					
					// properties
					if(t.getPredicate().isURI() && config.getPathMapping(t.getPredicate().getURI()) != null) {
						log.debug("Found a known property : <"+t.getPredicate().getURI()+">");
						
						//retire le triplet de la clause where
						triples.remove();

						Path expandedPath = config.getPathMapping(t.getPredicate().getURI());
						newTriplesToAdd.add(new TriplePath(
								t.getSubject(),
								expandedPath,
								t.getObject())
						);
						log.debug("Property <"+t.getPredicate().getURI()+"> replaced by path "+expandedPath.toString());
					}
				}	
				
				// add new triples
				for (TriplePath aTriplePath : newTriplesToAdd) {
					epb.addTriplePath(aTriplePath);
					untouched = false;
				}
				
			}
		}
		
		// add values clauses
		for (Element element : valuesClauseToAdd) {
			el.addElement(element);
			untouched = false;
		}
		
		if(untouched) {
			log.debug("Query left untouched");
		}
	}
	
	class VariableTypesGatherer extends ElementVisitorBase {
		
		protected Set<String> variableTypes = new HashSet<String>();

		@Override
		public void visit(ElementPathBlock epb) {
			
			// iterate on triples
			Iterator<TriplePath> triples = epb.patternElts();
			while(triples.hasNext()) {
				TriplePath t = triples.next();
				
				//type
				if(
						t.getPredicate().isURI()
						&&
						t.getPredicate().getURI().equals(RDF.type.toString())
						&&
						t.getObject().isVariable()
				) {	
					variableTypes.add(t.getObject().getName());
					log.debug("Found a variable object of rdf:type : ?"+t.getObject().getName());
				}
			}
		}

		public Set<String> getVariableTypes() {
			return variableTypes;
		}
		
	}
	
	class ValuesExpansionVisitor extends ElementVisitorBase {
		
		protected Set<String> variableTypes = new HashSet<String>();

		public ValuesExpansionVisitor(Set<String> variableTypes) {
			super();
			this.variableTypes = variableTypes;
		}

		@Override
		public void visit(ElementData ed) {
			List<Binding> bindingsToRemove = new ArrayList<Binding>();
			List<Binding> bindingsToAdd = new ArrayList<Binding>();
			for (Binding aBinding : ed.getRows()) {
				// now look if one of the variable types is bound in this clause
				for (String aTypeVar : variableTypes) {
					if(aBinding.contains(Var.alloc(aTypeVar))) {
						Node value = aBinding.get(Var.alloc(aTypeVar));
						
						if(value.isURI() && config.getTypeMapping(value.getURI()) != null) {
							log.debug("Matched a known binding for a variable type : ?"+aTypeVar+" with value "+value);
							bindingsToRemove.add(aBinding);
							
							// now add new bindings
							for(String uri: config.getTypeMapping(value.getURI())){
								BindingHashMap bhm = new BindingHashMap();
								bhm.add(Var.alloc(aTypeVar),NodeFactory.createURI(uri));
								bindingsToAdd.add(bhm);
							}							
						}
					}
				}
			}
			
			// now remove bindings to be removed
			for (Binding aBindingToRemove : bindingsToRemove) {
				ed.getRows().remove(aBindingToRemove);
			}
			
			// add bindings to add
			for (Binding aBindingToAdd : bindingsToAdd) {
				ed.add(aBindingToAdd);
			}
		}
		
		public void visit(ElementFilter ef) {
			Expr expr =	ef.getExpr();
		}
	}
	
	class FilterExpansionVisitor extends ExprVisitorBase {

		protected Set<String> variableTypes = new HashSet<String>();

		public FilterExpansionVisitor(Set<String> variableTypes) {
			super();
			this.variableTypes = variableTypes;
		}
		
		
		
		@Override
		public void visit(ExprFunction2 func) {
			if(func instanceof E_Equals) {
				// test both of the operands to see if one corresponds to one of our variable
				if(func.getArg1().isVariable() && variableTypes.contains(func.getArg1().asVar().getVarName())) {
					log.debug("Found variable ?"+func.getArg1().asVar().getVarName()+" in an equality condition in a FILTER");
					// then we should replace this equality condition with an IN with all corresponding types
					// TODO
				}
			}
		}
		
		@Override
		public void visit(ExprFunctionN func) {
			log.debug(func.getFunction().getFunctionName(null));
		}
	}
	
	public static void main(String...strings) throws Exception {
		final String SPARQL = "SELECT ?x WHERE {?x a <http://exemple.com/type/Thing> . ?x <http://test.fr/onto#hasUncle> ?uncle . ?x <http://test.fr/onto#hasFamily> ?family } LIMIT 10";
		Query query = QueryFactory.create(SPARQL);
		
		Model m = ModelFactory.createDefaultModel();
		String testConfig = ""
				+ "@prefix owl: <http://www.w3.org/2002/07/owl#> ."+"\n"
				+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."+"\n"
				+ "@prefix : <http://test.fr/onto#> ."+"\n"
				+ "<http://exemple.com/type/Type1> rdfs:subClassOf <http://exemple.com/type/Thing> . " +"\n"
				+ "<http://exemple.com/type/Type2> rdfs:subClassOf <http://exemple.com/type/Thing> ." +"\n"
				+ "<http://exemple.com/type/SubType22> rdfs:subClassOf <http://exemple.com/type/Type2> ." +"\n"
				+ ":hasGrandParent a owl:ObjectProperty ;" +"\n"
				+ "                owl:propertyChainAxiom ( :hasParent" +"\n"
				+ "                                         :hasParent" +"\n"
				+ "                                       ) ."+"\n"
				+ ":hasUncle owl:propertyChainAxiom \":hasFather/:hasBrother\" ."+"\n"
				+ ":hasUncle owl:propertyChainAxiom \":hasMother/:hasBrother\" ."+"\n"
				+ ":hasUncle rdfs:subPropertyOf :hasFamily ."+"\n"
				+ ":hasGrandParent rdfs:subPropertyOf :hasFamily ."+"\n";
		RDFDataMgr.read(m, new ByteArrayInputStream(testConfig.getBytes()), Lang.TURTLE);
		
		SparqlExpansionConfigOwlSupplier supplier = new SparqlExpansionConfigOwlSupplier(m);
		
		ElementWalker_New.walk(query.getQueryPattern(), new SparqlExpansionVisitor(supplier.get()));
		System.out.println(query.toString(Syntax.syntaxSPARQL_11));
		
		final String SPARQL2 = "SELECT ?x WHERE {?x a ?type . VALUES ?type { <http://exemple.com/type/Thing> } FILTER(?type = <http://exemple.com/type/Thing> && ?type IN (<http://exemple.com/type/Thing>)) } LIMIT 10";
		Query query2 = QueryFactory.create(SPARQL2);
		ElementWalker_New.walk(query2.getQueryPattern(), new SparqlExpansionVisitor(supplier.get()), new ExprVisitorBase());
		System.out.println(query2.toString(Syntax.syntaxSPARQL_11));
	}
	
	
}
