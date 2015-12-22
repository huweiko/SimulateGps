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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class BlueDataRecvThread extends Thread implements LocationListener{
	boolean isRunning = false;
	public static final LinkedBlockingQueue<String> queue = 
			new LinkedBlockingQueue<String>(60);
	private final Handler mHandler;
	private Context mContext;
	public BlueDataRecvThread(Context context,Handler handler) {
		mHandler = handler;
		mContext = context;
		initLocation();
	    thread.start(); 
	}
	Thread thread = new Thread(new Runnable() {
 	   public void run(){
 		   while(true){
 			   try {
 				   setLocation(mLocationInfo.getLongitude(), mLocationInfo.getLatitude());
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
 		   }
 	   }
    });
	GPGGA mGPGGA = new GPGGA();
	GPRMC mGPRMC = new GPRMC();
	LocationInfo mLocationInfo = new LocationInfo();
	Map<String, GSV> ley = new HashMap<String, GSV>();
	private String mMockProviderName = LocationManager.GPS_PROVIDER;
	private LocationManager locationManager;
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
			        	  
			        	  mLocationInfo.setLatitude(Double.parseDouble(mGPRMC.getLatitude()));
			        	  mLocationInfo.setLongitude(Double.parseDouble(mGPRMC.getLongitude()));
			        	  mLocationInfo.setUTC(mGPRMC.getUTCYear()+mGPRMC.getUTCDay());
			          }
			          else if(command.contains("GPGGA")){
			        	  mGPGGA.setUTCDay(vlaues[1]);
			        	  mGPGGA.setLatitude(vlaues[2]);
			        	  mGPGGA.setLongitude(vlaues[4]);
			        	  mGPGGA.setLevelAccuracy(vlaues[8]);
			        	  mGPGGA.setHeight(vlaues[9]);
			        	  
			        	  mLocationInfo.setLatitude(Double.parseDouble(mGPGGA.getLatitude()));
			        	  mLocationInfo.setLongitude(Double.parseDouble(mGPGGA.getLongitude()));
			        	  mLocationInfo.setLongitude(Double.parseDouble(mGPGGA.getHeight()));
			        	  mLocationInfo.setLongitude(Double.parseDouble(mGPGGA.getLevelAccuracy()));
			          }
			          
			       }
			       
			       
			       mHandler.obtainMessage(BluetoothChatActivity.MESSAGE_SATELLITE, -1, -1, ley)
			       .sendToTarget();
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
	/**
	 * inilocation 初始化 位置模拟
	 * 
	 */
	private void initLocation() {
		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addTestProvider(mMockProviderName, false, true, false,
				false, true, true, true, 0, 5);
		locationManager.setTestProviderEnabled(mMockProviderName, true);
		locationManager.requestLocationUpdates(mMockProviderName, 0, 0, this);
	}

	/**
	 * setLocation 设置GPS的位置
	 * 
	 */
	private void setLocation(double longitude, double latitude) {
		Location location = new Location(mMockProviderName);
		location.setTime(System.currentTimeMillis());
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		location.setAltitude(Float.parseFloat(mLocationInfo.getHeight()));
		location.setAccuracy(Float.parseFloat(mLocationInfo.getLevelAccuracy()));
		location.setBearing(180.00f);
		location.setSpeed(200f);
		locationManager.setTestProviderLocation(mMockProviderName, location);
	}


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
