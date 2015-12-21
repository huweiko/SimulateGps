package com.aslan.simulategps.bean;

public class GPRMC {
	private String UTCDay;//UTC（Coordinated Universal Time）时间，hhmmss（时分秒）格式
	private String LocationStatus;//定位状态，A=有效定位，V=无效定位
	private String Latitude;// Latitude，纬度ddmm.mmmm（度分）格式（前导位数不足则补0）
	private String LatitudePosition;//纬度半球N（北半球）或S（南半球）
	private String Longitude;//Longitude，经度dddmm.mmmm（度分）格式（前导位数不足则补0）
	private String LongitudePosition;//经度半球E（东经）或W（西经）
	private String GroundSpeed;//地面速率（000.0~999.9节，Knot，前导位数不足则补0）
	private String GroundCourse;//地面航向（000.0~359.9度，以真北为参考基准，前导位数不足则补0）
	private String UTCYear;// UTC日期，ddmmyy（日月年）格式
	private String MagneticVariation;//Magnetic Variation，磁偏角（000.0~180.0度，前导位数不足则补0）
	private String Declination;// Declination，磁偏角方向，E（东）或W（西）
	private String ModeIndicator;// Mode Indicator，模式指示（仅NMEA0183 3.00版本输出，A=自主定位，D=差分，E=估算，N=数据无效）
	
	public String getUTCDay() {
		return UTCDay;
	}
	public void setUTCDay(String uTCDay) {
		UTCDay = uTCDay;
	}
	public String getLocationStatus() {
		return LocationStatus;
	}
	public void setLocationStatus(String locationStatus) {
		LocationStatus = locationStatus;
	}
	public String getLatitude() {
		return Latitude;
	}
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}
	public String getLatitudePosition() {
		return LatitudePosition;
	}
	public void setLatitudePosition(String latitudePosition) {
		LatitudePosition = latitudePosition;
	}
	public String getLongitude() {
		return Longitude;
	}
	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
	public String getLongitudePosition() {
		return LongitudePosition;
	}
	public void setLongitudePosition(String longitudePosition) {
		LongitudePosition = longitudePosition;
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
	public String getUTCYear() {
		return UTCYear;
	}
	public void setUTCYear(String uTCYear) {
		UTCYear = uTCYear;
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
	public String getModeIndicator() {
		return ModeIndicator;
	}
	public void setModeIndicator(String modeIndicator) {
		ModeIndicator = modeIndicator;
	}
	
}
