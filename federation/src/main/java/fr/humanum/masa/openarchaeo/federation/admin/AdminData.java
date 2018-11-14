package fr.humanum.masa.openarchaeo.federation.admin;

import java.util.List;
import java.util.Set;

import fr.humanum.masa.openarchaeo.federation.source.FederationSource;

public class AdminData {
	
	protected List<FederationSource> federationSources;
	protected Set<String> indexIds;
	protected Message message;

	public class Message {
		protected String level;
		protected String text;
		
		public Message(String level, String text) {
			super();
			this.level = level;
			this.text = text;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
	
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public List<FederationSource> getFederationSources() {
		return federationSources;
	}

	public void setFederationSources(List<FederationSource> federationSources) {
		this.federationSources = federationSources;
	}

	public Set<String> getIndexIds() {
		return indexIds;
	}

	public void setIndexIds(Set<String> indexIds) {
		this.indexIds = indexIds;
	}
	
}
