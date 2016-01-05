package com.aslan.simulategps.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aslan.simulategps.activity.BluetoothChatActivity;
import com.aslan.simulategps.bean.GPGGA;
import com.aslan.simulategps.bean.GPRMC;
import com.aslan.simulategps.bean.GSV;
import com.aslan.simulategps.bean.LocationInfo;
import com.aslan.simulategps.utils.AssetUtils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class BlueDataRecvThread extends Thread {
	boolean isRunning = false;
	public static final LinkedBlockingQueue<String> queue = 
			new LinkedBlockingQueue<String>(60);
	private final Handler mHandler;
	private Context mContext;
	public BlueDataRecvThread(Context context,Handler handler) {
		mHandler = handler;
		mContext = context;
		
	}
	int c = 0;
	GPGGA mGPGGA = new GPGGA();
	GPRMC mGPRMC = new GPRMC();
	LocationInfo mLocationInfo = new LocationInfo();
	Map<String, GSV> ley = new HashMap<String, GSV>();
	
	@Override
	public void run() {		
		String list=null;
		while (isRunning) {
			try{
				list = queue.take();	
			       String regex = "\\$(.*?)\\*";
			       Pattern pattern = Pattern.compile(regex);
			       Matcher matcher = pattern.matcher(list);
			       Double la[] = {23.1043313,23.1012217025,23.0954377026,23.0988376459,23.0929196459,23.0840739837};
			       Double lo[] = {113.2577491,113.2589202303,113.2616152303,113.2689195845,113.2707525844,113.2743258159};
			       c++;
			       if(c>5){
			    	   c = 0;
			       }
			       while (matcher.find()) {
			          Log.i("huwei",matcher.group(1));
			          String command = matcher.group(1);
			          String []vlaues = command.split(",");
			          if(command.contains("GSV")){
			        	  
			        	  int type;
			        	  if(vlaues[0].equals("GPGSV")){
			        		  type = GSV.GP;
		        		  }
		        		  else if(vlaues[0].equals("BDGSV") || vlaues[0].equals("GBDGSV") || vlaues[0].equals("GBGSV")){
		        			  type = GSV.BD;
		        		  }
		        		  else if(vlaues[0].equals("GLGSV")){
		        			  type = GSV.GL;
		        		  }
		        		  else{
		        			  continue;
		        		  }
			        	  for(int i = 4; i< vlaues.length;i+=4){
			        		  if(i+3<vlaues.length){
			        			  if(vlaues[i].equals("")||vlaues[i+1].equals("")||vlaues[i+2].equals("")||vlaues[i+3].equals("")){
			        				  continue;
			        			  }
			        			  GSV lGSV = new GSV();
				        		  lGSV.setType(type);
				        		  lGSV.setBianhao(vlaues[i]);
				        		  lGSV.setYangjiao(vlaues[i+1]);
				        		  lGSV.setFangweijiao(vlaues[i+2]);
				        		  lGSV.setXinzaobi(vlaues[i+3]);
				        		  ley.put(lGSV.getBianhao(), lGSV);
			        		  }
			        	  }
			          }
			          else if(command.contains("GPRMC")){
			        	  mGPRMC.setUTCDay(vlaues[1]);
			        	  mGPRMC.setUTCYear(vlaues[9]);
			        	  mGPRMC.setLatitude(vlaues[3]);
			        	  mGPRMC.setLongitude(vlaues[5]);
			        	  mGPRMC.setGroundSpeed(vlaues[7]);
			        	  mGPRMC.setGroundCourse(vlaues[8]);
			        	  mGPRMC.setMagneticVariation(vlaues[10]);
			        	  
//			        	  mLocationInfo.setLatitude(Double.parseDouble(mGPRMC.getLatitude()));
//			        	  mLocationInfo.setLongitude(Double.parseDouble(mGPRMC.getLongitude()));
			        	  mLocationInfo.setUTC(mGPRMC.getUTCYear()+mGPRMC.getUTCDay());
			          }
			          else if(command.contains("GPGGA")){
			        	  mGPGGA.setUTCDay(vlaues[1]);
			        	  mGPGGA.setLatitude(vlaues[2]);
			        	  mGPGGA.setLongitude(vlaues[4]);
			        	  mGPGGA.setLevelAccuracy(vlaues[8]);
			        	  mGPGGA.setHeight(vlaues[9]);
			        	  
//			        	  mLocationInfo.setLatitude(la[c]);
//			        	  mLocationInfo.setLongitude(lo[c]);
			        	  mLocationInfo.setLatitude(AssetUtils.GGAToDouble(mGPGGA.getLatitude()));
			        	  mLocationInfo.setLongitude(AssetUtils.GGAToDouble(mGPGGA.getLongitude()));
			        	  mLocationInfo.setHeight(mGPGGA.getHeight());
			        	  mLocationInfo.setLevelAccuracy(mGPGGA.getLevelAccuracy());
			          }
			          
			       }
			       c++;
			       mHandler.obtainMessage(BluetoothChatActivity.MESSAGE_SATELLITE, -1, -1, ley)
			       .sendToTarget();
			       mHandler.obtainMessage(BluetoothChatActivity.MESSAGE_LOCATION, -1, -1, mLocationInfo)
			       .sendToTarget();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void cancel() {
		isRunning = false;
		repaintSatellites(new String());
		try {
			join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
