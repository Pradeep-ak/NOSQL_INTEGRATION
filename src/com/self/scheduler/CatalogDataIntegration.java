package com.self.scheduler;

import java.util.Map;

import atg.repository.MutableRepository;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.self.util.RQLUtils;

public class CatalogDataIntegration extends SingletonSchedulableService {

	/** The active. */
	private boolean active = Boolean.FALSE;
	
	/** The item queue repository. */
	private MutableRepository mItemQueueRepository;
	
	/** The item queue rql utils. */
	private RQLUtils mItemQueueRQLUtils;
	
	private Map<String, String> mQueueItemDescNameMap;
	
	
	@Override
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledJob) {

			if (isActive()) {
					
					
			} else {
				if (isLoggingDebug()) {
					logDebug("doScheduledTask: " + "The scheduler is not active.");
				}
			}
		
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param pActive the active to set
	 */
	public void setActive(boolean pActive) {
		active = pActive;
	}

	/**
	 * @return the itemQueueRepository
	 */
	public MutableRepository getItemQueueRepository() {
		return mItemQueueRepository;
	}

	/**
	 * @param pItemQueueRepository the itemQueueRepository to set
	 */
	public void setItemQueueRepository(MutableRepository pItemQueueRepository) {
		mItemQueueRepository = pItemQueueRepository;
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
