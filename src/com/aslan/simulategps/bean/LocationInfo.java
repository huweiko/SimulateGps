package com.aslan.simulategps.bean;

public class LocationInfo {
	private String UTC;
	private Double Latitude = 2310.3762187;// 纬度，格式为ddmm.mmmm（前导位数不足则补0）。
	private Double Longitude = 11326.0308251;//经度，格式为dddmm.mmmm（前导位数不足则补0）。
	private String LevelAccuracy = "0.5";// 水平精确度，0.5到99.9。
	private String Height = "1";//天线离海平面的高度，-9999.9到9999.9米
	private String GroundSpeed;//地面速率（000.0~999.9节，Knot，前导位数不足则补0）
	private String GroundCourse;//地面航向（000.0~359.9度，以真北为参考基准，前导位数不足则补0）
	public String getUTC() {
		return UTC;
	}
	public void setUTC(String uTC) {
		UTC = uTC;
	}
	public Double getLatitude() {
		return Latitude;
	}
	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}
	public Double getLongitude() {
		return Longitude;
	}
	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}
	public String getLevelAccuracy() {
		return LevelAccuracy;
	}
	public void setLevelAccuracy(String levelAccuracy) {
		LevelAccuracy = levelAccuracy;
	}
	public String getHeight() {
		return Height;
	}
	public void setHeight(String height) {
		Height = height;
	}
	public String getGroundSpeed() {
		return GroundSpeed;
	}
	public void setGroundSpeed(String groundSpeed) {
		GroundSpeed = groundSpeed;
	}
	public String getGroundCourse() {
		return GroundCourse;
	}
	public void setGroundCourse(String groundCourse) {
		GroundCourse = groundCourse;
	}
	public String getMagneticVariation() {
		return MagneticVariation;
	}
	public void setMagneticVariation(String magneticVariation) {
		MagneticVariation = magneticVariation;
	}
	public String getDeclination() {
		return Declination;
	}
	public void setDeclination(String declination) {
		Declination = declination;
	}
	private String MagneticVariation;//Magnetic Variation，磁偏角（000.0~180.0度，前导位数不足则补0）
	private String Declination;// Declination，磁偏角方向，E（东）或W（西）
}
