package com.car.loader.util;



import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

public class TimeCountUtil extends CountDownTimer {

	private Activity activity;
	private TextView tv;
	public TimeCountUtil(Activity activity,long millisInFuture, long countDownInterval,TextView tv) {
		super(millisInFuture, countDownInterval);
		this.activity = activity;
		this.tv = tv;
		
	}

	@Override
	public void onTick(long millisUntilFinished) {
		tv.setClickable(false);
		tv.setText("重新发送("+millisUntilFinished/1000+")");
		tv.setTextColor(Color.GRAY);
	}

	@Override
	public void onFinish() {
		tv.setTextColor(0xff0a7dd2);
		tv.setText("重新发送");
		tv.setClickable(true);
		
	}

}
