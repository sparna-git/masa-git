package fr.humanum.masa.expand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.sparql.path.Path;

public class SparqlExpansionConfig {
	
	protected Map<String, Set<String>> typesMapping;
	protected Map<String, Path> pathsMapping;
	
	public SparqlExpansionConfig() {
		this.typesMapping = new HashMap<String, Set<String>>();
		this.pathsMapping = new HashMap<String, Path>();
	}
	
	public void addTypeMapping(String oldType, Set<String> newTypes) {
		this.typesMapping.put(oldType, newTypes);
	}
	
	public void addTypeMapping(String oldType, String newType) {
		if(!this.typesMapping.containsKey(oldType)) {
			this.typesMapping.put(oldType, new HashSet<String>());
		}
		this.typesMapping.get(oldType).add(newType);
	}
	
	public void addPathMapping(String property, Path path) {
		this.pathsMapping.put(property, path);
	}
	
	public Set<String> getTypeMapping(String typeUri) {
		return typesMapping.get(typeUri);
	}
	
	public Path getPathMapping(String propertyUri) {
		return pathsMapping.get(propertyUri);
	}
	
}