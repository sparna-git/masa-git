package fr.humanum.openarchaeo.explorateur;

import java.util.List;
import java.util.stream.Collectors;

public class ExplorerDisplayData {

	protected List<SourceExplorerDisplay> sources;
	
	protected boolean requiresFederation = false;
	
	public List<SourceExplorerDisplay> getSources() {
		return sources;
	}
	public void setSources(List<SourceExplorerDisplay> sources) {
		this.sources = sources;
	}

	public String getSourcesDisplayString() {
		return sources.stream().map(s -> s.getName()).collect(Collectors.joining(", "));
	}
	public boolean isRequiresFederation() {
		return requiresFederation;
	}
	public void setRequiresFederation(boolean requiresFederation) {
		this.requiresFederation = requiresFederation;
	}
}
