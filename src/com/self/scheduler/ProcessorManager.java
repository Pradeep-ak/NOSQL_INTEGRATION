package com.self.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.self.NoSQLException;
import com.self.scheduler.queue.QueueFactory;
import com.self.scheduler.queue.RecordData;
import com.self.scheduler.queue.SynchMessage;
import com.self.scheduler.queue.Utils;
import com.self.util.QueueRepositoryConstants;

public class ProcessorManager extends Thread {
	
	private static final String MESSAGE_PROCESSOR = "MESSAGE_PROCESSOR";
	
	private Map<String, String> mClassItemDescNameMap;
	
	private Map<String, Repository> mRepoNametoRepoMap;
	
	private ProcessorHelper mHelper;
	
	@Override
	public void run() {
		try {
			SynchMessage message = getMessage();
			List<RecordData> recordDatas = getRecordsForMessage(message);
			RepositoryItem item = loadRepositoryItem(message);
			if (recordDatas != null
					&& !recordDatas.isEmpty()) {
				for (RecordData recordData : recordDatas) {
					mHelper.loadRecordData(item, recordData);
				}
			}
		} catch (NoSQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Load repository item.
	 *
	 * @param pMessage the message
	 * @return the repository item
	 * @throws NoSQLException 
	 * @throws RepositoryException 
	 */
	private RepositoryItem loadRepositoryItem(SynchMessage pMessage) throws NoSQLException {
		RepositoryItem item = null;
		if (pMessage != null) {
			Object repoName = pMessage.getProperties(
					QueueRepositoryConstants.IP_REPOSITORYNAME);
			Object itemDescName = pMessage.getProperties(
					QueueRepositoryConstants.IP_ITEMDESCRIPTORNAME);
			Object itemId = pMessage.getProperties(
					QueueRepositoryConstants.IP_ITEMID);
			try {
				if (repoName != null
						&& itemDescName != null
						&& itemId != null) {
					
					Repository repository = mRepoNametoRepoMap.get(repoName.toString());
					RepositoryItem repositoryItem = repository.getItem(itemId.toString(), itemDescName.toString());
					return repositoryItem;
				}
			} catch (RepositoryException e) {
				throw new NoSQLException(e);
			}
		}
		return item;
	}

	/**
	 * Gets the records for message.
	 *
	 * @param pMessage the message
	 * @return the records for message
	 */
	private List<RecordData> getRecordsForMessage(SynchMessage pMessage) {
		List<RecordData> recordDatas = new ArrayList<>();
		if (pMessage != null) {
			String itemDescName = (String) pMessage.getProperties(
					QueueRepositoryConstants.IP_ITEMDESCRIPTORNAME);
			String classNameForItemDesc = mClassItemDescNameMap.get(itemDescName);
			List<String> classNameLst = getRecordClass(classNameForItemDesc);
			if (classNameLst != null
					&& !classNameLst.isEmpty()) {
				for (String className : classNameLst) {
					try {
						RecordData data = (RecordData)Class.forName(className).newInstance();
						recordDatas.add(data); 
					} catch (ClassNotFoundException 
							| InstantiationException 
							| IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		return recordDatas;
	}

	private List<String> getRecordClass(String pClassNameForItemDesc) {
		List<String> classNameStr = new ArrayList<>();
		if (StringUtils.isNotBlank(pClassNameForItemDesc)) {
			classNameStr = Arrays.asList(pClassNameForItemDesc.split(","));
		}
		return classNameStr;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	private SynchMessage getMessage() {
		BlockingQueue<SynchMessage> queue = QueueFactory.getBlockingQueue(
				MESSAGE_PROCESSOR);
		if (queue != null) {
			try {
				return Utils.takeFromQueue(queue);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public ProcessorManager(Map<String, String> pClassItemDescNameMap, 
			Map<String, Repository> pRepoNametoRepoMap) {
		super();
		mClassItemDescNameMap = pClassItemDescNameMap;
		mRepoNametoRepoMap = pRepoNametoRepoMap;
	}
	
}
