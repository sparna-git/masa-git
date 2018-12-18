package fr.humanum.openarchaeo.federation.period;

import java.util.Set;

public class PeriodJson {

	private String uri;
	private String label;
	private Set<String> synonyms;
	private Instant start;
	private Instant stop;
	
	public static class Instant {
		private String year;

		public Instant() {
			super();
		}

		public Instant(String year) {
			super();
			this.year = year;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}
				
	}

	public PeriodJson(String uri) {
		super();
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Set<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Set<String> synonyms) {
		this.synonyms = synonyms;
	}

	public Instant getStart() {
		return start;
	}

	public void setStart(Instant start) {
		this.start = start;
	}

	public Instant getStop() {
		return stop;
	}

	public void setStop(Instant stop) {
		this.stop = stop;
	}

}
