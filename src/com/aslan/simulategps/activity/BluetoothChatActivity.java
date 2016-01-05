/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*����������������*/
package com.aslan.simulategps.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.aslan.simulategps.R;
import com.aslan.simulategps.BluetoothChat.BluetoothChatService;
import com.aslan.simulategps.activity.DeviceListActivity;
import com.aslan.simulategps.base.BaseActivity;
import com.aslan.simulategps.base.MyYAxisValueFormatter;
import com.aslan.simulategps.bean.GSV;
import com.aslan.simulategps.bean.LocationInfo;
import com.aslan.simulategps.gps.SatellitesView;
import com.aslan.simulategps.thread.CheckThread;
import com.aslan.simulategps.thread.NetDataRecvThread;
import com.aslan.simulategps.utils.AssetUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChatActivity extends BaseActivity implements LocationListener{
	Context mContext;
	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_SATELLITE = 6;//卫星更新
	public static final int MESSAGE_BARCAHT = 7;//信噪比柱状图
	public static final int MESSAGE_LOCATION = 8;//定位信息
	public static final int MESSAGE_INIT_LOCATION = 9;//初始化定位信息
	
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	public static final byte[] CALL_CMD = { 0x1a }; // lower than 128
	public static final byte[] ENDCALL_CMD = { 0x2a };
	public static final String SEND_CALL = "#";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// Layout Views
	private TextView mTitle;
	private Button mButtonSendData;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	public static BluetoothChatService mChatService = null;

	private LocationManager locationManager;
	private String mMockProviderName = LocationManager.GPS_PROVIDER;
	private LocationInfo mLocationInfo;
	private SatellitesView satellitesView;

	protected BarChart mChart;

    private Typeface mTf;
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
	
    public static NetDataRecvThread mNetDataRecvThread;
    TextView mTextViewLo,mTextViewLa;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		setContentView(R.layout.main);
		mContext = getApplicationContext();
		CheckThread mCheckThread = new CheckThread(getApplicationContext(), mHandler);
		mCheckThread.setRunning(true);
		mCheckThread.start();
		thread.start();
		mNetDataRecvThread = new NetDataRecvThread(mContext);
		mNetDataRecvThread.start();
/*		
		String str = AssetUtils.getDataFromAssets(getApplicationContext(), "question.txt");
		BlueDataRecvThread mBlueDataRecvThread = new BlueDataRecvThread(getApplicationContext(),mHandler);
		mBlueDataRecvThread.setRunning(true);
		mBlueDataRecvThread.start();
		mBlueDataRecvThread.repaintSatellites(str);
		mBlueDataRecvThread.repaintSatellites(new String());
		mBlueDataRecvThread.setRunning(false);
		*/
		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);
		mTextViewLa = (TextView) findViewById(R.id.TextViewLa);
		mTextViewLo = (TextView) findViewById(R.id.TextViewLo);
		
		satellitesView = (SatellitesView) findViewById(R.id.satellitesView);
		mButtonSendData = (Button) findViewById(R.id.ButtonSendData);
		mButtonSendData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String str = AssetUtils.getDataFromAssets(getApplicationContext(), "question.txt");
				try {
					mChatService.write(str.getBytes("GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
//		registerListener();
		initBarChart();
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");
		mChatService = new BluetoothChatService(this, mHandler);
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		unregisterListener();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}
	//设置被连接状态
	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}



	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
					case BluetoothChatService.STATE_CONNECTED:
						mTitle.setText(R.string.title_connected);
						break;
					case BluetoothChatService.STATE_CONNECTING:
						mTitle.setText(R.string.title_connecting);
						break;
					case BluetoothChatService.STATE_LISTEN:
						break;
					case BluetoothChatService.STATE_NONE:
						mTitle.setText(R.string.title_not_connected);
						break;
				}
				break;
			case MESSAGE_WRITE:
				break;
			case MESSAGE_READ:
				String str = (String)msg.obj;
//				Toast.makeText(BluetoothChatActivity.this, "蓝牙接收：" + str, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			case MESSAGE_SATELLITE:
				List<GSV> satelliteList = new ArrayList<GSV>();
				Map<String, GSV> ley = (Map<String, GSV>) msg.obj;
				Iterator iter = ley.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String)entry.getKey();
					GSV val = (GSV)entry.getValue();
					satelliteList.add(val);
