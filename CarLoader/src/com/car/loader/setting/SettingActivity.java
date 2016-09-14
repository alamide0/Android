package com.car.loader.setting;

import com.car.loader.R;
import com.car.loader.ui.HeaderView;
import com.car.loader.ui.SettingItemView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 设置界面，用来设置是否自动播放音乐及是否自动更新
 * @author Zsy
 *
 */
public class SettingActivity extends Activity implements OnClickListener {

	private HeaderView h_header;
	private SettingItemView siv_auto_play, siv_auto_update;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});
		boolean auto_play = sp.getBoolean("auto_play", true);
		boolean auto_update = sp.getBoolean("auto_update", true);
		siv_auto_play = (SettingItemView) findViewById(R.id.siv_auto_play);

		siv_auto_update = (SettingItemView) findViewById(R.id.siv_auto_update);

		siv_auto_play.setTurnOn(auto_play);
		siv_auto_update.setTurnOn(auto_update);
		siv_auto_play.setOnClickListener(this);
		siv_auto_update.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.siv_auto_play:
			if(siv_auto_play.getTurnState()){
				siv_auto_play.setTurnOn(false);
				sp.edit().putBoolean("auto_play", false).commit();
			}else{
				siv_auto_play.setTurnOn(true);
				sp.edit().putBoolean("auto_play", true).commit();
			}
			break;
		case R.id.siv_auto_update:
			if(siv_auto_update.getTurnState()){
				siv_auto_update.setTurnOn(false);
				sp.edit().putBoolean("auto_update", false).commit();
			}else{
				siv_auto_update.setTurnOn(true);
				sp.edit().putBoolean("auto_update", true).commit();
			}
			break;
			
		default:
			break;
		}

	}

}
