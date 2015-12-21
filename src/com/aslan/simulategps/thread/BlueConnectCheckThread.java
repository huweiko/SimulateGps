package com.aslan.simulategps.thread;

import android.content.Context;

public class BlueConnectCheckThread extends Thread {
	boolean isRunning = false;
	public BlueConnectCheckThread(Context context) {
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