//					iter.remove();
				}
				satellitesView.repaintSatellites(satelliteList);
				setData(satelliteList);
				break;
			case MESSAGE_LOCATION:
				mLocationInfo = (LocationInfo) msg.obj;
				mTextViewLa.setText("纬度："+mLocationInfo.getLatitude());
				mTextViewLo.setText("经度："+mLocationInfo.getLongitude());
				break;
			case MESSAGE_INIT_LOCATION:
				initLocation();
				break;
			case 100001:
				Toast.makeText(getApplicationContext(),
						"测试版试用时间到，请联系相关人员", Toast.LENGTH_SHORT)
						.show();
				break;
			case 100002:
				Toast.makeText(getApplicationContext(),
						"位置发生变化，正在模拟定位", Toast.LENGTH_SHORT)
						.show();
				break;
			case 100003:
				Toast.makeText(getApplicationContext(),
						"您的模拟位置没有打开，请设置允许模拟位置", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			
			
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				// Attempt to connect to the device
				mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.connectServer:
			// Ensure this device is discoverable by others
			Intent tcpIntent = new Intent();
			tcpIntent.setClass(BluetoothChatActivity.this, TcpClientActivity.class);
			startActivity(tcpIntent);
			return true;
		}
		return false;
	}
	void initBarChart(){
		 mChart = (BarChart) findViewById(R.id.chart1);

	        mChart.setDrawBarShadow(false);
	        mChart.setDrawValueAboveBar(true);

	        mChart.setDescription("");

	        // if more than 60 entries are displayed in the chart, no values will be
	        // drawn
	        mChart.setMaxVisibleValueCount(60);

	        // scaling can now only be done on x- and y-axis separately
	        mChart.setPinchZoom(false);

	        mChart.setDrawGridBackground(false);
	        // mChart.setDrawYLabels(false);

	        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

	        XAxis xAxis = mChart.getXAxis();
	        xAxis.setPosition(XAxisPosition.BOTTOM);
	        xAxis.setTypeface(mTf);
	        xAxis.setDrawGridLines(false);
	        xAxis.setSpaceBetweenLabels(2);

	        YAxisValueFormatter custom = new MyYAxisValueFormatter();

	        YAxis leftAxis = mChart.getAxisLeft();
	        leftAxis.setTypeface(mTf);
	        leftAxis.setLabelCount(8, false);
	        leftAxis.setValueFormatter(custom);
	        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
	        leftAxis.setSpaceTop(15f);

	        YAxis rightAxis = mChart.getAxisRight();
	        rightAxis.setDrawGridLines(false);
	        rightAxis.setTypeface(mTf);
	        rightAxis.setLabelCount(8, false);
	        rightAxis.setValueFormatter(custom);
	        rightAxis.setSpaceTop(15f);

	        Legend l = mChart.getLegend();
	        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
	        l.setForm(LegendForm.SQUARE);
	        l.setFormSize(9f);
	        l.setTextSize(11f);
	        l.setXEntrySpace(4f);
	        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
	        // "def", "ghj", "ikl", "mno" });
	        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
	        // "def", "ghj", "ikl", "mno" });

	        setData(new ArrayList<GSV>());
	}
	private void setData(List<GSV> list) {
		mChart.clear();
       ArrayList<String> xVals = new ArrayList<String>();
       for (int i = 0; i < list.size(); i++) {
    	   if(!list.get(i).getXinzaobi().equals(""))
           xVals.add("#"+list.get(i).getBianhao());
       }

       ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

       for (int i = 0; i < list.size(); i++) {
    	   if(!list.get(i).getXinzaobi().equals(""))
           yVals1.add(new BarEntry(Integer.parseInt(list.get(i).getXinzaobi()), i));
       }

       BarDataSet set1 = new BarDataSet(yVals1, "信噪比");
       set1.setBarSpacePercent(35f);

       ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
       dataSets.add(set1);

       BarData data = new BarData(xVals, dataSets);
       data.setValueTextSize(10f);
       data.setValueTypeface(mTf);

       mChart.setData(data);
   }
	class ChartThread extends Thread{
		BarChart l_chart;
		boolean isRunning = true;
		public ChartThread(BarChart chart) {
			l_chart = chart;
		}
		
		public void run(){
			while(isRunning){
				
			}
		}
		void cancel(){
			isRunning = false;
			try {
				join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * inilocation 初始化 位置模拟
	 * 
	 */
	private void initLocation() {
		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addTestProvider(mMockProviderName, false, true, false,
				false, true, true, true, 0, 5);
		locationManager.setTestProviderEnabled(mMockProviderName, true);
		locationManager.requestLocationUpdates(mMockProviderName, 0, 0, BluetoothChatActivity.this);
	}

	/**
	 * setLocation 设置GPS的位置
	 * 
	 */
	private void setLocation(LocationInfo locationInfo) {
		Location location = new Location(mMockProviderName);
		location.setTime(System.currentTimeMillis());
		location.setLatitude(locationInfo.getLatitude());
		location.setLongitude(locationInfo.getLongitude());
		location.setAltitude(Float.parseFloat(locationInfo.getHeight()));
		location.setAccuracy(Float.parseFloat(locationInfo.getLevelAccuracy()));
		locationManager.setTestProviderLocation(mMockProviderName, location);
	}
	//虚拟定位线程
	Thread thread = new Thread(new Runnable() {
	 	   public void run(){
	 		   while(true){
	 			   try {
	 				   if(Settings.Secure.getInt(getContentResolver(),Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0){
	 					   if(locationManager == null){
	 						  mHandler.sendEmptyMessage(MESSAGE_INIT_LOCATION);
	 					   }else{
	 						   if(mLocationInfo != null){
	 							  setLocation(mLocationInfo);
		 						  Log.i("当前经纬度", mLocationInfo.getLatitude()+","+mLocationInfo.getLongitude());  
	 						   }
		 						  
	 					   }
				       }else{
				    	   mHandler.sendEmptyMessage(100003);
				    	   Thread.sleep(10000);
				       }
	 				   Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	 		   }
	 	   }
	});

	@Override
	protected Object getContentViewId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void IniView() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void IniLister() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void IniData() {
		// TODO Auto-generated method stub
		
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

	@Override
	protected void thisFinish() {
		// TODO Auto-generated method stub
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		build.setTitle("提示");
		build.setMessage("退出后，将不再提供服务，继续退出吗？");
		build.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ActivityManager activityMgr= (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
				activityMgr.restartPackage(mContext.getPackageName());
				System.exit(0);
			}
		});
		build.setNeutralButton("最小化", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				moveTaskToBack(true);
			}
		});
		build.setNegativeButton("取消", null);
		build.show();
	}
	
}