package com.aslan.simulategps.BluetoothChat;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * ����������
 */
public class BluetoothTools {
	private static BluetoothAdapter adapter = BluetoothAdapter
			.getDefaultAdapter();

	/**
	 * ��������ʹ�õ�UUID
	 */
	public static final UUID PRIVATE_UUID = UUID
			.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");

	/**
	 * �ַ��������������Intent�е��豸����
	 */
	public static final String DEVICE = "DEVICE";

	/**
	 * �ַ��������������������豸�б��е�λ��
	 */
	public static final String SERVER_INDEX = "SERVER_INDEX";

	/**
	 * �ַ���������Intent�е�����
	 */
	public static final String DATA = "DATA";

	/**
	 * Action���ͱ�ʶ����Action���� Ϊ��������
	 */
	public static final String ACTION_READ_DATA = "ACTION_READ_DATA";

	/**
	 * Action���ͱ�ʶ����Action����Ϊ δ�����豸
	 */
	public static final String ACTION_NOT_FOUND_SERVER = "ACTION_NOT_FOUND_DEVICE";

	/**
	 * Action���ͱ�ʶ����Action����Ϊ ��ʼ�����豸
	 */
	public static final String ACTION_START_DISCOVERY = "ACTION_START_DISCOVERY";

	/**
	 * Action���豸�б�
	 */
	public static final String ACTION_FOUND_DEVICE = "ACTION_FOUND_DEVICE";

	/**
	 * Action��ѡ����������ӵ��豸
	 */
	public static final String ACTION_SELECTED_DEVICE = "ACTION_SELECTED_DEVICE";

	/**
	 * Action������������
	 */
	public static final String ACTION_START_SERVER = "ACTION_STARRT_SERVER";

	/**
	 * Action���رպ�̨Service
	 */
	public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

	/**
	 * Action����Service������
	 */
	public static final String ACTION_DATA_TO_SERVICE = "ACTION_DATA_TO_SERVICE";

	/**
	 * Action������Ϸҵ���е�����
	 */
	public static final String ACTION_DATA_TO_GAME = "ACTION_DATA_TO_GAME";

	/**
	 * Action�����ӳɹ�
	 */
	public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";

	/**
	 * Action�����Ӵ���
	 */
	public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";

	/**
	 * Message���ͱ�ʶ�������ӳɹ�
	 */
	public static final int MESSAGE_CONNECT_SUCCESS = 0x00000002;

	/**
	 * Message������ʧ��
	 */
	public static final int MESSAGE_CONNECT_ERROR = 0x00000003;

	/**
	 * Message����ȡ��һ������
	 */
	public static final int MESSAGE_READ_OBJECT = 0x00000004;

	/**
	 * ����������
	 */
	public static void openBluetooth() {
		adapter.enable();
	}

	/**
	 * �ر���������
	 */
	public static void closeBluetooth() {
		adapter.disable();
	}

	/**
	 * �����������ֹ���
	 * 
	 * @param duration
	 *            �����������ֹ��ܴ򿪳���������ֵΪ0��300֮���������
	 */
	public static void openDiscovery(int duration) {
		if (duration <= 0 || duration > 300) {
			duration = 200;
		}
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
	}

	/**
	 * ֹͣ��������
	 */
	public static void stopDiscovery() {
		adapter.cancelDiscovery();
	}

	//�����������ַ�����Ҫ���ӵ���̨�ֻ���������ַ��Ϊ �˱�֤��װ�˸ó�����ֻ������������ʹ�ã����Է�������ַ��
	//��������ַ��BluetoothClientService���е�discoveryReceiver��ʹ��
	//��ַ����������һ̨�ֻ���Ϊ�ͻ���������ƥ���ַ�ķ����ʱ���Զ���Բ�����
	/**
	 * Ҫ���ӵ�������ַ
	 */
	public static final String BluetoothAddress = "5C:DA:D4:1C:AB:36";//�������ߵĲ��Ե�ַ�����滻
	/**
	 * Ҫ���ӵ�������ַ2
	 */
	public static final String BluetoothAddress2="04:18:0F:30:A8:70";//�������ߵĲ��Ե�ַ�����滻
}
