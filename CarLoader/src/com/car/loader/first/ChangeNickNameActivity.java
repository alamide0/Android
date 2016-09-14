package com.car.loader.first;

import com.car.loader.R;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;
import com.car.loader.util.MessageInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 修改用户昵称的界面
 * @author Zsy
 *
 */
public class ChangeNickNameActivity extends Activity {
	private HeaderView h_header;
	private EditText et_nickname;
	protected static final int SUBINFO = 0;
	protected static final int NETWORKERROR = 1;
	private String phone;
	private SharedPreferences sp;
	private InputMethodManager manager;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUBINFO:
				String str = msg.obj.toString();
				Toast.makeText(ChangeNickNameActivity.this, MessageInfo.getMessageInfo(ChangeNickNameActivity.this, str), 1).show();
				//修改成功
				if("1008".equals(str)){
					Intent intent = new Intent(ChangeNickNameActivity.this,BeLoginActivity.class);
					startActivity(intent);
					Editor editor = sp.edit();
					editor.putString("nickname", nickname);
					editor.commit();
					ChangeNickNameActivity.this.finish();
				}
				break;
			case NETWORKERROR:
				Toast.makeText(ChangeNickNameActivity.this, msg.obj.toString(), 1).show();
				break;
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_change_nickname);
		manager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		phone = sp.getString("phone", "");
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.setTxt("修改昵称");
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChangeNickNameActivity.this.finish();
			}
		});
		et_nickname = (EditText) findViewById(R.id.et_nickname);
	}
	private String nickname; 
	public void complete(View view){
		nickname = et_nickname.getText().toString().trim();
		if(TextUtils.isEmpty(nickname)){
			Toast.makeText(this, "不能为空", 1).show();
		}
		
		if(nickname.length()>20){
			Toast.makeText(this, "昵称太长", 1).show();
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					String str = TalkWithInternet.toUpdatePwd(phone,"", nickname,"");
					msg.what = SUBINFO;
					msg.obj = str;
				} catch (Exception e) {
					msg.what = NETWORKERROR;
					msg.obj = "亲,网络错误了^~^";
					e.printStackTrace();
				} finally {
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(getCurrentFocus()!=null&&getCurrentFocus().getWindowToken()!=null){
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
}
