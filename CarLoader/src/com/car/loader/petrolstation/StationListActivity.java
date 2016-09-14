package com.car.loader.petrolstation;

import java.util.ArrayList;

import com.car.loader.R;
import com.car.loader.domain.Station;
import com.car.loader.petrolstation.adapter.StationListAdapter;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;



public class StationListActivity extends Activity {

	private Context mContext;
	private ListView lv_station;
	private ImageView iv_back;
	private ArrayList<Station>stationList;
	private HeaderView h_header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_petrol_list);
		h_header = (HeaderView) findViewById(R.id.h_header);
		
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StationListActivity.this.finish();
			}
		});
		mContext = this;
		initView();
	}

	private void initView() {
		Intent intent=getIntent();
		Bundle extras=intent.getExtras();
		stationList=extras.getParcelableArrayList("list");
		lv_station=(ListView)findViewById(R.id.lv_station);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setVisibility(View.VISIBLE);
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		lv_station = (ListView) findViewById(R.id.lv_station);
		setValues(stationList);

	}

	/**
	 * 
	 * 这个方法用于设置数据
	 * **/
	private void setValues(ArrayList<Station> list) {
		// TODO Auto-generated method stub
		StationListAdapter stationadapter=new StationListAdapter(mContext, list);
		lv_station.setAdapter(stationadapter);
	    lv_station.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,StationInfoActivity.class);
				intent.putExtra("s", stationList.get(position));
				//System.out.println(stationList.get(position).getLat()+"==="+stationList.get(position).getLon());
				intent.putExtra("locLat", getIntent().getDoubleExtra("locLat", 0));
				intent.putExtra("locLon", getIntent().getDoubleExtra("locLon", 0));
				startActivity(intent);
			}
		});
	}

}
