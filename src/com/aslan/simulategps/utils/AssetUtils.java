package com.aslan.simulategps.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AssetUtils {
	public static String getDataFromAssets(Context context, String string) {
		String ENCODING = "UTF-8";
		String result = "";
		try {
			InputStream is = context.getAssets().open(string);
			int reads = is.available();
			byte[] buffer = new byte[reads];
			is.read(buffer);
			result = EncodingUtils.getString(buffer, ENCODING);
			result = result.replace("\r\n", "").replace(" ", "");
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	public static boolean checkNetwork(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            return true;
        }
        return false;
    }
	public static boolean checkNetworkByIP(String ServerIP){
		boolean state = false;
		try {
			InetAddress InwardAd = InetAddress.getByName(ServerIP);
			state = InwardAd.isReachable(1000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return state;
	}
}
