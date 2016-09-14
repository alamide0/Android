package com.car.loader.receiver;

import com.car.loader.service.MaintainCarInfoService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
/**
 * �㲥�����ߣ����������½��ע��
 * @author Zsy
 *
 */
public class LoginStateReceiver extends BroadcastReceiver {

	private static String LOGIN = "com.car.loader.login";//��½
	private static String LOGOUT = "com.car.loader.logout";//ע��
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		if (action.equals(LOGIN)) {
			boolean b = sp.getBoolean("isLogin", false);
			if (b) {//����ǵ�½״̬����������
				Intent i = new Intent(context, MaintainCarInfoService.class);
				context.startService(i);
				System.out.println("��������");
			}
		} else if (action.equals(LOGOUT)) {//ע��ʱ�رշ���
			System.out.println("logout");
			Intent service = new Intent(context, MaintainCarInfoService.class);
			Editor editor = sp.edit();
			editor.putBoolean("isLogin", false);
			//editor.putString("onlycode", "");
			//editor.putString("phone", "");
			editor.commit();
			context.stopService(service);
		}

	}

}
