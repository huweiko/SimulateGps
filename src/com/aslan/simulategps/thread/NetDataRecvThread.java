package com.aslan.simulategps.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.aslan.simulategps.activity.BluetoothChatActivity;
import com.aslan.simulategps.activity.TcpClientActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class NetDataRecvThread extends Thread {
	String Tag = getClass().getSimpleName();
	boolean isRunning = true;
	Context mContext;
	Socket socket;
	String IPAddr ;
	int Port;
	public NetDataRecvThread(Context context,String ip,int port) {
		mContext = context;
		IPAddr = ip;
		Port = port;
	}
	
	@Override
	public void run() {		
		while (isRunning) {

			
			try {
				Log.i(Tag, "Tcp连接："+IPAddr+":"+Port);
				socket = new Socket(IPAddr, Port);
				InputStream inputstream = socket.getInputStream();
				OutputStream outputstream = socket.getOutputStream();
							
				byte buffer[] = new byte[1024];
				int len;
				String receiveData ;
				while(true){
					//接收网络数据并回显
					if( (len = inputstream.read(buffer)) != -1){
						 receiveData = new String(buffer, 0, len,"GBK");
						 System.out.println("当前值为：" + receiveData);
						 outputstream.write(buffer, 0, len);
						 
						 //发送消息到由handler处理
						 Message message = new Message();
		                 message.what = 1;
		                 message.obj = receiveData;
		                 handler.sendMessage(message);
					}else{
						break;
					}
				/*	try {
						Thread.sleep(1000);
						Log.i("tcp接收线程", new Date().toGMTString());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				}
				socket.close();
			} catch (IOException e) {
			}	
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
            	String receiveData = new String((String)msg.obj);
            	String str = (String)msg.obj;
//            	rcvdata.setText((String)msg.obj);
            	Toast.makeText(mContext, "当前值为：" + receiveData, Toast.LENGTH_SHORT).show();
            	try {
					BluetoothChatActivity.mChatService.write(str.getBytes("GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    };

	public void setRunning(boolean b) {
		isRunning = b;
	}
}
