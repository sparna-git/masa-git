package fr.humanum.masa.explorateur;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.BindingHashMap;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.vocabulary.RDF;

public class SemanticExpander {

	private SparqlProperty property;
	
	public SemanticExpander(SparqlProperty property) {
		this.property = property;
	}


	public String expand(String inputSparql) {

		Query query = QueryFactory.create(inputSparql);

		Element where = query.getQueryPattern();

		System.out.println(where.getClass().getName());

		where.visit(new ElementVisitorBase() {

			@Override
			public void visit(ElementGroup el) {

				List<Element> elementsToAdd = new ArrayList<Element>();
				List<Element> elements= el.getElements();
				for (Element e : elements) {
					System.out.println(e.getClass().getName());

					if(e instanceof ElementPathBlock) {
						
						ElementPathBlock epb = (ElementPathBlock)e;

						Iterator<TriplePath> triples = epb.patternElts();						

					
						while(triples.hasNext()) {
							TriplePath t = triples.next();
							
							//type
							if(t.getPredicate().isURI() && t.getPredicate().getURI().equals(RDF.type.toString())) {


								if(t.getObject().isURI() && property.getConfigType().containsKey(t.getObject().getURI())) {
									
									//creation du triplet en prenant le sujet d'origine, le path et la creation d'une nouvelle variable "type"

									ElementPathBlock newEpb= new ElementPathBlock();
									
									String var="type_"+UUID.randomUUID().toString().substring(0, 4);
									
									
									newEpb.addTriple(new TriplePath(t.getSubject(), t.getPath(),NodeFactory.createVariable(var.replaceAll("-", "a"))));
									
									//retire le triplet de la clause where
									triples.remove();


									ElementData valuesClause = new ElementData();

						
									valuesClause.add(Var.alloc(newEpb.patternElts().next().getObject()));

									
									for(String uri: property.getConfigType().get(t.getObject().getURI())){
										BindingHashMap bhm1 = new BindingHashMap();
										bhm1.add(Var.alloc(newEpb.patternElts().next().getObject()),NodeFactory.createURI(uri));
										valuesClause.add(bhm1);

									}
								
								
									elementsToAdd.add(newEpb);
									elementsToAdd.add(valuesClause);
								}
							}

							// properties
							if(t.getPredicate().isURI() && property.getConfigProperty().containsKey(t.getPredicate().getURI())) {

									//retire le triplet de la clause where
									triples.remove();

									ElementPathBlock epb1= new ElementPathBlock();
									

									Path expandedPath = property.getConfigProperty().get(t.getPredicate().getURI());
									
									epb1.addTriple(new TriplePath(t.getSubject(),expandedPath, t.getObject()));
									
									elementsToAdd.add(epb1);
											
									
								
							}
							System.out.println(t.getPredicate().getURI());							
						}
					
						if(epb.getPattern().isEmpty()){
							el.getElements().remove(epb);
							break;
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

		return query.toString(Syntax.syntaxSPARQL_11);

	}

}
