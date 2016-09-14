package com.car.loader.first;

import com.car.loader.R;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 这是处理登陆模块的‘预处理’界面。如果用户已经登陆则显示用户的基本信息
 * 如果用户不是已登录状态，则显示的是用户登录的界面
 * @author Zsy
 *
 */
public class BeLoginActivity extends Activity {
	private HeaderView h_header ;
	private ImageView iv_change_nickname;
	private ImageView iv_change_pwd;
	private TextView tv_nickname;
	private TextView tv_phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean b = sp.getBoolean("isLogin", false);
		if(!b){
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			this.finish();
			return;
		}
		setContentView(R.layout.first_detilinfo);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.setTxt("个人信息");
		iv_change_nickname = (ImageView) findViewById(R.id.iv_change_nickname);
		iv_change_pwd = (ImageView) findViewById(R.id.iv_change_pwd);
		iv_change_nickname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BeLoginActivity.this,ChangeNickNameActivity.class);
				startActivity(intent);
			}
		});
		
		iv_change_pwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BeLoginActivity.this,ChangePwdActivity.class);
				startActivity(intent);
			}
		});
		ImageView iv_back = h_header.getImageView();
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BeLoginActivity.this.finish();
			}
		});
	}
	
//	public void changeNickName(View view){
//		Intent intent = new Intent(BeLoginActivity.this,ChangeNickNameActivity.class);
//		startActivity(intent);
//	}
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		String nickname = sp.getString("nickname", "");
		String phone = sp.getString("phone", "");
		boolean b = sp.getBoolean("isLogin", true);
		//System.out.println("b="+b);
		if(!b){
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			this.finish();
			return;
		}
		tv_nickname.setText(nickname);
		tv_phone.setText(phone);
	}
	
	public void logout(View view){
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		Intent intent = new Intent(this,LoginActivity.class);
		startActivity(intent);
		Intent i = new Intent();
		i.setAction("com.car.loader.logout");//发送注销广播
		i.addCategory("android.intent.category.DEFAULT");
		sendBroadcast(i);
//		Editor editor = sp.edit();
//		editor.putBoolean("isLogin", false);
//		editor.putString("phone", "");
//		editor.commit();
		this.finish();
	}
}
