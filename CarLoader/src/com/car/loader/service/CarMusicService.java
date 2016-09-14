package com.car.loader.service;

import java.util.List;

import com.car.loader.domain.CarMusicLoader;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
/**
 * �������ֵĺ�̨������Ҫ�Ĺ����в�����һ������һ������ͣ�����ŵ�
 * @author Zsy
 *
 */
public class CarMusicService extends Service{

	
	
	//public static final String MUSICS = "com.example.nature.MUSIC_LIST";
	
	//public static final String NATURE_SERVICE = "com.example.nature.NatureService";	
	/**
	 * ϵͳ���ֲ�����
	 */
	private MediaPlayer mediaPlayer;
	/**
	 * ��ǰ���ֲ������Ƿ��ڲ���
	 */
	private boolean isPlaying = false;
	/**
	 * �������ݿ�������
	 */
	private List<CarMusicLoader.MusicInfo> musicList;
	/**
	 * Ϊ����ṩһ��������ͨ�����൱��һ��������
	 */
	private Binder natureBinder = new NatureBinder();
	/**
	 * ��ǰ���ŵ����ֵ�λ��
	 */
	private int currentMusic;
	/**
	 * ��ǰ���ֲ��ŵĽ���
	 */
	private int currentPosition;
	/**
	 * ��ǰ���ֵ�ʱ��
	 */
	private int currentMax;
	private int CPostion;
	
	private static final int updateProgress = 1;
	private static final int updateCurrentMusic = 2;
	private static final int updateDuration = 3;
	
	public static final String ACTION_UPDATE_PROGRESS = "UPDATE_PROGRESS";//���µ�ǰ���Ž���
	public static final String ACTION_UPDATE_DURATION = "UPDATE_DURATION";//���µ�ǰ�������ֵĳ���
	public static final String ACTION_UPDATE_CURRENT_MUSIC = "UPDATE_CURRENT_MUSIC";//���µ�ǰ���ŵ��ǵڼ�������
	
	private int currentMode = 1; //Ĭ�ϵĲ���ģʽ
	
	public static final String[] MODE_DESC = {"Single Loop", "List Loop", "Random", "Sequence"};
	
