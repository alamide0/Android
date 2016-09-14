package com.car.loader.petrolstation;

import java.util.ArrayList;

import com.car.loader.R;
import com.car.loader.domain.Station;
import com.car.loader.petrolstation.adapter.PriceListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;



public class StationInfoActivity extends Activity implements OnClickListener {

	private Context mContext;
	private TextView tv_title_right, tv_name, tv_distance, tv_area, tv_addr;
	private ImageView iv_back;
	private ScrollView sv;
	private ListView lv_gast_price, lv_price;
	private Station mStation;
	private double lat,lon;
	private ArrayList station_price,gast_price;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_petrol_info);
		mContext = this;
		initView();
	}

	private void initView() {
		//获得从前方activity的数据
		Intent intent=getIntent();
		Bundle extras=intent.getExtras();
		mStation=extras.getParcelable("s");
		lat=extras.getDouble("locLat", 0);
		lon=extras.getDouble("locLon",0);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_addr = (TextView) findViewById(R.id.tv_addr);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_title_right = (TextView) findViewById(R.id.tv_title_button);
		tv_title_right.setText("路线>");
		tv_title_right.setOnClickListener(this);
		tv_title_right.setVisibility(View.VISIBLE);
		//表示本站油价
		this.lv_gast_price=(ListView)findViewById(R.id.lv_gast_price);
		//表示省空油价
		this.lv_price=(ListView)findViewById(R.id.lv_price);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setVisibility(View.VISIBLE);
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		lv_gast_price = (ListView) findViewById(R.id.lv_gast_price);
		lv_price = (ListView) findViewById(R.id.lv_price);
		sv = (ScrollView) findViewById(R.id.sv);
		setValues(mStation);
	}

	private void setValues(Station s) {
		// TODO Auto-generated method stub
		tv_name.setText(s.getName());
		tv_distance.setText(String.valueOf(s.getDistance())+"m");
		tv_area.setText(s.getArea());
		tv_addr.setText(s.getAddr());
		station_price=s.getGastPriceList();
		gast_price=s.getPriceList();
		PriceListAdapter station_adapter=new PriceListAdapter(mContext, station_price);
		lv_gast_price.setAdapter(station_adapter);
		PriceListAdapter adapter=new PriceListAdapter(mContext, gast_price);
		lv_price.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_title_button:
			//在这个地方来编写一个来计算最优线路的算法
			Intent route=new Intent(mContext,PetrolDriveRoutePlan.class);
			//获取目标加油站的位置
			System.out.println(mStation.getLat()+":"+mStation.getLon());
			System.out.println(lat+":"+lon);
			route.putExtra("lat", mStation.getLat());
			route.putExtra("lon", mStation.getLon());
			//获取自己当前的位置
			route.putExtra("locLat", lat);
			route.putExtra("locLon", lon);
			startActivity(route);
			break;
		default:
			break;
		}
	}

}
