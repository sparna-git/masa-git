package fr.humanum.masa.federation.admin;

import java.util.Set;

import fr.humanum.masa.federation.admin.AdminData.Message;
import fr.humanum.masa.federation.source.FederationSource;

public class AdminData {
	
	protected Set<FederationSource> federationSources;
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

	public Set<FederationSource> getFederationSources() {
		return federationSources;
	}

	public void setFederationSources(Set<FederationSource> federationSources) {
		this.federationSources = federationSources;
	}

	public Set<String> getIndexIds() {
		return indexIds;
	}

	public void setIndexIds(Set<String> indexIds) {
		this.indexIds = indexIds;
	}
	
}
