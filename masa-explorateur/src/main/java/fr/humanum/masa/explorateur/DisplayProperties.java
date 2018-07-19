package fr.humanum.masa.explorateur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.BindingHashMap;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.vocabulary.RDF;

public class DisplayProperties {

	private SparqlProperty property;

	protected Model model;

	public DisplayProperties(SparqlProperty property, Model model) {
		this.property = property;
		this.model=model;
	}


	public Query getLatitude(String request,String labelProperty){
		return getQuery(request,labelProperty,"latitude");
	}

	public Query getLabelOfType(String request,String labelProperty){
		return getQuery(request,labelProperty,"label");
	}
	

	public Query getLongitude(String request,String labelProperty){

		return getQuery(request,labelProperty,"longitude");
	}

	public Query getStartAndEndDate(String request,String startDateProperty, String endDateProperty){
		
		return getDatesQuery(request,startDateProperty,endDateProperty);
	}



	private Query getDatesQuery(String request, final String startDateProperty, final String endDateProperty) {
		// TODO Auto-generated method stub
		final Query query = QueryFactory.create(request);
		Element where = query.getQueryPattern();

		where.visit(new ElementVisitorBase() {

			@Override
			public void visit(ElementGroup el) {

				List<Element> elementsToAdd = new ArrayList<Element>();
				for (Element e : el.getElements()) {

					if(e instanceof ElementPathBlock) {

						ElementPathBlock epb = (ElementPathBlock)e;

						Iterator<TriplePath> triples = epb.patternElts();						

						while(triples.hasNext()) {
							TriplePath t = triples.next();

							if(t.getSubject().getName().equals("this"))  {

								ElementPathBlock newEpb1= new ElementPathBlock();
								newEpb1.addTriple(new TriplePath(t.getSubject(), PathParser.parse(startDateProperty, property.pmap),NodeFactory.createVariable("start")));						
								elementsToAdd.add(newEpb1);

								ElementPathBlock newEpb2= new ElementPathBlock();
								newEpb2.addTriple(new TriplePath(t.getSubject(), PathParser.parse(endDateProperty, property.pmap),NodeFactory.createVariable("end")));
								elementsToAdd.add(newEpb2);

								//Ajout des variables supplémentaires au select                    
								query.setQuerySelectType();                               
								query.addResultVar("start");
								query.addResultVar("end");
							}
						}
					}
				}
				for (Element element : elementsToAdd) {
					el.addElement(element);
				}
			}

			@Override
			public void visit(ElementPathBlock el) {
				System.out.println(el);
			}

		});

		return query;
	}


	private Query getQuery(String inputSparql, final String label, final String variable) {

		final Query query = QueryFactory.create(inputSparql);

		Element where = query.getQueryPattern();

		System.out.println(where.getClass().getName());

		where.visit(new ElementVisitorBase() {

			@Override
			public void visit(ElementGroup el) {

				List<Element> elementsToAdd = new ArrayList<Element>();
				for (Element e : el.getElements()) {
					System.out.println(e.getClass().getName());

					if(e instanceof ElementPathBlock) {

						ElementPathBlock epb = (ElementPathBlock)e;

						Iterator<TriplePath> triples = epb.patternElts();						


						while(triples.hasNext()) {
							TriplePath t = triples.next();

							//type
							if(t.getSubject().getName().equals("this")) {

								ElementPathBlock newEpb2= new ElementPathBlock();
								newEpb2.addTriple(new TriplePath(t.getSubject(), PathParser.parse(label, property.pmap),NodeFactory.createVariable(variable)));
								elementsToAdd.add(newEpb2);

								//Ajout des variables supplémentaires au select                    
								query.setQuerySelectType();                               
								query.addResultVar(variable);

							}
						}							
					}
				}
				for (Element element : elementsToAdd) {
					el.addElement(element);
				}
			}

			@Override
			public void visit(ElementPathBlock el) {
				System.out.println(el);
			}

		});

		return query;

	}

	//	public static void main(String...strings) throws Exception {
	//		DisplayProperties me = new DisplayProperties(new SparqlProperty(), ModelFactory.createDefaultModel());
	//		String TESTB = "SELECT ?this WHERE { "
	//				+ "?this a <http://exemple.com/type/Thing> . "
	//				+ "}";
	//
	//		System.out.println(me.getDatesQuery(TESTB,"rdf:date","rdf:date").toString(Syntax.syntaxSPARQL_11));
	//	}

}
