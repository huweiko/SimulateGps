package com.aslan.simulategps.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.aslan.simulategps.R;
import com.aslan.simulategps.base.BaseActivity;
import com.aslan.simulategps.bean.Constant.Preference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/****************************************************
 * 总结：
 * 1. 在子线程、非UI线程中， 不能刷新UI界面
 * 2. 在非UI线程中， 不能使用Toast弹出提示信息。
 * 3. 在监听器中，不能使用阻塞式的程序
 * 4. 违反上述三种，必然宕机， 原因未知。
 */

public class TcpClientActivity extends BaseActivity {

	private Button connButton;
	private EditText edIp;
	private EditText edPort;
	
	private EditText rcvdata;
	private EditText snddata;
	private Button sndButton;
	
	boolean conn = false;
	Thread myThread = null;
	Socket socket = null;
	String IPAddr ;
	int Port;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tcp_client);

		/*
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		*/
		
		connButton= (Button)findViewById(R.id.myButton);
		edIp = (EditText)findViewById(R.id.ip);
		edPort = (EditText)findViewById(R.id.port);
		
		rcvdata = (EditText)findViewById(R.id.rcvdata);
		snddata = (EditText)findViewById(R.id.snddata);
		sndButton = (Button)findViewById(R.id.sndButton);
		IPAddr = preferences.getString(Preference.SERVERIP, "192.168.0.1");
		Port = preferences.getInt(Preference.PORT, 0);
		edIp.setText(IPAddr);
		edPort.setText(Port+"");
		
		connButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if( conn == false){
					conn = true;
					IPAddr = edIp.getText().toString();
					Port = Integer.parseInt(edPort.getText().toString());
					connButton.setText(R.string.disconn);
					
					myThread = new Thread(updataThread);
					myThread.start();//
				}else{
					conn = false;
					connButton.setText(R.string.conn);
				}
			}
		});
		
		sndButton.setOnClickListener(new View.OnClickListener() {
			String edData;
			int len;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//未连接
				if(conn == false){
					System.out.println("disconnect state");
					return;  
				}
				
				edData = snddata.getText().toString();
				len = edData.length();					
				if( len > 0){
					OutputStream outputStream;
					try {
						outputStream = socket.getOutputStream();
						byte buff[] = edData.getBytes("GBK");
						len = buff.length;				
						outputStream.write(buff, 0, len);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			}
		});
	}

	final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
            	String receiveData = new String((String)msg.obj);
            	String str = (String)msg.obj;
//            	rcvdata.setText((String)msg.obj);
            	Toast.makeText(TcpClientActivity.this, "当前值为：" + receiveData, Toast.LENGTH_SHORT).show();
            	try {
					BluetoothChatActivity.mChatService.write(str.getBytes("GBK"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    };
    public void saveUser() {
		preferences.edit().putString(Preference.SERVERIP, IPAddr).commit();
		preferences.edit().putInt(Preference.PORT, Port).commit();
	}


	Runnable updataThread = new Runnable(){
		@Override
		public void run(){
		
			try {
				socket = new Socket(IPAddr, Port);
				saveUser();
				InputStream inputstream = socket.getInputStream();
				OutputStream outputstream = socket.getOutputStream();
							
				byte buffer[] = new byte[1024];
				int len;
				String receiveData ;
				while(conn){
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
				}
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
			
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
	};
	@Override
	protected Object getContentViewId() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void IniView() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void IniLister() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void IniData() {
		// TODO Auto-generated method stub
		
	}


}
