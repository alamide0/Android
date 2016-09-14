package com.car.loader.music;

import java.util.List;

import com.car.loader.R;
import com.car.loader.domain.CarMusicLoader;
import com.car.loader.service.CarMusicService;
import com.car.loader.service.CarMusicService.NatureBinder;
import com.car.loader.util.FormatHelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 音乐播放的主界面
 * 此处采用的是绑定的方式开启服务，START开启服务的方式，服务开启之后服务和开启者便没有任何联系，不能调用服务里的方法
 * 而绑定开启服务的方式，开启者可以通过ServiceConnection调用服务里的方法
 * @author Zsy
 *
 */
public class MusicHomeActivity extends Activity {
	/**
	 * 音乐清单列表
	 */
	private List<CarMusicLoader.MusicInfo> musicList;
	private boolean state = false;
	/**
	 * 
	 */
	private int currentPosition;
	private int maxProgress;
	/**
	 * 当前播放的是第几首音乐
	 */
	private int currentMusic;

	private ListView lv_list;
	private TextView tv_title, tv_playing, tv_playing_drution, tv_drution;
	private ImageView iv_play, iv_mode, iv_music_bg;
	/**
	 * 音乐播放的进度条
	 */
	private SeekBar pbDuration;
	private NatureBinder natureBinder;
	/**
	 * 与后台服务交互的广播接收者
	 */
	private ProgressReceiver receiver;
	/**
	 * 当前播放的播放模式，随机 顺序循环 单曲播放
	 */
	private int currentMode = 3;
	/**
	 * 和后台服务连接的接口
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			natureBinder = (NatureBinder) service;
			if (natureBinder.isPlaying()) {
				iv_play.setImageResource(R.drawable.pause);
			} else {
				iv_play.setImageResource(R.drawable.play);
			}

			currentMode = natureBinder.getCurrentMode();
			switch (currentMode) {
			case 0:
				iv_mode.setImageResource(R.drawable.one);
				break;
			case 1:
				iv_mode.setImageResource(R.drawable.loop);
				break;
			case 2:
				iv_mode.setImageResource(R.drawable.random);
				break;
			default:
				break;
			}
			natureBinder.notifyActivity();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_detail);
		lv_list = (ListView) findViewById(R.id.lv_list);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_playing = (TextView) findViewById(R.id.tv_playing);
		iv_play = (ImageView) findViewById(R.id.iv_play);
		iv_mode = (ImageView) findViewById(R.id.iv_mode);
		iv_music_bg = (ImageView) findViewById(R.id.iv_music_bg);

		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MusicHomeActivity.this.finish();
			}
		});

		lv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				natureBinder.startPlay(position, 0);
			}
		});

		pbDuration = (SeekBar) findViewById(R.id.pbDuration);
		tv_playing_drution = (TextView) findViewById(R.id.tv_playing_drution);
		tv_drution = (TextView) findViewById(R.id.tv_duration);
		CarMusicLoader loader = CarMusicLoader.instance(getContentResolver());
		musicList = loader.getMusicList();
		lv_list.setAdapter(new MyAdapter());
		musicList = CarMusicLoader.instance(getContentResolver()).getMusicList();
		if (musicList != null && musicList.size() > 0) {
			connectToService();
			registerReceiver();
		} else {
			Toast.makeText(this, "本地无音乐", 0).show();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (receiver != null)
			unregisterReceiver(receiver);
		if (isConnect)
			unbindService(serviceConnection);
		receiver = null;
		serviceConnection = null;
	}
	/**
	 * 注册广播接收者
	 */
	public void registerReceiver() {
		receiver = new ProgressReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CarMusicService.ACTION_UPDATE_CURRENT_MUSIC);
		filter.addAction(CarMusicService.ACTION_UPDATE_DURATION);
		filter.addAction(CarMusicService.ACTION_UPDATE_PROGRESS);
		registerReceiver(receiver, filter);
	}

	private boolean isConnect = false;
	/**
	 * 绑定服务
	 */
	public void connectToService() {
		Intent intent = new Intent(this, CarMusicService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		isConnect = true;
	}

	public void click(View view) {

		switch (view.getId()) {
		case R.id.iv_mode:
			natureBinder.changeMode();
			currentMode = natureBinder.getCurrentMode();
			switch (currentMode) {
			case 0:
				iv_mode.setImageResource(R.drawable.one);
				break;
			case 1:
				iv_mode.setImageResource(R.drawable.loop);
				break;
			case 2:
				iv_mode.setImageResource(R.drawable.random);
				break;
			default:
				break;
			}
			break;
		case R.id.iv_pre:
			natureBinder.toPrevious();
			break;
		case R.id.iv_next:
			natureBinder.toNext();
			break;
		case R.id.iv_play:
			if (natureBinder.isPlaying()) {
				natureBinder.stopPlay();
				iv_play.setImageResource(R.drawable.play);
			} else {
				natureBinder.startPlay(currentMusic, currentPosition);
				iv_play.setImageResource(R.drawable.pause);
			}
			break;
		case R.id.iv_list:
			if (!state) {
				iv_music_bg.setVisibility(View.INVISIBLE);
				lv_list.setVisibility(View.VISIBLE);

			} else {
				iv_music_bg.setVisibility(View.VISIBLE);
				lv_list.setVisibility(View.INVISIBLE);
			}
			state = !state;
			break;

		}
	}

	class ProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (CarMusicService.ACTION_UPDATE_PROGRESS.equals(action)) {
				int progress = intent.getIntExtra(CarMusicService.ACTION_UPDATE_PROGRESS, 0);
				if (progress > 0) {
					currentPosition = progress;
					pbDuration.setProgress(progress / 1000);
					tv_playing_drution.setText(FormatHelper.formatDuration(progress));
				}

			} else if (CarMusicService.ACTION_UPDATE_DURATION.equals(action)) {
				maxProgress = intent.getIntExtra(CarMusicService.ACTION_UPDATE_DURATION, 0);
				if (maxProgress > 0 && maxProgress < 60*60*1000) {
					pbDuration.setMax(maxProgress / 1000);
					tv_drution.setText(FormatHelper.formatDuration(maxProgress));
				}

			} else if (CarMusicService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)) {
				currentMusic = intent.getIntExtra(CarMusicService.ACTION_UPDATE_CURRENT_MUSIC, 0);
				tv_title.setText(FormatHelper.formatTitle(musicList.get(currentMusic).getTitle(), 15));
				tv_playing.setText((currentMusic+1) + "/" + musicList.size());
			}
		}

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return musicList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;

			if (convertView == null) {
				view = View.inflate(MusicHomeActivity.this, R.layout.item_music, null);
				holder = new ViewHolder();
				holder.iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
				holder.tv_artist = (TextView) view.findViewById(R.id.tv_artist);
				holder.tv_drution = (TextView) view.findViewById(R.id.tv_duration);
				holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			holder.iv_photo.setImageResource(R.drawable.audio);
			holder.tv_artist.setText(musicList.get(position).getArtist());
			holder.tv_drution.setText(FormatHelper.formatDuration(musicList.get(position).getDuration()));
			holder.tv_title.setText(musicList.get(position).getTitle());
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

	static class ViewHolder {
		private ImageView iv_photo;
		private TextView tv_drution;
		private TextView tv_title;
		private TextView tv_artist;
	}
}
