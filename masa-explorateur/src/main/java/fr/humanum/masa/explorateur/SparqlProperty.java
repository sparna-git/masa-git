package fr.humanum.masa.explorateur;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;
import org.apache.jena.sparql.path.PathParser;


public class SparqlProperty {



	private  static final String createdBy="http://exemple.com/link/created_by";

	public static final   String typeThing="http://exemple.com/type/Thing";

	public static final   String typeActor="http://exemple.com/type/Actor";

	public PrefixMapping pmap;

	private HashMap <String, Path> configProperty;

	private HashMap<String, List<String>> configType;

	


	public SparqlProperty(Properties properties) {
		// TODO Auto-generated constructor stub
		this.configProperty=new HashMap<>();
		this.configType=new HashMap<>();
		PathParser parser=new PathParser();
		this.pmap = new PrefixMappingImpl() ;
		pmap.setNsPrefix("onto", "http://cidoc-crm.org/ontology/") ;
		pmap.setNsPrefix("ex", "http://ex.fr");
		pmap.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		pmap.setNsPrefix("rdf", "http://www.w3.org/2000/01/rdf-schema#");
		pmap.setNsPrefix("skos", "http://www.w3.org/2008/05/skos#");
		pmap.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
		pmap.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		pmap.setNsPrefix("edm", "http://www.europeana.eu/schemas/edm/");
		pmap.setNsPrefix("ore", "http://www.openarchives.org/ore/terms/");
		
		
		String createdBya=properties.getProperty("createdBy");
		String thing=properties.getProperty("thing");
		String actor=properties.getProperty("actor");
		this.configProperty.put(createdBy, parser.parse(createdBya, pmap));
		this.configType.put(typeThing,Arrays.asList(thing.trim().split("[,]")));
		this.configType.put(typeActor,Arrays.asList(actor.trim().split("[,]")));

	}

	public PrefixMapping getPrefixMapping(){
		return pmap;
	}

	public HashMap<String, Path> getConfigProperty() {
		return configProperty;
	}

	public HashMap<String, List<String>> getConfigType() {
		return configType;
	}

	

}
