package com.car.loader.mycar;

import java.util.ArrayList;
import java.util.List;

import com.car.loader.R;
import com.car.loader.domain.CarBaseInfo;
import com.car.loader.domain.SimpleInfo;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 用来显示本手机号所维护的车辆信息，点击车牌号便可显示该车牌号的基本信息
 * @author Zsy
 *
 */
public class MyCarInfoActivity extends Activity {
	private TextView tv_header;
	private ListView lv_car_info;
	private SharedPreferences sp ;
	private ProgressBar pb_loader;
	private List<CarBaseInfo> list;
	private String phone;
	private HeaderView h_header;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycar_info);
		sp =  getSharedPreferences("config", MODE_PRIVATE);
		
		if(!sp.getBoolean("isLogin", false)){
			Toast.makeText(this, "此功能需要登录后才可以使用！", 0).show();
			this.finish();
			return;
		}
		tv_header = (TextView) findViewById(R.id.tv_header);
		lv_car_info = (ListView) findViewById(R.id.lv_car_info);
		pb_loader = (ProgressBar) findViewById(R.id.pb_loader);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyCarInfoActivity.this.finish();
			}
		});
		
		lv_car_info.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object obj = lv_car_info.getItemAtPosition(position);
				if(obj!=null){
					Intent intent = new Intent(MyCarInfoActivity.this,MyCarDetilInfo.class);
					intent.putExtra("carnumber", list.get(position).getCarnumber());
					startActivity(intent);
				}
			}
		});
		phone = sp.getString("phone", "");
		//System.out.println(phone+"000000000000");
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "未知错误", 0).show();
			this.finish();
			return;
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					list = TalkWithInternet.toGetCarBaseInfo(phone);
					
					runOnUiThread(new Runnable() {
						public void run() {
							if(list==null){
								Toast.makeText(MyCarInfoActivity.this, "登陆过期，请重新登陆", 0).show();
								Intent i = new Intent();
								i.setAction("com.car.loader.logout");
								i.addCategory("android.intent.category.DEFAULT");
								sendBroadcast(i);
								return;
							}
							lv_car_info.setAdapter(new MyAdapter());
							tv_header.setText("您一共有"+list.size()+"辆车");
							lv_car_info.setVisibility(View.VISIBLE);
							tv_header.setVisibility(View.VISIBLE);
							pb_loader.setVisibility(View.INVISIBLE);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(MyCarInfoActivity.this, "网络错误^~^", 0).show();
							pb_loader.setVisibility(View.INVISIBLE);
						}
					});
				}finally {
					runOnUiThread(new Runnable() {
						public void run() {
							pb_loader.setVisibility(View.INVISIBLE);
						}
					});
					
				}
			}
		}).start();
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView==null){
				view = View.inflate(MyCarInfoActivity.this, R.layout.item_base_car, null);
				holder = new ViewHolder();
				holder.iv_car_sign = (ImageView) view.findViewById(R.id.iv_car_sign);
				holder.tv_car_number = (TextView) view.findViewById(R.id.tv_car_number);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			
			holder.iv_car_sign.setImageBitmap(list.get(position).getBitmap());
			holder.tv_car_number.setText(list.get(position).getCarnumber());
			return view;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	
	}
	
	private static class ViewHolder{
		private ImageView iv_car_sign;
		private TextView tv_car_number;
	}
	
	
}
