package com.car.loader.first;

import com.car.loader.R;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;
import com.car.loader.util.MessageInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 用户注册
 * @author Zsy
 *
 */
public class RegisterActivity extends Activity {
	private EditText et_phone;
	private HeaderView h_header;
	private String phone;
	
	protected static final int GETPCODEINFO = 0;
	protected static final int NETWORKERROR = 1;
	protected static final int REGISTERINFO = 2;
	
	private InputMethodManager manager;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NETWORKERROR:
				Toast.makeText(RegisterActivity.this, msg.obj.toString(), 1).show();
				break;
			case GETPCODEINFO:
				String str = msg.obj.toString();
				if(!TextUtils.isEmpty(str))
					Toast.makeText(RegisterActivity.this, MessageInfo.getMessageInfo(RegisterActivity.this, str), 1).show();
				//该号码已被注册
				if("1002".equals(str)){
					return;
				}
				Intent intent = new Intent(RegisterActivity.this,FillPassCodeActivity.class);
				intent.putExtra("phone", phone);
				startActivity(intent);
				break;
			
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_register);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		et_phone = (EditText) findViewById(R.id.et_phone);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.setTxt("获取验证码");
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RegisterActivity.this.finish();
			}
		});
	}
	
	public void getPassCode(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					String str = TalkWithInternet.toGetPassCode(phone);
					msg.what = GETPCODEINFO;
					msg.obj = str;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = NETWORKERROR;
					msg.obj = "亲,网络错误了^~^";
				} finally {
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	public void next(View view){
		phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "不能为空", 0).show();
			return;
		}

		if( !phone.matches("^1[345678]\\d{9}$")){
			Toast.makeText(this, "手机号码格式不正确", 0).show();
			return;
		}
		getPassCode();
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
