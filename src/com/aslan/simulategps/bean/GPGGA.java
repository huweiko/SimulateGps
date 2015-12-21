package com.aslan.simulategps.bean;

public class GPGGA {
	private String UTCDay;// UTC时间，格式为hhmmss.sss。
	private String Latitude;// 纬度，格式为ddmm.mmmm（前导位数不足则补0）。
	private String LatitudePosition;//纬度半球，N或S（北纬或南纬）。
	private String Longitude;//经度，格式为dddmm.mmmm（前导位数不足则补0）。
	private String LongitudePosition;//经度半球，E或W（东经或西经）。
	
	private String PositioningQualityIndicator;//定位质量指示，0=定位无效，1=定位有效。
	private String UseSatelliteNumber;//使用卫星数量，从00到12（前导位数不足则补0）
	private String LevelAccuracy;// 水平精确度，0.5到99.9。
	private String Height;//天线离海平面的高度，-9999.9到9999.9米
	private String HeightUnit;// 高度单位，M表示单位米。
	private String EllipseHeight;//大地椭球面相对海平面的高度（-999.9到9999.9）。
	private String EllipseHeightUnit;// 高度单位，M表示单位米。
	private String DifferentialGPSData;//差分GPS数据期限（RTCM SC-104），最后设立RTCM传送的秒数量。
	private String DifferentialReference;//差分参考基站标号，从0000到1023（前导位数不足则补0）。
	public String getUTCDay() {
		return UTCDay;
	}
	public void setUTCDay(String uTCDay) {
		UTCDay = uTCDay;
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
	public String getPositioningQualityIndicator() {
		return PositioningQualityIndicator;
	}
	public void setPositioningQualityIndicator(String positioningQualityIndicator) {
		PositioningQualityIndicator = positioningQualityIndicator;
	}
	public String getUseSatelliteNumber() {
		return UseSatelliteNumber;
	}
	public void setUseSatelliteNumber(String useSatelliteNumber) {
		UseSatelliteNumber = useSatelliteNumber;
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
	public String getHeightUnit() {
		return HeightUnit;
	}
	public void setHeightUnit(String heightUnit) {
		HeightUnit = heightUnit;
	}
	public String getEllipseHeight() {
		return EllipseHeight;
	}
	public void setEllipseHeight(String ellipseHeight) {
		EllipseHeight = ellipseHeight;
	}
	public String getEllipseHeightUnit() {
		return EllipseHeightUnit;
	}
	public void setEllipseHeightUnit(String ellipseHeightUnit) {
		EllipseHeightUnit = ellipseHeightUnit;
	}
	public String getDifferentialGPSData() {
		return DifferentialGPSData;
	}
	public void setDifferentialGPSData(String differentialGPSData) {
		DifferentialGPSData = differentialGPSData;
	}
	public String getDifferentialReference() {
		return DifferentialReference;
	}
	public void setDifferentialReference(String differentialReference) {
		DifferentialReference = differentialReference;
	}
	
}
