package com.car.loader.service;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.car.loader.R;
import com.car.loader.five.FixInfoActivity;
import com.car.loader.five.ScanInfoActivity;
import com.car.loader.internet.TalkWithInternet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;
/**
 * 这是本应用程序与远端服务器交互的重要渠道，非常关键
 * @author Zsy
 *
 */
public class MaintainCarInfoService extends Service {

	private TimerTask task,task2;
	/**
	 * 用来与服务器交互，查看是否车辆有异常信息
	 */
	private Timer timer;
	/**
	 * 用来与服务器交互，查看是否是单点登录
	 */
	private Timer timer2;
	
	private String phone;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		startMaintain();
	}

	private String onlycode;
	/**
	 * 开始交互
	 */
	private void startMaintain() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		phone = sp.getString("phone", "");
		timer = new Timer();
		timer2 = new Timer();
		task2 = new TimerTask() {
			
			@Override
			public void run() {
				try {
					onlycode = sp.getString("onlycode", "");
					System.out.println("onlycode="+onlycode);
					if(!TextUtils.isEmpty(onlycode)){
						String getcode = TalkWithInternet.toQueryLoginOnlyCode(phone);
						if(!"110".equals(getcode)&&!getcode.equals(onlycode)&&!TextUtils.isEmpty(getcode)){
						
							Intent i = new Intent();
							i.setAction("com.car.loader.logout");
							i.addCategory("android.intent.category.DEFAULT");
							sendBroadcast(i);
							
							NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
							Notification not = new Notification(R.drawable.ic_launcher, "在其它终端登录",
									System.currentTimeMillis());
							Intent intent = new Intent(MaintainCarInfoService.this, FixInfoActivity.class);
							String msg = "您的账号在其它终端登录，可能是密码泄露，如不是本人操作，请尽快修改您的密码";
							intent.putExtra("info", msg);
							PendingIntent pend = PendingIntent.getActivity(MaintainCarInfoService.this, 0, intent,
									0);
							not.flags = Notification.FLAG_AUTO_CANCEL;
							not.setLatestEventInfo(MaintainCarInfoService.this, "密码泄露", msg, pend);
							nm.notify(0, not);
						}
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		};
		task = new TimerTask() {
			@Override
			public void run() {
				try {
					
					if (!TextUtils.isEmpty(phone)) {
						
						String result = TalkWithInternet.toGetCarAbnormalInfo(phone);
						if (!result.contains("null") && !result.contains("网络异常，请稍后再试")) {
							StringBuffer buffer = new StringBuffer();
							JSONArray array = new JSONArray(result);
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = new JSONObject(array.get(i).toString());
								String carnumber = object.optString("carnumber");
								String state = object.optString("state");
								int t = Integer.valueOf(state);
								buffer.append("您车牌号为:" + carnumber);
								if ((t & 1) == 1) {
									buffer.append("汽油量不足20%，请及时加油\n");
								}
								if ((t & 2) == 2) {
									buffer.append("超过15000KM，请及时保养车辆\n");
								}
								if ((t & 4) == 4) {
									buffer.append("发动机异常，请及时维修\n");
								}
								if ((t & 8) == 8) {
									buffer.append("制动器异常，请及时为修\n");
								}
								if ((t & 16) == 16) {
									buffer.append("车灯异常，请及时维修哦\n");
								}
							}
							//System.out.println("buffer=" + buffer.toString());
							if (buffer.toString().length() > 0) {
								NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
								Notification not = new Notification(R.drawable.ic_launcher, "友情提示",
										System.currentTimeMillis());
								Intent intent = new Intent(MaintainCarInfoService.this, FixInfoActivity.class);
								intent.putExtra("info", buffer.toString());
								PendingIntent pend = PendingIntent.getActivity(MaintainCarInfoService.this, 0, intent,
										0);
								not.flags = Notification.FLAG_AUTO_CANCEL;
								not.setLatestEventInfo(MaintainCarInfoService.this, "车辆维护信息", buffer.toString(), pend);
								nm.notify(0, not);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		timer.schedule(task, 0, 1000 * 100);
		timer2.schedule(task2, 0,1000*100);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		if (task != null) {
			task.cancel();
		}
		if (timer2 != null) {
			timer2.cancel();
		}
		if (task2 != null) {
			task2.cancel();
		}
		timer = null;
		task = null;
		timer2 = null;
		task2 = null;
		sp = null;
		phone = null;
	}

	 
}
