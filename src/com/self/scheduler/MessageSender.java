package com.self.scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.self.scheduler.queue.QueueFactory;
import com.self.scheduler.queue.SynchMessage;
import com.self.scheduler.queue.Utils;
import com.self.util.QueueRepositoryConstants;
import com.self.util.RQLUtils;

/**
 * The Class MessageSender.
 */
public class MessageSender extends Thread {
	
	private RQLUtils mItemQueueRQLUtils;
	
	private Map<String, String> mQueueItemDescNameMap;
	
	@Override
	public void run() {

		RepositoryItem[] items;
		try {
			items = getItemQueueRQLUtils().getItemByProperty(
					QueueRepositoryConstants.ID_SNAPSHOTITEMS, 
					QueueRepositoryConstants.IP_SYNCHED, Boolean.FALSE);
			if (items != null
					&& items.length > 0) {
				for (RepositoryItem repositoryItem : items) {
					List<RepositoryItem> itemLst = (List<RepositoryItem>) repositoryItem.getPropertyValue(
							QueueRepositoryConstants.IP_SYNCHITEMS);
					if (itemLst != null) {
						for (RepositoryItem repoItem : itemLst) {
							SynchMessage message = getMessage(repoItem);
							if (message != null) {
								String queueKey = message.getProperties(QueueRepositoryConstants.IP_ITEMDESCRIPTORNAME).toString()
										+ message.getProperties(QueueRepositoryConstants.IP_REPOSITORYNAME).toString();
								if (mQueueItemDescNameMap != null
										&& mQueueItemDescNameMap.get(queueKey) != null) {
									
									BlockingQueue<SynchMessage> queue = QueueFactory.getBlockingQueue(
											mQueueItemDescNameMap.get(queueKey));
									if (queue != null) {
										Utils.putIntoQueue(queue, message);
									}
									
								}
							}
						}
					}
				}
			}
			
		} catch (RepositoryException | InterruptedException e) {
			e.printStackTrace();
		}
	
	}

	private SynchMessage getMessage(RepositoryItem pRepoItem) {
		if (pRepoItem != null) {
			SynchMessage message = new SynchMessage();
			message.setSynchItemID(pRepoItem.getRepositoryId());
			message.setProperties(QueueRepositoryConstants.IP_ITEMID, 
					pRepoItem.getPropertyValue(QueueRepositoryConstants.IP_ITEMID));
			message.setProperties(QueueRepositoryConstants.IP_ASSETVERSION, 
					pRepoItem.getPropertyValue(QueueRepositoryConstants.IP_ASSETVERSION));
			message.setProperties(QueueRepositoryConstants.IP_ITEMDESCRIPTORNAME, 
					pRepoItem.getPropertyValue(QueueRepositoryConstants.IP_ITEMDESCRIPTORNAME));
			message.setProperties(QueueRepositoryConstants.IP_REPOSITORYNAME, 
					pRepoItem.getPropertyValue(QueueRepositoryConstants.IP_REPOSITORYNAME));
			message.setProperties(QueueRepositoryConstants.IP_ISSYNCHED, 
					pRepoItem.getPropertyValue(QueueRepositoryConstants.IP_ISSYNCHED));
			return message;
		}
		return null;
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
