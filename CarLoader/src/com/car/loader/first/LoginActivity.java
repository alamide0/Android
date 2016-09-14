package com.car.loader.first;

import java.util.UUID;

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
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 用户登录
 * @author Zsy
 *
 */
public class LoginActivity extends Activity {
	protected static final int LOGININFO = 0;
	protected static final int NETWORKERROR = 1;
	protected static final int UPDATEINFO = 2;
	private EditText et_phone;
	private EditText et_pwd;
	private TextView tv_forget;
	private HeaderView u_header;
	private ImageView iv_back;

	private String realPhone;
	private String pwd;
	private SharedPreferences sp;
	private String nickname;

	private InputMethodManager manager;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGININFO:
				String str = msg.obj.toString();
				Toast.makeText(LoginActivity.this, MessageInfo.getMessageInfo(LoginActivity.this, str), 1).show();
				// 登陆成功
				if ("1005".equals(str)) {
					Editor editor = sp.edit();
					editor.putBoolean("isLogin", true);
					editor.putString("phone", realPhone);
					editor.putString("nickname", nickname);
					editor.putString("onlycode", onlycode);
					editor.putString("password", MD5Utils.md5(pwd));
					editor.commit();
					Intent i = new Intent();
					i.setAction("com.car.loader.login");//发送登陆广播
					i.addCategory("android.intent.category.DEFAULT");
					sendBroadcast(i);
					// Intent service = new Intent(LoginActivity.this,
					// MaintainCarInfoService.class);
					// startService(service);
					// System.out.println("服务开启啦");
					Intent intent = new Intent(LoginActivity.this, BeLoginActivity.class);
					startActivity(intent);
					LoginActivity.this.finish();

				}
				break;
			case NETWORKERROR:
				Toast.makeText(LoginActivity.this, msg.obj.toString(), 1).show();
				break;

			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_login);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		et_phone = (EditText) findViewById(R.id.et_phone);
		et_pwd = (EditText) findViewById(R.id.et_pwd);

		tv_forget = (TextView) findViewById(R.id.tv_forget);
		tv_forget.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_forget.getPaint().setAntiAlias(true);
		u_header = (HeaderView) findViewById(R.id.h_header);
		u_header.setTxt("登录");
		iv_back = u_header.getImageView();
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginActivity.this.finish();
			}
		});
	}

	public void register(View view) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	public void forgetpwd(View view) {
		Intent intent = new Intent(this, ForgetPwdActivity.class);
		startActivity(intent);
	}

	private String onlycode;

	public void login(View view) {
		pwd = et_pwd.getText().toString().trim();
		realPhone = et_phone.getText().toString().trim();

		if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(realPhone)) {
			Toast.makeText(LoginActivity.this, "不能为空", 0).show();
			return;
		}

		if (!realPhone.matches("^1[345678]\\d{9}$")) {
			Toast.makeText(LoginActivity.this, "手机号码格式不正确", 0).show();
			return;
		}
		Editor editor = sp.edit();
		editor.putString("phone", realPhone);
		
		editor.putString("password",MD5Utils.md5(pwd));

		if(TextUtils.isEmpty(sp.getString("onlycode", ""))){
			onlycode = UUID.randomUUID().toString();
			editor.putString("onlycode", onlycode);
		}else{
			onlycode = sp.getString("onlycode", "");
		}
		///onlycode = UUID.randomUUID().toString();
		editor.commit();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					String str = TalkWithInternet.toLogin(realPhone, MD5Utils.md5(pwd));
					nickname = TalkWithInternet.toGetNickName(realPhone);
					if ("1005".equals(str)) {
						
						TalkWithInternet.toStorageLoginOnlyCode(realPhone, onlycode);
					}
					msg.what = LOGININFO;
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
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
}
