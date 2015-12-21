package com.aslan.simulategps.BluetoothChat;

import java.io.Serializable;

/**
 * ���ڴ����������
 */
public class TransmitBean implements Serializable {
	private String msg = "";

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
