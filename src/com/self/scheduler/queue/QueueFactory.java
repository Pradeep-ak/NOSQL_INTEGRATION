package com.self.scheduler.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueFactory {

	/** The blocking queue map. */
	private static Map<String, BlockingQueue<SynchMessage>>  mBlockingQueueMap;
	
	/**
	 * Instantiates a new queue factory.
	 */
	private QueueFactory() {
		
	}
	
	public static BlockingQueue<SynchMessage> getBlockingQueue(String pName) {
		if (mBlockingQueueMap == null) {
			mBlockingQueueMap = new HashMap<String, BlockingQueue<SynchMessage>>();
		}
		if (mBlockingQueueMap.get(pName) == null) {
			mBlockingQueueMap.put(pName, new LinkedBlockingQueue<SynchMessage>());
		}
		return mBlockingQueueMap.get(pName);
	}
}
