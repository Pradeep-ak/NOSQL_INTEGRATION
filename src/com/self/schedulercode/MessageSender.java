package com.self.schedulercode;

import java.util.List;
import java.util.Map;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.self.util.MongoQueueRepositoryConstants;
import com.self.util.RQLUtils;

public class MessageSender extends Thread {
	
	private RQLUtils mItemQueueRQLUtils;
	
	private Map<String, String> mQueueItemDescNameMap;
	
	@Override
	public void run() {

		RepositoryItem[] items;
		try {
			items = getItemQueueRQLUtils().getItemByProperty(
					MongoQueueRepositoryConstants.ID_SNAPSHOTITEMS, 
					MongoQueueRepositoryConstants.IP_SYNCHED, Boolean.FALSE);
			if (items != null
					&& items.length > 0) {
				for (RepositoryItem repositoryItem : items) {
					List<RepositoryItem> itemLst = (List<RepositoryItem>) repositoryItem.getPropertyValue(
							MongoQueueRepositoryConstants.IP_SYNCHITEMS);
					if (itemLst != null) {
						for (RepositoryItem repoItem : itemLst) {
							
						}
					}
				}
			}
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	/**
	 * @return the itemQueueRQLUtils
	 */
	public RQLUtils getItemQueueRQLUtils() {
		return mItemQueueRQLUtils;
	}

	/**
	 * @param pItemQueueRQLUtils the itemQueueRQLUtils to set
	 */
	public void setItemQueueRQLUtils(RQLUtils pItemQueueRQLUtils) {
		mItemQueueRQLUtils = pItemQueueRQLUtils;
	}

	/**
	 * @return the queueItemDescNameMap
	 */
	public Map<String, String> getQueueItemDescNameMap() {
		return mQueueItemDescNameMap;
	}

	/**
	 * @param pQueueItemDescNameMap the queueItemDescNameMap to set
	 */
	public void setQueueItemDescNameMap(Map<String, String> pQueueItemDescNameMap) {
		mQueueItemDescNameMap = pQueueItemDescNameMap;
	}
		
}
