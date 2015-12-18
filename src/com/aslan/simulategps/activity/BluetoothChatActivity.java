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

import java.util.ArrayList;
import java.util.List;

import com.aslan.simulategps.R;
import com.aslan.simulategps.BluetoothChat.BluetoothChatService;
import com.aslan.simulategps.activity.DeviceListActivity;
import com.aslan.simulategps.base.BaseActivity;
import com.aslan.simulategps.base.MyYAxisValueFormatter;
import com.aslan.simulategps.gps.SatellitesView;
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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChatActivity extends Activity {
	// Debugging
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int CALL_OUT = 6;
	public static final int Hang_UP = 7;

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


	// Name of the connected device
	private String mConnectedDeviceName = null;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;

	
	private int minTime = 1000;
	private int minDistance = 0;

	private LocationManager locationManager;
	private SatellitesView satellitesView;
	private TextView lonlatText;
	private TextView gpsStatusText;

	protected BarChart mChart;

    private Typeface mTf;
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		setContentView(R.layout.main);
		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);
		gpsStatusText = (TextView) findViewById(R.id.gps_status_text);
		lonlatText = (TextView) findViewById(R.id.lonlat_text);
		satellitesView = (SatellitesView) findViewById(R.id.satellitesView);
		registerListener();
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

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
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
		unregisterListener();
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
					break;
				case BluetoothChatService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				break;
			case MESSAGE_READ:
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
			case CALL_OUT:
				Intent intent = new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:10086"));
				BluetoothChatActivity.this.startActivity(intent);
				// Toast.makeText(BluetoothChat.this,"calling...",Toast.LENGTH_LONG).show();
				// BluetoothChat.this.call();
				// Intent intent = new
				// Intent(BluetoothChat.this,CallService.class);
				// startService(intent);
				// telephonyService.endCall();
				// closePhone();
				break;
			case Hang_UP:
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
		case R.id.background_run:
			
			return true;
		case R.id.closeApp:
			// Ensure this device is discoverable by others
			finish();
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

	        setData(12, 50);
	}
	private void setData(int count, float range) {

       ArrayList<String> xVals = new ArrayList<String>();
       for (int i = 0; i < count; i++) {
           xVals.add(mMonths[i % 12]);
       }

       ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

       for (int i = 0; i < count; i++) {
           float mult = (range + 1);
           float val = (float) (Math.random() * mult);
           yVals1.add(new BarEntry(val, i));
       }

       BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
       set1.setBarSpacePercent(35f);

       ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
       dataSets.add(set1);

       BarData data = new BarData(xVals, dataSets);
       data.setValueTextSize(10f);
       data.setValueTypeface(mTf);

       mChart.setData(data);
   }
   /**
    * 注册监听
    */
	private void registerListener() {
		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		//侦听位置信息(经纬度变化)
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, locationListener);
		// 侦听GPS状态，主要是捕获到的各个卫星的状态
		locationManager.addGpsStatusListener(gpsStatusListener);
		//TODO:考虑增加监听传感器中的方位数据，以使罗盘的北能自动指向真实的北向
	}
   /**
    * 移除监听
    */
	private void unregisterListener() {
		if (locationManager != null) {
			locationManager.removeGpsStatusListener(gpsStatusListener);
			locationManager.removeUpdates(locationListener);
		}
	}
   /**
    * 坐标位置监听
    */
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			StringBuffer sb = new StringBuffer();
			int fmt = Location.FORMAT_DEGREES;
			sb.append(Location.convert(location.getLongitude(), fmt));
			sb.append(" ");
			sb.append(Location.convert(location.getLatitude(), fmt));
			lonlatText.setText(sb.toString());

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			gpsStatusText.setText("onStatusChanged");

		}

		@Override
		public void onProviderEnabled(String provider) {
			gpsStatusText.setText("onProviderEnabled");

		}

		@Override
		public void onProviderDisabled(String provider) {
			gpsStatusText.setText("onProviderDisabled");

		}

	};
	
   /**
    * Gps状态监听
    */
	private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			switch (event) {
			case GpsStatus.GPS_EVENT_FIRST_FIX: {
				gpsStatusText.setText("GPS_EVENT_FIRST_FIX");
				// 第一次定位时间UTC gps可用
				// Log.v(TAG,"GPS is usable");
				int i = gpsStatus.getTimeToFirstFix();
				break;
			}

			case GpsStatus.GPS_EVENT_SATELLITE_STATUS: {// 周期的报告卫星状态
				// 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
				Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();

				List<GpsSatellite> satelliteList = new ArrayList<GpsSatellite>();

				for (GpsSatellite satellite : satellites) {
					// 包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
					/*
					 * satellite.getElevation(); //卫星仰角
					 * satellite.getAzimuth();   //卫星方位角 
					 * satellite.getSnr();       //信噪比
					 * satellite.getPrn();       //伪随机数，可以认为他就是卫星的编号
					 * satellite.hasAlmanac();   //卫星历书 
					 * satellite.hasEphemeris();
					 * satellite.usedInFix();
					 */
					satelliteList.add(satellite);
				}

				satellitesView.repaintSatellites(satelliteList);
				gpsStatusText.setText("GPS_EVENT_SATELLITE_STATUS:"
						+ satelliteList.size());
				break;
			}

			case GpsStatus.GPS_EVENT_STARTED: {
				gpsStatusText.setText("GPS_EVENT_STARTED");
				break;
			}

			case GpsStatus.GPS_EVENT_STOPPED: {
				gpsStatusText.setText("GPS_EVENT_STOPPED");
				break;
			}

			default:
				gpsStatusText.setText("GPS_EVENT:" + event);
				break;
			}
		}
	};


	
}