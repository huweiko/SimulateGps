package com.aslan.simulategps.thread;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;

public class CheckThread extends Thread {
	Context mContext;
	boolean isRunning = false;
	Handler mHandler;
	public CheckThread(Context context,Handler handler) {
		mContext = context;
		mHandler = handler;
	}
	
	@Override
	public void run() {	
		int i = 240;
		while (i-- > 0) {
			try {
				sleep(5000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mHandler.sendEmptyMessage(100001);
		ActivityManager activityMgr= (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		activityMgr.restartPackage(mContext.getPackageName());
		System.exit(0);
	}


	public void setRunning(boolean b) {
		isRunning = b;
	}
}
