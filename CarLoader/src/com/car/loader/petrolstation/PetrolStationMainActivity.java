package com.car.loader.petrolstation;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.car.loader.R;
import com.car.loader.domain.Petrol;
import com.car.loader.domain.Station;

import com.car.loader.ui.HeaderView;
import com.car.loader.util.StationData;


public class PetrolStationMainActivity extends Activity implements OnClickListener {

	private Context mContext;
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private LocationClient mLocationClient = null;
	private BDLocationListener mListener = new MyLocationListener();

	private ImageView iv_list, iv_loc;
	private Toast mToast;
	private TextView tv_title_right, tv_name, tv_distance, tv_price_a, tv_price_b;
	private LinearLayout ll_summary;
	private Dialog selectDialog, loadingDialog;
	private StationData stationData;
	private BDLocation loc;

	private ArrayList<Station> mList;
	private Station mStation = null;

	private int mDistance = 3000;
	private Marker lastMarker = null;
	
	private ImageView iv_back;
	private TextView tv_title_button;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_petrol_main);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		//tv_title_button =  (TextView) findViewById(R.id.tv_title_button);
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PetrolStationMainActivity.this.finish();
			}
		});
		mContext = this;
		stationData = new StationData(mHandler);
		initView();
		
		
	}

	private void initView() {
		iv_list = (ImageView) findViewById(R.id.iv_list);
		iv_list.setOnClickListener(this);
		iv_loc = (ImageView) findViewById(R.id.iv_loc);
		iv_loc.setOnClickListener(this);
		tv_title_right = (TextView) findViewById(R.id.tv_title_button);
		tv_title_right.setText("3km" + " >");
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setOnClickListener(this);

		ll_summary = (LinearLayout) findViewById(R.id.ll_summary);
		ll_summary.setOnClickListener(this);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_price_a = (TextView) findViewById(R.id.tv_price_a);
		tv_price_b = (TextView) findViewById(R.id.tv_price_b);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.showScaleControl(false);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();
		MapStatus ms = new MapStatus.Builder().overlook(-30).zoom(16).build();
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);

		mBaiduMap.animateMapStatus(u, 1000);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, BitmapDescriptorFactory
				.fromResource(R.drawable.petrol_now)));
		mBaiduMap.setMyLocationEnabled(true);

		mLocationClient = new LocationClient(mContext);
		mLocationClient.registerLocationListener(mListener);

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
															
		option.setCoorType("bd09ll"); 
										
		option.setScanSpan(0);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();

	}

	public void setMarker(ArrayList<Station> list) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.petrol_marker, null);
		final TextView tv = (TextView) view.findViewById(R.id.tv_marker);
		for (int i = 0; i < list.size(); i++) {
			Station s = list.get(i);
			tv.setText((i + 1) + "");
			if (i == 0) {
				tv.setBackgroundResource(R.drawable.petrol_icon_focus_mark);
			} else {
				tv.setBackgroundResource(R.drawable.petrol_icon_mark);
			}
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
			LatLng latLng = new LatLng(s.getLat(), s.getLon());
			Bundle b = new Bundle();
			b.putParcelable("s", list.get(i));
			OverlayOptions oo = new MarkerOptions().position(latLng).icon(bitmap).title((i + 1) + "").extraInfo(b);
			if (i == 0) {
				lastMarker = (Marker) mBaiduMap.addOverlay(oo);
				mStation = s;
				showLayoutInfo((i + 1) + "", mStation);
			} else {
				mBaiduMap.addOverlay(oo);
			}
		}

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				if (lastMarker != null) {
					tv.setText(lastMarker.getTitle());
					tv.setBackgroundResource(R.drawable.petrol_icon_mark);
					BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
					lastMarker.setIcon(bitmap);
				}
				lastMarker = marker;
				String position = marker.getTitle();
				tv.setText(position);
				tv.setBackgroundResource(R.drawable.petrol_icon_focus_mark);
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
				marker.setIcon(bitmap);
				mStation = marker.getExtraInfo().getParcelable("s");
				showLayoutInfo(position, mStation);
				return false;
			}
		});

	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0x01:
				mList = (ArrayList<Station>) msg.obj;
				setMarker(mList);
				loadingDialog.dismiss();
				break;
			case 0x02:
				loadingDialog.dismiss();
				showToast(String.valueOf(msg.obj));
				break;
			default:
				break;
			}

		}

	};

	public void showLayoutInfo(String position, Station s) {
		tv_name.setText(position + "." + s.getName());
		tv_distance.setText(s.getDistance() + ""+"m");
		List<Petrol> list = s.getGastPriceList();

		if (list != null && list.size() > 0) {
			tv_price_a.setText(list.get(0).getType() + " " + list.get(0).getPrice());
			if (list.size() > 1) {
				tv_price_b.setText(list.get(1).getType() + " " + list.get(1).getPrice());
			}
		}
		ll_summary.setVisibility(View.VISIBLE);
	}

	public void searchStation(double lat, double lon, int distance) {
		showLoadingDialog();
		mBaiduMap.clear();
		ll_summary.setVisibility(View.GONE);
		stationData.getStationData(lat, lon, distance,this);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (location == null) {
				return;
			}
			loc = location;
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			searchStation(location.getLatitude(), location.getLongitude(), mDistance);
		}

	}
	
	

	
	public void onDialogClick(View v) {
		switch (v.getId()) {
		case R.id.bt_3km:
			distanceSearch("3km >", 3000);
			break;
		case R.id.bt_5km:
			distanceSearch("5km >", 5000);
			break;
		case R.id.bt_8km:
			distanceSearch("8km >", 8000);
			break;
		case R.id.bt_10km:
			distanceSearch("10km >", 10000);
			break;
		default:
			break;
		}
	}

	
	public void distanceSearch(String text, int distance) {
		mDistance = distance;
		tv_title_right.setText(text);
		searchStation(loc.getLatitude(), loc.getLongitude(), distance);
		selectDialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.iv_list:
			
			Intent listIntent = new Intent(mContext, StationListActivity.class);
			
			listIntent.putParcelableArrayListExtra("list", mList);
			
			listIntent.putExtra("locLat", loc.getLatitude());
			listIntent.putExtra("locLon", loc.getLongitude());
			startActivity(listIntent);
			break;
		case R.id.iv_loc:
			int r = mLocationClient.requestLocation();
			switch (r) {
			case 1:
				showToast("服务没有启动。");
				break;
			case 2:
				showToast("没有监听函数。");
				break;
			case 6:
				showToast("请求间隔过短。");
				break;

			default:
				break;
			}

			break;
		case R.id.tv_title_button:
			showSelectDialog();
			break;
		case R.id.ll_summary:
			Intent infoIntent = new Intent(mContext, StationInfoActivity.class);
			infoIntent.putExtra("s", mStation);
			
			infoIntent.putExtra("locLat", loc.getLatitude());
			infoIntent.putExtra("locLon", loc.getLongitude());
			startActivity(infoIntent);
			break;
		default:
			break;
		}

	}

	
	@SuppressLint("InflateParams")
	private void showSelectDialog() {
		if (selectDialog != null) {
			selectDialog.show();
			return;
		}
		selectDialog = new Dialog(mContext, R.style.dialog);
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_petrol_distance, null);
		selectDialog.setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		selectDialog.setCanceledOnTouchOutside(true);
		selectDialog.show();
	}

	@SuppressLint("InflateParams")
	private void showLoadingDialog() {
		if (loadingDialog != null) {
			loadingDialog.show();
			return;
		}
		loadingDialog = new Dialog(mContext, R.style.dialog_loading);
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_petrol_loading, null);
		loadingDialog.setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		loadingDialog.setCancelable(false);
		loadingDialog.show();
	}

	
	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
		}
		mToast.setText(msg);
		mToast.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		
		
		
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
		mLocationClient.stop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mMapView.onDestroy();
		mHandler = null;
	}

}