	public static final int MODE_ONE_LOOP = 0;
	public static final int MODE_ALL_LOOP = 1;
	public static final int MODE_RANDOM = 2; 
	/**
	 * �첽��Ϣ�������	
	 */
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg){
			switch(msg.what){
			case updateProgress:				
				toUpdateProgress();
				break;
			case updateDuration:				
				toUpdateDuration();
				break;
			case updateCurrentMusic:
				toUpdateCurrentMusic();
				break;
			}
		}
	};
	/**
	 * ���µ�ǰ���Ž���
	 */
	private void toUpdateProgress(){
		if(mediaPlayer != null && isPlaying){					
			int progress = mediaPlayer.getCurrentPosition();
			CPostion = progress;
			System.out.println("progress="+progress);
			//System.out.println(progress);
			Intent intent = new Intent();
			intent.setAction(ACTION_UPDATE_PROGRESS);
			intent.putExtra(ACTION_UPDATE_PROGRESS,progress);
			sendBroadcast(intent);
			handler.sendEmptyMessageDelayed(updateProgress, 1000);					
		}
	}
	/**
	 * ���µ�ǰ�������ֵ�ʱ��
	 */
	private void toUpdateDuration(){
		if(mediaPlayer != null){					
			int duration = mediaPlayer.getDuration();
			currentMax = duration;
			Intent intent = new Intent();
			intent.setAction(ACTION_UPDATE_DURATION);
			intent.putExtra(ACTION_UPDATE_DURATION,duration);
			sendBroadcast(intent);									
		}
	}
	/**
	 * ���µ�ǰ����
	 */
	private void toUpdateCurrentMusic(){
		Intent intent = new Intent();
		intent.setAction(ACTION_UPDATE_CURRENT_MUSIC);
		intent.putExtra(ACTION_UPDATE_CURRENT_MUSIC,currentMusic);
		sendBroadcast(intent);				
	}
	
	public void onCreate(){
		initMediaPlayer();
		musicList = CarMusicLoader.instance(getContentResolver()).getMusicList();
		//Log.v(TAG, "OnCreate");   
		super.onCreate();
	}	
	
	public void onDestroy(){
		if(mediaPlayer != null){
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}	
	
	/**
	 * ��ʼ�����ֲ�����
	 */
	private void initMediaPlayer(){
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);		
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {//ע�������				
			@Override
			public void onPrepared(MediaPlayer mp) {				
				mediaPlayer.start();
				mediaPlayer.seekTo(currentPosition);
				handler.sendEmptyMessage(updateDuration);
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {//�������ʱ			
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(isPlaying){
					switch (currentMode) {
					case MODE_ONE_LOOP:
						mediaPlayer.start();
						break;					
					case MODE_ALL_LOOP:
						play((currentMusic + 1) % musicList.size(), 0);
						break;
					case MODE_RANDOM:
						play(getRandomPosition(), 0);
						break;
					default:
						break;
					}
				}
			}
		});
	}
	/**
	 * ���õ�ǰ���ŵ�����
	 * @param pCurrentMusic
	 */
	private void setCurrentMusic(int pCurrentMusic){
		currentMusic = pCurrentMusic;
		handler.sendEmptyMessage(updateCurrentMusic);
	}
	
	private int getRandomPosition(){
		int random = (int)(Math.random() * (musicList.size() - 1));
		return random;
	}
	/**
	 * ����
	 * @param currentMusic
	 * @param pCurrentPosition
	 */
	private void play(int currentMusic, int pCurrentPosition) {
		currentPosition = pCurrentPosition;
		setCurrentMusic(currentMusic);
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(musicList.get(currentMusic).getUrl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mediaPlayer.prepareAsync();
		handler.sendEmptyMessage(updateProgress);

		isPlaying = true;
	}
	/**
	 * ��ͣ
	 */
	private void stop(){
		mediaPlayer.stop();
		isPlaying = false;
	}
	/**
	 * ��һ��
	 */
	private void playNext(){
		switch(currentMode){
		case MODE_ONE_LOOP:
			play(currentMusic, 0);
			break;
		case MODE_ALL_LOOP:
			if(currentMusic + 1 == musicList.size()){
				play(0,0);
			}else{
				play(currentMusic + 1, 0);
			}
			break;
//		case MODE_SEQUENCE:
//			if(currentMusic + 1 == musicList.size()){
//				Toast.makeText(this, "No more song.", Toast.LENGTH_SHORT).show();
//			}else{
//				play(currentMusic + 1, 0);
//			}
//			break;
		case MODE_RANDOM:
			play(getRandomPosition(), 0);
			break;
		}
	}
	/**
	 * ��һ��
	 */
	private void playPrevious(){		
		switch(currentMode){
		case MODE_ONE_LOOP:
			play(currentMusic, 0);
			break;
		case MODE_ALL_LOOP:
			if(currentMusic - 1 < 0){
				play(musicList.size() - 1, 0);
			}else{
				play(currentMusic - 1, 0);
			}
			break;
//		case MODE_SEQUENCE:
//			if(currentMusic - 1 < 0){
//				Toast.makeText(this, "No previous song.", Toast.LENGTH_SHORT).show();
//			}else{
//				play(currentMusic - 1, 0);
//			}
//			break;
		case MODE_RANDOM:
			play(getRandomPosition(), 0);
			break;
		}
	}
	

	@Override	
	public IBinder onBind(Intent intent) {		
		return natureBinder;
	}	
	
	public class NatureBinder extends Binder{
		
		public void startPlay(int currentMusic, int currentPosition){
			play(currentMusic,currentPosition);
		}
		
		public void stopPlay(){
			stop();
		}
		
		public void toNext(){
			playNext();
		}
		
		public void toPrevious(){
			playPrevious();
		}
		
		/**
		 * MODE_ONE_LOOP = 1;
		 * MODE_ALL_LOOP = 2;
		 * MODE_RANDOM = 3;
		 * 
		 */		
		public void changeMode(){			
			currentMode = (currentMode + 1) % 3;
//			Log.v(TAG, "[NatureBinder] changeMode : " + currentMode);
//			Toast.makeText(CarMusicService.this, MODE_DESC[currentMode], Toast.LENGTH_SHORT).show();
		}
		
		/**
		 * return the current mode
		 * MODE_ONE_LOOP = 1;
		 * MODE_ALL_LOOP = 2;
		 * MODE_RANDOM = 3;
		 * MODE_SEQUENCE = 4; 
		 * @return
		 */
		public int getCurrentMode(){
			return currentMode; 
		}
		
		public int getCurrentPostion(){
			return CPostion;
		}
		
		public int getCurrentMusic(){
			return currentMusic;
		}
		/**
		 * ��ȡ��ǰ�������ֵ�ʱ��
		 * @return
		 */
		public int getCurrentMax(){
			return currentMax;
		}
		/**
		 * The service is playing the music
		 * @return
		 */
		public boolean isPlaying(){
			return isPlaying;
		}
		
		/**
		 * ��������
		 */
		public void notifyActivity(){
			toUpdateCurrentMusic();
			toUpdateDuration();	
			toUpdateProgress();
		}
		
		/**
		 * Ԥ���ã����������ı䲥�ŵĽ���
		 * 
		 */
		public void changeProgress(int progress){
			if(mediaPlayer != null){
				currentPosition = progress * 1000;
				if(isPlaying){
					mediaPlayer.seekTo(currentPosition);
				}else{
					play(currentMusic, currentPosition);
				}
			}
		}
	}

}