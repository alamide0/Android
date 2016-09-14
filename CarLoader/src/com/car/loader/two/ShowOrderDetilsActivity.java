package com.car.loader.two;

import com.car.loader.R;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ShowOrderDetilsActivity extends Activity {
	private TextView tv_carnumber;
	private TextView tv_name;
	private TextView tv_time;
	private TextView tv_station;
	private TextView tv_oil_type;
	private TextView tv_money;
	private String info;
	private HeaderView h_header;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oilorder_detil_info);
		
		
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowOrderDetilsActivity.this.finish();
			}
		});
		
		tv_carnumber = (TextView) findViewById(R.id.tv_carnumber);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_station = (TextView) findViewById(R.id.tv_station);
		tv_oil_type = (TextView) findViewById(R.id.tv_oil_type);
		tv_money = (TextView) findViewById(R.id.tv_money);
		info = getIntent().getStringExtra("info");
		String[] strs = info.split(",");
		tv_carnumber.setText(strs[0]);
		tv_name.setText(strs[1]);
		tv_time.setText(strs[2]);
		tv_station.setText(strs[3]);
		tv_oil_type.setText(strs[4]);
		tv_money.setText(strs[5]+"Ԫ");
	}
	
	public void query(View view){
		Intent intent = new Intent(this,ShowCodeInfoActivity.class);
		intent.putExtra("info", info);
		startActivity(intent);
	}
}
