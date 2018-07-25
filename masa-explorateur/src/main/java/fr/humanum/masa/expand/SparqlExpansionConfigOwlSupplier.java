package fr.humanum.masa.expand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlExpansionConfigOwlSupplier implements Supplier<SparqlExpansionConfig> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// source Model from which config is read
	private Model model;
	
	public SparqlExpansionConfigOwlSupplier(Model model) {
		super();
		this.model = model;
	}


	@Override
	public SparqlExpansionConfig get() {
		
		SparqlExpansionConfig result = new SparqlExpansionConfig();
		
		// find all objects of subClassOf
		NodeIterator objectsOfSubClasseOf = this.model.listObjectsOfProperty(RDFS.subClassOf);
		while(objectsOfSubClasseOf.hasNext()) {
			// recurse for each one to find all the recursive subClasses
			Resource aClass = objectsOfSubClasseOf.next().asResource();
			Set<Resource> subClasses = followInverseTransitiveRec(aClass, RDFS.subClassOf);
			Set<String> subClassesString = subClasses.stream().map(s -> s.getURI()).collect(Collectors.toSet());
			log.debug("Parsed recursive rdfs:subClassOf for "+aClass.getURI()+" : "+subClassesString);
			result.addTypeMapping(aClass.getURI(), subClassesString);
		}
		
		// parse property chains and generate property mappings
		StmtIterator propertyChains = this.model.listStatements(null, OWL2.propertyChainAxiom, (RDFNode)null);
		
		while(propertyChains.hasNext()) {
			Statement aChain = propertyChains.next();
			log.debug("Parsing an owl:propertyChainAxiom statement : "+aChain.toString());
			
			// this is the type we are mapping from
			String subjectString = aChain.getSubject().getURI();
			String sparqlPath = null;
			// standard OWL case : the value is a resource and can be parsed as an RDF list...
			if(aChain.getObject().isResource() && aChain.getObject().canAs(RDFList.class)) {
				log.debug("owl:propertyChainAxiom value is an RDF list resource, turning it into SPARQL path");
				RDFList rdfList = aChain.getObject().as(RDFList.class);

				List<RDFNode> itemsList = rdfList.iterator().toList();
				sparqlPath = "<"+String.join(">/<", itemsList.stream().map(s -> s.asResource().getURI()).collect(Collectors.toList()))+">";
				
			// extended case : the value is a Literal expressed as a SPARQL property path
			} else if(aChain.getObject().isLiteral()) {
				log.debug("owl:propertyChainAxiom value is a Literal value");
				sparqlPath = aChain.getObject().asLiteral().getLexicalForm();
			} else {
				log.debug("owl:propertyChainAxion object has an unknown structure.");
			}
			
			if(sparqlPath != null) {
				// combine with existing path if needed
				Path existingPath = result.getPathMapping(subjectString);
				if(existingPath != null) {
					log.debug("A path is already mapped to "+subjectString+" : "+existingPath.toString()+", will concatenate paths");
					sparqlPath = "("+existingPath.toString()+")|("+sparqlPath+")";
				}
				log.debug("Will attempt parsing of SPARQL property path : "+sparqlPath);
				
				result.addPathMapping(subjectString, PathParser.parse(sparqlPath, model));
			}
		}
		
		// now also add property mappings based on subPropertyOf declarations
		
		// TODO : currently there is no combination of the propertyPathAxioms and the subProperties
		
		// find all objects of subClassOf
		NodeIterator objectsOfSubPropertyOf = this.model.listObjectsOfProperty(RDFS.subPropertyOf);
		while(objectsOfSubPropertyOf.hasNext()) {
			// recurse for each one to find all the recursive subProeprties
			Resource aProperty = objectsOfSubPropertyOf.next().asResource();
			Set<Resource> subProperties = followInverseTransitiveRec(aProperty, RDFS.subPropertyOf);
			Set<String> subPropertiesString = subProperties.stream().map(s -> s.getURI()).collect(Collectors.toSet());
			log.debug("Parsed recursive rdfs:subPropertiesOf for "+aProperty.getURI()+" : "+subPropertiesString);
			String pathString = "<"+String.join(">|<", subPropertiesString)+">";
			log.debug("Will attempt parsing of property path : "+pathString);
			result.addPathMapping(aProperty.getURI(), PathParser.parse(pathString, model));
		}
		
		log.debug("Built a SPARQL expansion config with "+result.typesMapping.size()+" types mappings and "+result.pathsMapping.size()+" property mappings");
		return result;	
	}
	
	private Set<Resource> followInverseTransitiveRec(Resource r, Property p) {
		StmtIterator subClassesOf = this.model.listStatements(null, p, r);
		Set<Resource> result = new HashSet<Resource>();
		while(subClassesOf.hasNext()) {
			Resource aSubClass = subClassesOf.next().getSubject();
			result.add(aSubClass);
			result.addAll(followInverseTransitiveRec(aSubClass, p));
		}
		return result;
	}
	
}
