package com.aslan.simulategps.activity;

import java.io.IOException;
import java.net.Socket;
import com.aslan.simulategps.R;
import com.aslan.simulategps.base.BaseActivity;
import com.aslan.simulategps.bean.Constant.Preference;
import com.aslan.simulategps.thread.NetCheckThread;
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
	Thread myThread = null;
	Socket socket = null;
	String IPAddr ;
	int Port;
	NetCheckThread mNetCheckThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tcp_client);

		connButton= (Button)findViewById(R.id.myButton);
		edIp = (EditText)findViewById(R.id.ip);
		edPort = (EditText)findViewById(R.id.port);
		IPAddr = preferences.getString(Preference.SERVERIP, "192.168.0.1");
		Port = preferences.getInt(Preference.PORT, 0);
		edIp.setText(IPAddr);
		edPort.setText(Port+"");
		
		connButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IPAddr = edIp.getText().toString();
				Port = Integer.parseInt(edPort.getText().toString());
				/*myThread = new Thread(updataThread);
				myThread.start();*/
				saveUser();
				if(BluetoothChatActivity.mNetDataRecvThread != null){
					BluetoothChatActivity.mNetDataRecvThread.updateIP();
				}
			}
		});
	}
	Runnable updataThread = new Runnable(){
		@Override
		public void run(){
		
			try {
				socket = new Socket(IPAddr, Port);
				handler.sendEmptyMessage(1);
				saveUser();
				if(BluetoothChatActivity.mNetDataRecvThread != null){
					BluetoothChatActivity.mNetDataRecvThread.updateIP();
				}
				socket.close();
			} catch (IOException e) {
				handler.sendEmptyMessage(2);
				e.printStackTrace();
			}	
		}
			
	};
	final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
            	Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
            }
            else if(msg.what == 2){
            	Toast.makeText(getApplicationContext(), "保存失败,连接不上服务器", Toast.LENGTH_SHORT).show();
			}
        }
    };
    public void saveUser() {
		preferences.edit().putString(Preference.SERVERIP, IPAddr).commit();
		preferences.edit().putInt(Preference.PORT, Port).commit();
	}


	
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
	@Override
	protected void thisFinish() {
		// TODO Auto-generated method stub
		finish();
	}


}
