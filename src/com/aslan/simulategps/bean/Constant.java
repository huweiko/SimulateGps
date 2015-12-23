package com.aslan.simulategps.bean;

import android.content.Context;
import android.content.SharedPreferences;

public class Constant {

	public static final long MIN_LOGINTIME = 0;
	public static final String PgyerAPPID="80d0c2fdff696ab18b9c5c75cc410a00";// 集成蒲公英sdk应用的appId
	public static class Preference {

		public static final String SERVERIP = "SERVERIP";
		public static final String PORT = "PORT";

		public static SharedPreferences getSharedPreferences(Context context) {
			return context.getSharedPreferences("SimulateGpsSharePref", Context.MODE_PRIVATE);
		}
	}
}
