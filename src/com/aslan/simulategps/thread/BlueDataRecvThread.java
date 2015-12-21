package com.aslan.simulategps.thread;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class BlueDataRecvThread extends Thread {
	boolean isRunning = false;
	public static final LinkedBlockingQueue<String> queue = 
			new LinkedBlockingQueue<String>(60);
	public BlueDataRecvThread(Context context) {
	}
	
	@Override
	public void run() {		
		String list=null;
		while (isRunning) {
			try{
				list = queue.take();	
			       String regex = "\\$(.*?)\\*";
			       Pattern pattern = Pattern.compile(regex);
			       Matcher matcher = pattern.matcher(list);
			       while (matcher.find()) {
			          Log.i("huwei",matcher.group(1));
			       }
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public void setRunning(boolean b) {
		isRunning = b;
	}
	public void repaintSatellites(String list) {
		try {
			queue.offer(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
