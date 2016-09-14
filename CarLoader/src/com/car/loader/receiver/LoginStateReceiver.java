package com.car.loader.receiver;

import com.car.loader.service.MaintainCarInfoService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
/**
 * 广播接收者，用来处理登陆与注销
 * @author Zsy
 *
 */
public class LoginStateReceiver extends BroadcastReceiver {

	private static String LOGIN = "com.car.loader.login";//登陆
	private static String LOGOUT = "com.car.loader.logout";//注销
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		if (action.equals(LOGIN)) {
			boolean b = sp.getBoolean("isLogin", false);
			if (b) {//如果是登陆状态，则开启服务
				Intent i = new Intent(context, MaintainCarInfoService.class);
				context.startService(i);
				System.out.println("服务开启啦");
			}
		} else if (action.equals(LOGOUT)) {//注销时关闭服务
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
