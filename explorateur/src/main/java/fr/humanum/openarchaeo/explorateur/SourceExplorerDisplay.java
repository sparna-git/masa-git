package fr.humanum.openarchaeo.explorateur;

public class SourceExplorerDisplay {

	protected String sourceString;
	protected String name;
	
	public SourceExplorerDisplay(String sourceString, String name) {
		super();
		this.sourceString = sourceString;
		this.name = name;
	}
	
	public String getSourceString() {
		return sourceString;
	}
	public void setSourceString(String sourceString) {
		this.sourceString = sourceString;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
