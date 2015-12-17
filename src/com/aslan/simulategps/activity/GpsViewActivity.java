package com.aslan.simulategps.activity;

import java.util.ArrayList;
import java.util.List;

import com.aslan.simulategps.R;
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
import android.content.Context;
import android.graphics.Typeface;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class GpsViewActivity extends Activity {
	private int minTime = 1000;
	private int minDistance = 0;
	private static final String TAG = "GpsView";

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_view_activity);

		gpsStatusText = (TextView) findViewById(R.id.gps_status_text);
		lonlatText = (TextView) findViewById(R.id.lonlat_text);
		satellitesView = (SatellitesView) findViewById(R.id.satellitesView);
		
		registerListener();
		initBarChart();
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

	@Override
	protected void onResume() {
		super.onResume();
		registerListener();
	}

	@Override
	protected void onDestroy() {
		unregisterListener();
		super.onDestroy();
	}

}
