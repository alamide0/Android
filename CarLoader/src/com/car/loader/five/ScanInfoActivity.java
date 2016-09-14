package com.car.loader.five;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.car.loader.R;
import com.car.loader.domain.CarInfo;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;
import com.car.loader.util.AnswerInfo;
import com.car.loader.util.AnswerJSON;
import com.car.loader.util.AnswerString;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 维护车辆时，扫描二维码用，用的是开源库ZXing，此功能需要登录后才可以使用
 * @author Zsy
 *
 */
public class ScanInfoActivity extends Activity {
	private ImageView iv_sign;
	private TextView tv_brand;
	private TextView tv_type;
	private TextView tv_number;
	private TextView tv_engin_number;
	private TextView tv_bodylevel;
	private TextView tv_light;
	private TextView tv_mileage;
	private TextView tv_oil;
	private TextView tv_engin_state;
	private TextView tv_tran_state;
	private ProgressBar pb_loader;
	private String phone;
	private SharedPreferences sp;
	private HeaderView h_header;
	private boolean isFinish;
	// private int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		phone = sp.getString("phone", "");
		boolean b = sp.getBoolean("isLogin", false);
		isFinish = true;
		if (!b) {
			Toast.makeText(this, "此功能需要登录才能使用！", 0).show();
			this.finish();
			return;
		}
		// test();
		// count++;

		setContentView(R.layout.activity_scan);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ScanInfoActivity.this.finish();
			}
		});
		iv_sign = (ImageView) findViewById(R.id.iv_sign);
		tv_brand = (TextView) findViewById(R.id.tv_brand);
		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_engin_number = (TextView) findViewById(R.id.tv_engin_number);
		tv_bodylevel = (TextView) findViewById(R.id.tv_bodylevel);
		tv_light = (TextView) findViewById(R.id.tv_light);
		tv_mileage = (TextView) findViewById(R.id.tv_mileage);
		tv_oil = (TextView) findViewById(R.id.tv_oil);
		tv_engin_state = (TextView) findViewById(R.id.tv_engin_state);
		tv_tran_state = (TextView) findViewById(R.id.tv_tran_state);
		pb_loader = (ProgressBar) findViewById(R.id.pb_loader);
		Intent intent = new Intent(this, CaptureActivity.class);
		startActivityForResult(intent, 0);
		// test();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(isFinish){
			ScanInfoActivity.this.finish();
		}
	}
/**
 * 测试用的，由于电脑模拟器不方便打开摄像头
 */
	private void test() {
		String i = "大众,http://139.129.53.67/CarLoader/dazhong.bmp,桑塔纳2000,苏H123456,NF4561289,四门五座,16000,0.23,0,1,1";
		try {
			final CarInfo in = AnswerString.answerString(i, phone);
			new Thread(new Runnable() {
				public void run() {
					try {
						final Bitmap map = TalkWithInternet.toGetBitmap(in.getCarsign());
						// final Bitmap map = BitmapFactory.decodeByteArray(b,
						// 0, b.length);
						runOnUiThread(new Runnable() {
							public void run() {
								iv_sign.setImageBitmap(map);
							}
						});

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}).start();

			tv_brand.setText(in.getCarbrand());
			tv_type.setText(in.getCartype());
			tv_number.setText(in.getCarnumber());
			tv_engin_number.setText(in.getCarenginnumber());
			tv_bodylevel.setText(in.getCarbodylevel());
			int t = Integer.parseInt(in.getCarlightstate());
			if (t == 0)
				tv_light.setText("坏");
			else
				tv_light.setText("好");
			tv_mileage.setText(in.getCarmileage() + "KM");
			double d = Double.parseDouble(in.getCaroil());
			d = d * 100;
			tv_oil.setText(d + "%");
			t = Integer.parseInt(in.getCarenginstate());
			if (t == 0)
				tv_engin_state.setText("坏");
			else
				tv_engin_state.setText("好");
			t = Integer.parseInt(in.getCartranstate());
			if (t == 0)
				tv_tran_state.setText("坏");
			else
				tv_tran_state.setText("坏");
			new Thread(new Runnable() {
				public void run() {
					try {
						TalkWithInternet.toMaintainBaseCarInfo(in);
						String str = TalkWithInternet.toMaintainCarMaintainInfo(in);
						int t = Integer.valueOf(str);
						StringBuffer buffer = new StringBuffer();
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

						NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						Notification not = new Notification(R.drawable.ic_launcher, "友情提示", System.currentTimeMillis());
						Intent intent = new Intent(ScanInfoActivity.this, FixInfoActivity.class);
						intent.putExtra("info", buffer.toString());
						PendingIntent pend = PendingIntent.getActivity(ScanInfoActivity.this, 0, intent, 0);
						not.flags = Notification.FLAG_AUTO_CANCEL;
						not.setLatestEventInfo(ScanInfoActivity.this, "车辆维护信息", buffer.toString(), pend);
						nm.notify(0, not);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			String info = data.getExtras().getString("result");
			isFinish = false;
			try {
				final CarInfo in = AnswerString.answerString(info, phone);
				if (in == null) {
					Toast.makeText(ScanInfoActivity.this, "解析错误", 0).show();
					ScanInfoActivity.this.finish();
					//System.out.println("Finish执行了");
					return;
				}
				new Thread(new Runnable() {
					public void run() {
						try {

							// count++;
							runOnUiThread(new Runnable() {
								public void run() {

									tv_brand.setText(in.getCarbrand());
									tv_type.setText(in.getCartype());
									tv_number.setText(in.getCarnumber());
									tv_engin_number.setText(in.getCarenginnumber());
									tv_bodylevel.setText(in.getCarbodylevel());
									int t = Integer.parseInt(in.getCarlightstate());
									if (t == 0)
										tv_light.setText("坏");
									else
										tv_light.setText("好");
									tv_mileage.setText(in.getCarmileage() + "KM");
									double d = Double.parseDouble(in.getCaroil());
									d = d * 100;
									tv_oil.setText(d + "%");
									t = Integer.parseInt(in.getCarenginstate());
									if (t == 0)
										tv_engin_state.setText("坏");
									else
										tv_engin_state.setText("好");
									t = Integer.parseInt(in.getCartranstate());
									if (t == 0)
										tv_tran_state.setText("坏");
									else
										tv_tran_state.setText("好");
								}
							});
							TalkWithInternet.toMaintainBaseCarInfo(in);
							String str = TalkWithInternet.toMaintainCarMaintainInfo(in);
							final Bitmap map = TalkWithInternet.toGetBitmap(in.getCarsign());
							runOnUiThread(new Runnable() {
								public void run() {
									if (map != null) {
										iv_sign.setImageBitmap(map);
										pb_loader.setVisibility(View.INVISIBLE);
									}
								}
							});
							int t = Integer.valueOf(str);
							System.out.println("t==" + t);
							String s = AnswerInfo.answerInfo(t);
							System.out.println("---"+s);
							NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
							Notification not = new Notification(R.drawable.ic_launcher, "友情提示",
									System.currentTimeMillis());
							Intent intent = new Intent(ScanInfoActivity.this, FixInfoActivity.class);
							intent.putExtra("info", s);
							System.out.println("000"+s);
							PendingIntent pend = PendingIntent.getActivity(ScanInfoActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
							not.flags = Notification.FLAG_AUTO_CANCEL;
							not.setLatestEventInfo(ScanInfoActivity.this, "车辆维护信息", s, pend);
							nm.notify(0, not);
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(ScanInfoActivity.this, "网络错误", 0).show();
									pb_loader.setVisibility(View.INVISIBLE);
								}
							});

						}
					}
				}).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
