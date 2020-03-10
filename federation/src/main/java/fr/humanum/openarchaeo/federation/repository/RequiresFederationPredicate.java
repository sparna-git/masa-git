package fr.humanum.openarchaeo.federation.repository;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.humanum.openarchaeo.federation.source.FederationSource;

public class RequiresFederationPredicate implements Predicate<RequiresFederationPredicate.QueryOverSources> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	
	public class QueryOverSources {
		public String query;
		public List<? extends FederationSource> sources;
		
		public QueryOverSources(String query, List<? extends FederationSource> sources) {
			super();
			this.query = query;
			this.sources = sources;
		}
	}

	@Override
	public boolean test(QueryOverSources queryOverSources) {
		log.debug("See if some referential properties are needed");
		final Query q = QueryFactory.create(queryOverSources.query);
		HasPredicateVisitor visitorLat = new HasPredicateVisitor("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
		HasPredicateVisitor visitorLong = new HasPredicateVisitor("http://www.w3.org/2003/01/geo/wgs84_pos#long");
		q.getQueryPattern().visit(visitorLat);
		q.getQueryPattern().visit(visitorLong);
		
		if(visitorLat.isFound() || visitorLong.isFound()) {
			log.debug("Referential properties are needed, federation is needed");
			return true;
		} else {
			log.debug("No referential properties needed, see if federation is still needed");
			
			if(queryOverSources.sources.size() == 1) {
				log.debug("Single source requested (and no referential properties) no federation needed");
				return false;
			} else {
				log.debug(queryOverSources.sources.size()+" sources requested, see if they query the same triplestore with different graphs");

				if(FederationSource.areQueryingSameEndpoint(queryOverSources.sources)) {
					log.debug(queryOverSources.sources.size()+" sources are actually querying the same endpoint with different graphs ("+queryOverSources.sources.get(0).getEndpoint()+"), no federation required");
					return false;
				} else {
					log.debug(queryOverSources.sources.size()+" sources are querying multiple endpoints, federation required");
					return true;
				}
			}
		}
	}
	
	
	
}
