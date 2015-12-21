package com.aslan.simulategps.thread;

import android.content.Context;

public class NetDataRecvThread extends Thread {
	boolean isRunning = false;
	public NetDataRecvThread(Context context) {
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
