package com.car.loader;

import java.util.List;

import com.car.loader.domain.CarMusicLoader;
import com.car.loader.domain.CarMusicLoader.MusicInfo;
import com.car.loader.first.BeLoginActivity;
import com.car.loader.five.ScanInfoActivity;
import com.car.loader.illeagequery.IlleageQuery;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.map._RoutePlanDemo;
import com.car.loader.music.MusicHomeActivity;
import com.car.loader.mycar.MyCarInfoActivity;
import com.car.loader.petrolstation.PetrolStationMainActivity;
import com.car.loader.service.CarMusicService;
import com.car.loader.service.CarMusicService.NatureBinder;
import com.car.loader.setting.SettingActivity;
import com.car.loader.two.OrderOilActivity;
import com.car.loader.ui.MyGridAdapter;
import com.car.loader.ui.MyGridView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
/**
 * ����APP�������棬����Ӧ�û��������Ǹ������ܵ���ڴ�
 * ������滹�е����������ǣ������������Ĵ��Զ���������ʱ��APP�Զ���������
 * �Լ����͵�½�㲥���ڹ㲥�������д����Ƿ�����̨������������ķ���
 * @author Zsy
 *
 */
public class MainActivity extends Activity {
	private MyGridView gridview;
	private int musicListSize = 0;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();

		sp = getSharedPreferences("config", MODE_PRIVATE);
		if (sp.getBoolean("auto_play", true)) {
			startPlay();
		}
		Intent i = new Intent();
		i.setAction("com.car.loader.login");
		i.addCategory("android.intent.category.DEFAULT");
		sendBroadcast(i);
	}

	public void startPlay() {
		musicList = CarMusicLoader.instance(getContentResolver()).getMusicList();
		if (musicList != null && musicList.size() > 0) {
			musicListSize = musicList.size();
			connectToNatureService();

		} else {
			Toast.makeText(this, "����������", 0).show();
		}
	}

	private void initView() {
		gridview = (MyGridView) findViewById(R.id.gridview);

		gridview.setAdapter(new MyGridAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					Intent intent = new Intent(MainActivity.this, BeLoginActivity.class);
					startActivity(intent);
					break;
				case 1:
					Intent intent1 = new Intent(MainActivity.this, _RoutePlanDemo.class);
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(MainActivity.this, ScanInfoActivity.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent(MainActivity.this, PetrolStationMainActivity.class);
					startActivity(intent3);
					break;
				case 4:
					Intent intent4 = new Intent(MainActivity.this, MyCarInfoActivity.class);
					startActivity(intent4);
					break;
				case 5:
					Intent intent5 = new Intent(MainActivity.this, MusicHomeActivity.class);
					startActivity(intent5);
					break;
				case 6:
					Intent intent6 = new Intent(MainActivity.this, IlleageQuery.class);
					startActivity(intent6);
					break;
				case 7:
					Intent intent7 = new Intent(MainActivity.this, OrderOilActivity.class);
					startActivity(intent7);
					break;
				case 8:
					Intent intent8 = new Intent(MainActivity.this, SettingActivity.class);
					startActivity(intent8);
					break;
				}
			}
		});
	}

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
			if(musicListSize>3)
				natureBinder.startPlay(8, 0);
			else{
				natureBinder.startPlay(0, 0);
			}
		}
	};
	private List<MusicInfo> musicList;

	private void connectToNatureService() {

		Intent intent = new Intent(this, CarMusicService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		System.out.println("�󶨳ɹ�");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (natureBinder != null) {
			unbindService(serviceConnection);
			natureBinder = null;
		}

	}
}
