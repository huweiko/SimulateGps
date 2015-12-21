package com.aslan.simulategps.thread;

import android.content.Context;

public class NetCheckThread extends Thread {
	boolean isRunning = false;
	public NetCheckThread(Context context) {
	}
	
	@Override
	public void run() {		
		while (isRunning) {
			
		}
	}


	public void setRunning(boolean b) {
		isRunning = b;
	}
}
