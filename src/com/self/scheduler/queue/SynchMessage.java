package com.self.scheduler.queue;

import java.util.HashMap;
import java.util.Map;

public class SynchMessage {
	
	/** The synch item id. */
	private String synchItemID;
	
	/** The properties. */
	private Map<String, Object> properties;

	/**
	 * @return the synchItemID
	 */
	public String getSynchItemID() {
		return synchItemID;
	}

	/**
	 * @param pSynchItemID the synchItemID to set
	 */
	public void setSynchItemID(String pSynchItemID) {
		synchItemID = pSynchItemID;
	}

	/**
	 * @return the properties
	 */
	public Object getProperties(String PropertyName) {
		if (properties != null
				&& !properties.isEmpty()) {
			return properties.get(PropertyName);
		}
		return null;
	}

	/**
	 * @param pProperties the properties to set
	 */
	public void setProperties(String PropertyName, Object pPropertyValue) {
		if (properties == null) {
			properties = new HashMap<String, Object>();
		}
		properties.put(PropertyName, pPropertyValue);
	}
}
