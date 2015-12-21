package com.aslan.simulategps.thread;

import android.content.Context;

public class BlueDataRecvThread extends Thread {
	boolean isRunning = false;
	public BlueDataRecvThread(Context context) {
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
