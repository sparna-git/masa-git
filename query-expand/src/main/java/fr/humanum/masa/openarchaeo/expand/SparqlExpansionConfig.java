package fr.humanum.masa.openarchaeo.expand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.sparql.path.Path;

public class SparqlExpansionConfig {
	
	protected Map<String, Set<ClassMapping>> typesMapping;
	protected Map<String, Path> pathsMapping;
	
	public SparqlExpansionConfig() {
		this.typesMapping = new HashMap<String, Set<ClassMapping>>();
		this.pathsMapping = new HashMap<String, Path>();
	}
	
	public static boolean isSingleHasRestrictionMapping(Set<ClassMapping> mappings) {
		return mappings.size() == 1 && mappings.iterator().next().isHasValueRestrictionMapping();
	}
	
	public void addTypeMapping(String oldType, Set<String> newTypes) {
		this.typesMapping.put(oldType, newTypes.stream().map(t -> new ClassMapping(t)).collect(Collectors.toSet()));
	}
	
	public void addTypeMapping(String oldType, String newType) {
		this.addTypeMapping(oldType, new ClassMapping(newType));
	}
	
	public void addTypeMapping(String oldType, ClassMapping cm) {
		if(!this.typesMapping.containsKey(oldType)) {
			this.typesMapping.put(oldType, new HashSet<ClassMapping>());
		}
		this.typesMapping.get(oldType).add(cm);
	}
	
	public void addPathMapping(String property, Path path) {
		this.pathsMapping.put(property, path);
	}	
	
	public Map<String, Set<ClassMapping>> getTypesMapping() {
		return typesMapping;
	}
	
	public Set<ClassMapping> getTypeMapping(String oldType) {
		return typesMapping.get(oldType);
	}

	public Path getPathMapping(String propertyUri) {
		return pathsMapping.get(propertyUri);
	}
	
	class ClassMapping {
		protected String clazz;
		protected String onProperty;
		protected String hasValue;
		
		public ClassMapping(String clazz) {
			super();
			this.clazz = clazz;
		}
		
		public ClassMapping(String clazz, String onProperty, String hasValue) {
			super();
			this.clazz = clazz;
			this.onProperty = onProperty;
			this.hasValue = hasValue;
		}

		public boolean isHasValueRestrictionMapping() {
			return (onProperty != null) && (hasValue != null);
		}
		
		public String getClazz() {
			return clazz;
		}
		public void setClazz(String clazz) {
			this.clazz = clazz;
		}
		public String getOnProperty() {
			return onProperty;
		}
		public void setOnProperty(String onProperty) {
			this.onProperty = onProperty;
		}
		public String getHasValue() {
			return hasValue;
		}
		public void setHasValue(String hasValue) {
			this.hasValue = hasValue;
		}
	}
	
}