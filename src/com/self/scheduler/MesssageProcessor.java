package com.self.scheduler;

import java.util.Map;

public class MesssageProcessor extends Thread {
	
	private Map<String, String> mClassItemDescNameMap;
	
	@Override
	public void run() {
		
	}
	
	public MesssageProcessor(Map<String, String> pClassItemDescNameMap) {
		super();
		mClassItemDescNameMap = pClassItemDescNameMap;
	}
	
}
