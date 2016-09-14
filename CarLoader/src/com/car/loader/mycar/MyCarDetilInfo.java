package com.car.loader.mycar;

import com.car.loader.R;
import com.car.loader.domain.CarBaseInfo;
import com.car.loader.five.ShowMaintainInfos;
import com.car.loader.five.ShowMyMaintainInfos;

import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 显示车辆的基本信息
 * @author Zsy
 *
 */
public class MyCarDetilInfo extends Activity {
	private ImageView iv_sign;
	private TextView tv_brand;
	private TextView tv_type;
	private TextView tv_number;
	private TextView tv_engin_number;
	private TextView tv_bodylevel;
	private String carnumber ;
	private CarBaseInfo info;
	private LinearLayout ll_surround;
	private ProgressBar pb_loader,pb_loader1;
	private Button btn_query_all,btn_query_my;
	private String phone;
	private SharedPreferences sp;
	private HeaderView h_header;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycar_detilinfo);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyCarDetilInfo.this.finish();
			}
		});
		sp = getSharedPreferences("config", MODE_PRIVATE);
		phone = sp.getString("phone", "");
		carnumber = getIntent().getExtras().getString("carnumber");
		ll_surround = (LinearLayout) findViewById(R.id.ll_surround);
		pb_loader = (ProgressBar) findViewById(R.id.pb_loader);
		pb_loader1 = (ProgressBar) findViewById(R.id.pb_loader1);
		iv_sign = (ImageView) findViewById(R.id.iv_sign);
		tv_brand = (TextView) findViewById(R.id.tv_brand);
		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_engin_number = (TextView) findViewById(R.id.tv_engin_number);
		tv_bodylevel = (TextView) findViewById(R.id.tv_bodylevel);
		btn_query_all = (Button) findViewById(R.id.btn_query_all);
		btn_query_my = (Button) findViewById(R.id.btn_query_my);
		new Thread(new Runnable() {
			public void run() {
				try {
					//System.out.println("carnumber="+carnumber);
					info = TalkWithInternet.toGetBaseInfoByCarNumber(carnumber);
					runOnUiThread(new Runnable() {
						public void run() {
							if(info==null){
								Toast.makeText(MyCarDetilInfo.this, "登陆过期，请重新登陆", 0).show();
								Intent i = new Intent();
								i.setAction("com.car.loader.logout");
								i.addCategory("android.intent.category.DEFAULT");
								sendBroadcast(i);
								return;
							}
							iv_sign.setImageBitmap(info.getBitmap());
							tv_bodylevel.setText(info.getCarbodylevel());
							tv_brand.setText(info.getCarbrand());
							tv_engin_number.setText(info.getCarenginnumber());
							tv_number.setText(info.getCarnumber());
							tv_type.setText(info.getCartype());
							//System.out.println("0000000000000000000000000");
							ll_surround.setVisibility(View.VISIBLE);
							btn_query_all.setVisibility(View.VISIBLE);
							btn_query_my.setVisibility(View.VISIBLE);
							pb_loader.setVisibility(View.INVISIBLE);
							pb_loader1.setVisibility(View.INVISIBLE);
							//System.out.println("11111111111111111111111111");
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(MyCarDetilInfo.this, "网络错误^~^", 0).show();
							pb_loader.setVisibility(View.INVISIBLE);
						}
					});
				}finally{
					runOnUiThread(new  Runnable() {
						public void run() {
							pb_loader.setVisibility(View.INVISIBLE);
						}
					});
					
				}
			}
		}).start();
	}
	/**
	 * 查询
	 * @param view
	 */
	public void query(View view){
		Intent intent = new Intent(this,ShowMaintainInfos.class);
		intent.putExtra("carnumber", info.getCarnumber());
		intent.putExtra("phone", phone);
		startActivity(intent);
	}
	/**
	 * 查询本手机号
	 * @param view
	 */
	public void queryMy(View view){
		Intent intent = new Intent(this,ShowMyMaintainInfos.class);
		intent.putExtra("carnumber", info.getCarnumber());
		intent.putExtra("phone", phone);
		startActivity(intent);
	}
}
