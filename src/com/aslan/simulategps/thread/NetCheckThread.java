package com.aslan.simulategps.thread;

import com.aslan.simulategps.utils.AssetUtils;

import android.content.Context;
import android.util.Log;

public class NetCheckThread extends Thread {
	String Tag = getClass().getSimpleName();
	boolean isRunning = true;
	Context mContext;
	String mServerIp;
	public NetCheckThread(Context context,String ServerIp) {
		mContext = context;
		mServerIp = ServerIp;
	}
	
	@Override
	public void run() {		
		while (isRunning) {
			if(AssetUtils.checkNetworkByIP(mServerIp)){
				Log.i(Tag, "有网");
			}else{
				Log.i(Tag, "无网");
			}
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void setRunning(boolean b) {
		isRunning = b;
	}
}
