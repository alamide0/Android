package com.car.loader;
/**
 * 此类已被MainActivity代替，但保留以防止后面修改
 */
import java.util.List;

import com.car.loader.domain.CarMusicLoader;
import com.car.loader.domain.CarMusicLoader.MusicInfo;
import com.car.loader.first.BeLoginActivity;
import com.car.loader.five.ScanInfoActivity;
import com.car.loader.five.ShowMaintainInfos;
import com.car.loader.map.RoutePlanDemo;
import com.car.loader.map._RoutePlanDemo;
import com.car.loader.music.MusicHomeActivity;
import com.car.loader.mycar.MyCarInfoActivity;
import com.car.loader.service.CarMusicService;
import com.car.loader.service.CarMusicService.NatureBinder;
import com.car.loader.service.MaintainCarInfoService;
import com.car.loader.two.OrderOilActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PreActivity extends Activity {
	private GridView gv_menu;
	private String[] text = { "第一模块", "第四模块", "第五模块", "我的车辆", "我的音乐", "第二模块" };
	private int[] res = { R.drawable.login, R.drawable.routeplan, R.drawable.maintain, R.drawable.maininfo,
			R.drawable.music, R.drawable.oilorder };
	private SharedPreferences sp;
	private NatureBinder natureBinder;
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			natureBinder = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			natureBinder = (NatureBinder) service;
			natureBinder.startPlay(2, 0);
		}
	};
	private List<MusicInfo> musicList;

	private void connectToNatureService() {

		Intent intent = new Intent(this, CarMusicService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		System.out.println("绑定成功");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (natureBinder != null) {
			unbindService(serviceConnection);
			natureBinder = null;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pre);
		 musicList =
		 CarMusicLoader.instance(getContentResolver()).getMusicList();
		 if (musicList != null && musicList.size() > 0) {
		 connectToNatureService();
		
		 }else{
		 Toast.makeText(this, "本地无音乐", 0).show();
		 }

		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		Intent i = new Intent();
		i.setAction("com.car.loader.login");
		i.addCategory("android.intent.category.DEFAULT");
		sendBroadcast(i);

		gv_menu = (GridView) findViewById(R.id.gv_menu);
		gv_menu.setAdapter(new MyAdapter());
		gv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					Intent intent = new Intent(PreActivity.this, BeLoginActivity.class);
					startActivity(intent);
					break;
				case 1:
					Intent intent1 = new Intent(PreActivity.this, _RoutePlanDemo.class);
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(PreActivity.this, ScanInfoActivity.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent(PreActivity.this, MyCarInfoActivity.class);
					startActivity(intent3);
					break;
				case 4:
					Intent intent4 = new Intent(PreActivity.this, MusicHomeActivity.class);
					startActivity(intent4);
					break;
				case 5:
					Intent intent5 = new Intent(PreActivity.this, OrderOilActivity.class);
					startActivity(intent5);
					break;
				}

			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return text.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(PreActivity.this, R.layout.item_pre, null);
			ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);
			TextView tv_text = (TextView) view.findViewById(R.id.tv_text);
			iv_image.setImageResource(res[position]);
			tv_text.setText(text[position]);

			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}
