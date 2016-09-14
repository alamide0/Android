package com.car.loader.first;



import com.car.loader.R;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;
import com.car.loader.util.MessageInfo;
import com.car.loader.util.TimeCountUtil;

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
import android.widget.TextView;
import android.widget.Toast;
/**
 * 填写用户注册时，服务器发送过来的验证码的界面
 * @author Zsy
 *
 */
public class FFillPassCodeActivity extends Activity {
	private TextView tv_text;
	private TextView tv_resend;
	private EditText et_passcode;
	private HeaderView h_header;
	
	private String phone,passcode;
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
				Toast.makeText(FFillPassCodeActivity.this, msg.obj.toString(), 1).show();
				break;
			case GETPCODEINFO:
				String str = msg.obj.toString();
				if(!TextUtils.isEmpty(str))
					Toast.makeText(FFillPassCodeActivity.this, MessageInfo.getMessageInfo(FFillPassCodeActivity.this, str), 1).show();
				break;
			case REGISTERINFO:
				String str2 = msg.obj.toString();
				Toast.makeText(FFillPassCodeActivity.this, MessageInfo.getMessageInfo(FFillPassCodeActivity.this, str2), 1).show();
				if("1016".equals(str2)){
					Intent intent = new Intent(FFillPassCodeActivity.this, FPersonInfoActivity.class);
					intent.putExtra("phone", phone);
					intent.putExtra("passcode", passcode);
					startActivity(intent);
				}
				break;
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_fillpwdcode);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.setTxt("填写验证码");
		tv_text = (TextView) findViewById(R.id.tv_text);
		tv_resend = (TextView) findViewById(R.id.tv_resend);
		et_passcode = (EditText) findViewById(R.id.et_passcode);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FFillPassCodeActivity.this.finish();
			}
		});
		phone = getIntent().getStringExtra("phone");
		//getPassCode();
		tv_text.setText("我们已经给你的手机号 +86-"+phone+"发送了短信验证码。");
		TimeCountUtil t = new TimeCountUtil(this, 60000, 1000, tv_resend);
		t.start();
	}
	
	public void getPassCode(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					String str = TalkWithInternet.toGetPCodeForForget(phone);
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
	
	public void resend(View view){
		Toast.makeText(this, "发送成功", 0).show();
		TimeCountUtil t = new TimeCountUtil(this, 60000, 1000, tv_resend);
		t.start();
	}
	
	public void next(View view){
		passcode = et_passcode.getText().toString().trim();
		if (TextUtils.isEmpty(passcode)) {
			Toast.makeText(this, "验证码不能为空", 1).show();
			return;
		}
		if(passcode.length()>10){
			Toast.makeText(this, "验证码错误", 1).show();
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					String str = TalkWithInternet.toCheckPCodeForForget(phone, passcode);
					msg.what = REGISTERINFO;
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
