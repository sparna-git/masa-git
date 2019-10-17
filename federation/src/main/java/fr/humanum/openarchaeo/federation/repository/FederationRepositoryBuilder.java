package fr.humanum.openarchaeo.federation.repository;

import java.util.List;

import org.eclipse.rdf4j.repository.Repository;

import fr.humanum.openarchaeo.federation.source.FederationSource;

public interface FederationRepositoryBuilder {

	public Repository buildRepository(List<FederationSource> sources, String query);
	
}
