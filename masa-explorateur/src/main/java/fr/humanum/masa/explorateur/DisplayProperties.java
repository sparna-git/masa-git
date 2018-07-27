package fr.humanum.masa.explorateur;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.path.PathParser;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayProperties {

	private Logger log= LoggerFactory.getLogger(this.getClass().getName());

	protected Model model;

	public DisplayProperties(Model model) {
		this.model=model;
	}


	public Query getLatitude(String request,String labelProperty){
		log.debug("- Lecture de la latitude");
		return getQuery(request,labelProperty,"latitude");
	}

	public Query getLabelOfType(String request,String labelProperty){
		log.debug("- Lecture des libellés");
		return getQuery(request,labelProperty,"label");
	}
	

	public Query getLongitude(String request,String labelProperty){
		log.debug("- Lecture de la longitude");
		return getQuery(request,labelProperty,"longitude");
	}

	public Query getStartAndEndDate(String request,String startDateProperty, String endDateProperty){
		log.debug("- Lecture des dates de débuts et fins");
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
								newEpb1.addTriple(new TriplePath(t.getSubject(), PathParser.parse(startDateProperty, ModelFactory.createDefaultModel()),NodeFactory.createVariable("start")));						
								elementsToAdd.add(newEpb1);

								ElementPathBlock newEpb2= new ElementPathBlock();
								newEpb2.addTriple(new TriplePath(t.getSubject(), PathParser.parse(endDateProperty, ModelFactory.createDefaultModel()),NodeFactory.createVariable("end")));
								elementsToAdd.add(newEpb2);

								//Ajout des variables supplementaires au select                    
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
								newEpb2.addTriple(new TriplePath(t.getSubject(), PathParser.parse(label, ModelFactory.createDefaultModel()),NodeFactory.createVariable(variable)));
								elementsToAdd.add(newEpb2);

								//Ajout des variables supplementaires au select                    
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


}
