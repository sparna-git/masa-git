package fr.humanum.openarchaeo.federation.repository;

import java.util.List;

import org.eclipse.rdf4j.repository.Repository;

public interface FederationRepositoryBuilder {

	public Repository buildRepository(List<String> endpoints, String query);
	
}
