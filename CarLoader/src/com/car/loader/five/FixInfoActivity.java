package com.car.loader.five;

import com.car.loader.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FixInfoActivity extends Activity {
	private TextView tv_fix;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fix);
		tv_fix = (TextView) findViewById(R.id.tv_fix);
		tv_fix.setText(getIntent().getExtras().getString("info"));
		System.out.println("=="+getIntent().getExtras().getString("info"));
	}
}
