package fr.humanum.openarchaeo.federation.period;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

public class PeriodoPeriodService implements PeriodServiceIfc {

	private static final IRI START = SimpleValueFactory.getInstance().createIRI("http://www.w3.org/2006/time#intervalStartedBy");
	private static final IRI STOP = SimpleValueFactory.getInstance().createIRI("http://www.w3.org/2006/time#intervalFinishedBy");
	private static final IRI IN = SimpleValueFactory.getInstance().createIRI("http://www.w3.org/2006/time#hasDateTimeDescription");
	private static final IRI YEAR = SimpleValueFactory.getInstance().createIRI("http://www.w3.org/2006/time#year");
	
	private File periodoFile;
	private Model model;

	public PeriodoPeriodService(File periodoFile) {
		super();
		this.periodoFile = periodoFile;
		this.initModel();
	}
	
	private void initModel() {
		try {
			try(InputStream in = new FileInputStream(this.periodoFile)) {
				this.model = Rio.parse(in, RDF.NAMESPACE, RDFFormat.JSONLD);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.model = null;
		}
	}

	@Override
	public List<PeriodJson> getPeriods(String lang) {		
		// for each periods...
		List<PeriodJson> results = this.model.filter(null, RDF.TYPE, SKOS.CONCEPT).subjects().stream()
		.filter(r -> {
				return true;
		}).map(r -> {
			PeriodJson p = new PeriodJson(r.stringValue());
			
			Resource start = (Resource)this.model.filter(r, START, null).objects().stream().findFirst().get();
			Resource startIn = (Resource)this.model.filter(start, IN, null).objects().stream().findFirst().get();
			Literal startYear = (Literal)this.model.filter(startIn, YEAR, null).objects().stream().findFirst().get();
			p.setStart(new PeriodJson.Instant(startYear.stringValue()));
			
			Resource stop = (Resource)this.model.filter(r, STOP, null).objects().stream().findFirst().get();
			Resource stopIn = (Resource)this.model.filter(stop, IN, null).objects().stream().findFirst().get();
			Literal stopYear = (Literal)this.model.filter(stopIn, YEAR, null).objects().stream().findFirst().get();
			p.setStop(new PeriodJson.Instant(stopYear.stringValue()));
			
			p.setSynonyms(Collections.emptySet());
			
			p.setLabel(((Literal)this.model.filter(r, SKOS.PREF_LABEL, null).objects().stream().findFirst().get()).stringValue());
			
			return p;
		}).collect(Collectors.toList());
		
		return results;
	}
	
}
