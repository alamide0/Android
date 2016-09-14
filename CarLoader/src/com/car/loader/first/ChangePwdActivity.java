package com.car.loader.first;

import com.car.loader.R;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;
import com.car.loader.util.MD5Utils;
import com.car.loader.util.MessageInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
 * 修改用户密码的界面
 * @author Zsy
 *
 */
public class ChangePwdActivity extends Activity {
	private HeaderView h_header;
	private EditText et_password;
	private EditText et_password2;
	private String phone;
	private SharedPreferences sp;
	private InputMethodManager manager;
	
	protected static final int SUBINFO = 0;
	protected static final int NETWORKERROR = 1;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUBINFO:
				String str = msg.obj.toString();
				Toast.makeText(ChangePwdActivity.this, MessageInfo.getMessageInfo(ChangePwdActivity.this, str), 1).show();
				//修改成功
				if("1008".equals(str)){
					Editor editor = sp.edit();
					editor.putBoolean("isLogin", false);
					editor.commit();
					Intent intent = new Intent(ChangePwdActivity.this,BeLoginActivity.class);
					startActivity(intent);
					ChangePwdActivity.this.finish();
				}
				break;
			case NETWORKERROR:
				Toast.makeText(ChangePwdActivity.this, msg.obj.toString(), 1).show();
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_change_pwd);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		h_header = (HeaderView) findViewById(R.id.h_header);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		phone = sp.getString("phone", "");
		
		h_header.setTxt("修改密码");
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangePwdActivity.this.finish();
			}
		});
		et_password = (EditText) findViewById(R.id.et_password);
		et_password2 = (EditText) findViewById(R.id.et_password2);
	}
	
	public void complete(View view){
		final String password = et_password.getText().toString().trim();
		String password2 = et_password2.getText().toString().trim();
		
		if(TextUtils.isEmpty(password)||TextUtils.isEmpty(password2)){
			Toast.makeText(this, "不能为空", 1).show();
			return;
		}
		if(password.length()>20){
			Toast.makeText(this, "密码太长", 1).show();
			return;
		}
		if (!password.equals(password2)) {
			Toast.makeText(this, "亲,两次密码不一致^_^", 1).show();
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					String str = TalkWithInternet.toUpdatePwd(phone,MD5Utils.md5(password), "","");
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
