package com.car.loader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.car.loader.internet.TalkWithInternet;
import com.car.loader.util.StreamTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

/**
 * 进入APP时的主界面时用作显示用，主要的功能有检查是否有更新以及初始化TalkWithInternet.setContext(this);
 * 把上下文传入网络对话的类中，方便 后面操作
 * @author Zsy
 *
 */
public class SplashActivity extends Activity {

	protected static final int UPDATE_DIALOG = 0;
	protected static final int URLERROR = 1;
	protected static final int NETWORKERROR = 2;
	protected static final int JSONERROR = 3;
	protected static final int ENTERHOME = 4;
	private TextView tv_version;
	private String descrption;
	private String apkUrl;
	private SharedPreferences sp;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_DIALOG:
				showUpdateDialog();
				break;

			case URLERROR:
				enterHomeActivity();
				Toast.makeText(getApplicationContext(), "Url请求错误，请检查网络地址是否正确", 0).show();
				break;
			case NETWORKERROR:
				enterHomeActivity();
				Toast.makeText(getApplicationContext(), "网络错误，请检查网络连接", 0).show();
				break;
			case JSONERROR:
				enterHomeActivity();
				Toast.makeText(getApplicationContext(), "Json解析错误", 0).show();
				break;
			case ENTERHOME:
				enterHomeActivity();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		TalkWithInternet.setContext(this);
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("版本号"+getVersionName());
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		boolean isUpdate = sp.getBoolean("auto_update", true);
		
		if(isUpdate){
			checkUpdate();
		}else{
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					enterHomeActivity();
				}
			}, 3000);
		}
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(3000);
		findViewById(R.id.rv_splash_root).startAnimation(aa);
	}

	

	private void checkUpdate() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				try {
					// 与new 比较减少了内存的开销，而且节约时间
					URL url = new URL(getString(R.string.url));
					HttpURLConnection con = (HttpURLConnection) url.openConnection();

					con.setConnectTimeout(4000);
					con.setRequestMethod("GET");
					int responseCode = con.getResponseCode();
					Log.i("SplashActivity", responseCode + "");

					if (responseCode == 200) {
						String string = StreamTools.readFromStream(con.getInputStream());
						JSONObject job = new JSONObject(string);
						String versionName = job.getString("version");
						descrption = job.getString("descrption");
						apkUrl = job.getString("apkUrl");
						if (!getVersionName().equals(versionName)) {
							msg.what = UPDATE_DIALOG;
						} else {
							msg.what = ENTERHOME;
						}
						Log.i("SplashActivity", versionName + "," + descrption + "," + apkUrl);
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block

					msg.what = URLERROR;
					e.printStackTrace();
				} catch (IOException e) {
					Log.i("123", "2666");
					msg.what = NETWORKERROR;
					e.printStackTrace();
				} catch (JSONException e) {

					msg.what = JSONERROR;
					e.printStackTrace();
				} finally {
					handler.sendMessage(msg);
				}
			}

		}).start();

	}

	protected void enterHomeActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		this.finish();
	}

	protected void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		builder.setMessage(descrption);
		builder.setTitle("有新升级！");
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				enterHomeActivity();
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("立即升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				FinalHttp finalHttp = new FinalHttp();
				finalHttp.download(apkUrl, Environment.getExternalStorageDirectory().getAbsolutePath()+"/defence2.apk", new AjaxCallBack<File>() {

					@Override
					public void onFailure(Throwable t, int errorNo, String strMsg) {
						// TODO Auto-generated method stub
						t.printStackTrace();
						Toast.makeText(SplashActivity.this, "下载失败", 0).show();
						enterHomeActivity();
						super.onFailure(t, errorNo, strMsg);
					}

					@Override
					public void onLoading(long count, long current) {
						// TODO Auto-generated method stub
						
						super.onLoading(count, current);
						enterHomeActivity();
						
					}

					@Override
					public void onSuccess(File t) {
						// TODO Auto-generated method stub
						super.onSuccess(t);
						installApk(t);
					}

					private void installApk(File file) {
						// TODO Auto-generated method stub
						 Intent intent = new Intent();
						  intent.setAction("android.intent.action.VIEW");
						  intent.addCategory("android.intent.category.DEFAULT");
						  intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
						  
						  startActivity(intent);
					}
					
				});
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				enterHomeActivity();
			}
		});
		builder.show();
	}

	public String getVersionName() {
		try {
			PackageManager pm = getPackageManager();
			PackageInfo info = pm.getPackageInfo("com.car.loader", 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
