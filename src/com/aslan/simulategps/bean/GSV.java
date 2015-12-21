package com.aslan.simulategps.bean;

public class GSV {
	public static final int GP = 1;//美国GPS
	public static final int GL = 2;//俄罗斯GLONASS
	public static final int BD = 3;//中国北斗
	private int type;
	private String bianhao;//卫星编号
	private String yangjiao;//卫星仰角
	private String fangweijiao;//卫星方位角
	private String xinzaobi;//信噪比
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getBianhao() {
		return bianhao;
	}
	public void setBianhao(String bianhao) {
		this.bianhao = bianhao;
	}
	public String getYangjiao() {
		return yangjiao;
	}
	public void setYangjiao(String yangjiao) {
		this.yangjiao = yangjiao;
	}
	public String getFangweijiao() {
		return fangweijiao;
	}
	public void setFangweijiao(String fangweijiao) {
		this.fangweijiao = fangweijiao;
	}
	public String getXinzaobi() {
		return xinzaobi;
	}
	public void setXinzaobi(String xinzaobi) {
		this.xinzaobi = xinzaobi;
	}
	
}
