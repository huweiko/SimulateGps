package com.aslan.simulategps.thread;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class BlueConnectCheckThread extends Thread {
	boolean isRunning = false;
	public BlueConnectCheckThread(Context context,BluetoothSocket socket) {
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
