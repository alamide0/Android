package com.car.loader.map;

import com.car.loader.R;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class NowPlaceActivity extends Activity {
	private TextView tv_now_place;
	private HeaderView h_header;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_now_place);
		tv_now_place = (TextView) findViewById(R.id.tv_now_place);
		h_header = (HeaderView) findViewById(R.id.h_header);
		
		String place = getIntent().getStringExtra("place");
		tv_now_place.setText(place);
		
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NowPlaceActivity.this.finish();
			}
		});
	}
}
