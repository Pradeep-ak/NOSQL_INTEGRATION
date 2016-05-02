package com.self.scheduler.queue;

import java.util.concurrent.BlockingQueue;

public class Utils {

	/**
	 * Put into queue.
	 *
	 * @param pQueue the queue
	 * @param pMessage the message
	 * @throws InterruptedException the interrupted exception
	 */
	public static void putIntoQueue(BlockingQueue<SynchMessage> pQueue, SynchMessage pMessage)
			throws InterruptedException {
		while (!pQueue.offer(pMessage)) {
			Thread.sleep(2000);
		}
	}

	/**
	 * Take from queue.
	 *
	 * @param pQueue the queue
	 * @return the synch message
	 * @throws InterruptedException the interrupted exception
	 */
	public static SynchMessage takeFromQueue(BlockingQueue<SynchMessage> pQueue) throws InterruptedException {
		SynchMessage obj = null;
		while (((obj = (SynchMessage) pQueue.poll()) == null)) {
			Thread.sleep(2000);
		}
		return obj;
	}
}
