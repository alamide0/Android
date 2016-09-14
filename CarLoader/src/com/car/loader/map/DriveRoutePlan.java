package com.car.loader.map;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.car.loader.R;
import com.car.loader.util.MathUtils;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 用来显示规划后路线的地图与_RoutePlanDemon类似
 * @author Zsy
 *
 */
public class DriveRoutePlan extends Activity implements OnClickListener, OnGetGeoCoderResultListener,
		OnGetRoutePlanResultListener, BaiduMap.OnMapClickListener {

	private MapView mapView;
	private BaiduMap map;
	private LocationMode mode;
	private GeoCoder msearch = null;
	private boolean isFirstLoc = true;
	private BDLocation place;
	private LocationClient client;
	private SensorManager sensorManager;
	private MySensorEventListner listener;
	private ImageView iv_open_traffic;
	private ImageView iv_mode;
	private RelativeLayout rl_mode;
	private ImageView iv_drection;

	private TextView tv_state;
	private TextView tv_place;

	private GeoCoder search = null;
	private RoutePlanSearch rSearch;

	private TextView tv_distance, tv_duration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_route);

		mapView = (MapView) findViewById(R.id.map);
		iv_open_traffic = (ImageView) findViewById(R.id.iv_open_traffic);
		iv_mode = (ImageView) findViewById(R.id.iv_mode);
		rl_mode = (RelativeLayout) findViewById(R.id.rl_mode);
		iv_drection = (ImageView) findViewById(R.id.iv_derction);

		tv_state = (TextView) findViewById(R.id.tv_state);
		tv_place = (TextView) findViewById(R.id.tv_place);

		tv_distance = (TextView) findViewById(R.id.tv_route_distance);
		tv_duration = (TextView) findViewById(R.id.tv_route_duration);

		map = mapView.getMap();
		map.setOnMapClickListener(this);
		mapView.showZoomControls(false);
		search = GeoCoder.newInstance();
		search.setOnGetGeoCodeResultListener(this);

		MapStatus ms = new MapStatus.Builder().overlook(-30).zoom(nowZoom).build();
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);

		map.animateMapStatus(u, 1000);
		map.setMyLocationEnabled(true);
		map.setTrafficEnabled(false);
		map.setBuildingsEnabled(true);
		mapView.showZoomControls(false);

		client = new LocationClient(this);
		client.registerLocationListener(new MyLocationListenner());
		mode = LocationMode.NORMAL;
		map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));

		LocationClientOption option = new LocationClientOption();// 定位用
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);// 设置扫描间隔

		client.setLocOption(option);
		client.start();
		// Bundle extras = getIntent().getExtras();
		String city = getIntent().getStringExtra("city");
		String start = getIntent().getStringExtra("start");
		String end = getIntent().getStringExtra("end");
		double lat = getIntent().getDoubleExtra("locLat", 0);
		double lon = getIntent().getDoubleExtra("locLon", 0);
		boolean useStart = getIntent().getBooleanExtra("useStart", false);
		boolean useEnd = getIntent().getBooleanExtra("useEnd", false);
		PlanNode startNode;
		//先判断当前的起点位置是否是当前位置，如果是当前位置则直接使用当前位置的地理坐标
		if (!useStart) {
			startNode = PlanNode.withCityNameAndPlaceName(city, start);
		} else {
			startNode = PlanNode.withLocation(new LatLng(lat, lon));
		}
		PlanNode endNode;
		//同上
		if (!useEnd) {
			endNode = PlanNode.withCityNameAndPlaceName(city, end);
		} else {
			endNode = PlanNode.withLocation(new LatLng(lat, lon));
		}

		rSearch = RoutePlanSearch.newInstance();
		rSearch.setOnGetRoutePlanResultListener(this);
		rSearch.drivingSearch(
				(new DrivingRoutePlanOption()).from(startNode).to(endNode).policy(DrivingPolicy.ECAR_AVOID_JAM));// 选择路线的策略
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		listener = new MySensorEventListner();
	}

	private boolean open = false;

	public void openTraffic(View view) {
		if (open) {
			iv_open_traffic.setImageResource(R.drawable.traffic_close);
			map.setTrafficEnabled(false);
			open = false;
		} else {
			iv_open_traffic.setImageResource(R.drawable.traffic_open);
			map.setTrafficEnabled(true);
			open = true;
		}

	}

	public void changeMode(View view) {
		switch (mode) {
		case NORMAL:
			mode = LocationMode.FOLLOWING;
			break;

		}
		if (mode != LocationMode.COMPASS) {
			map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));
			mode = LocationMode.NORMAL;
			map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));
		}
		//map.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
	//上面是个正确的方法，但是我现在不想改
	}

	public void showDirection(View view) {
		switch (mode) {
		case NORMAL:
			mode = LocationMode.COMPASS;
			iv_drection.setImageResource(R.drawable.lock_drection);
			break;
		case COMPASS:
			mode = LocationMode.FOLLOWING;
			iv_drection.setImageResource(R.drawable.map_folllow);
			break;
		case FOLLOWING:
			mode = LocationMode.NORMAL;
			iv_drection.setImageResource(R.drawable.lock);
			break;
		}
		map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));
	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置

			if (location == null || map == null) {
				return;
			}

			search.reverseGeoCode(
					new ReverseGeoCodeOption().location(new LatLng(location.getLatitude(), location.getLongitude())));
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(x).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			map.setMyLocationData(locData);

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				map.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private float x = 0;

	private final class MySensorEventListner implements SensorEventListener {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				x = event.values[SensorManager.DATA_X];

			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

	}

	@Override
	protected void onPause() {
		if (sensorManager != null)
			sensorManager.unregisterListener(listener);
		// sensorManager = null;
		// listener = null;
		mapView.onPause();

		super.onPause();
	}

	@Override
	protected void onResume() {

		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		client.stop();
		// 关闭定位图层
		map.setMyLocationEnabled(false);
		sensorManager = null;
		listener = null;
		mapView.onDestroy();
		mapView = null;
		super.onDestroy();
	}

	private int nowZoom = 16;

	@Override
	public void onClick(View v) {

	}

	private int state = 0;

	public void changeState(View view) {
		if (state == 0) {
			tv_state.setText("卫星");
			state = 1;
			map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		} else if (state == 1) {
			state = 0;
			tv_state.setText("普通");
			map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		}

	}

	public void showPlaceDetil(View view) {
		Intent intent = new Intent(this, NowPlaceActivity.class);
		intent.putExtra("place", nowPlace);
		startActivity(intent);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 坐标转地理
	 * 
	 * @param result
	 */
	private String nowPlace;
	private String city;

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			nowPlace = "位置不可用";
			tv_place.setText(nowPlace);

			return;
		}
		city = result.getAddressDetail().city;
		nowPlace = result.getAddress();

		tv_place.setText(nowPlace);

		// Toast.makeText(this, "抱歉，未能找到结果"+city, Toast.LENGTH_LONG)
		// .show();
	}

	private RouteLine route;
	private OverlayManager routeOverlay;

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(DriveRoutePlan.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			// nodeIndex = -1;
			route = result.getRouteLines().get(0);
			System.out.println(route.getDistance() + ":" + route.getDuration());
			tv_distance.setText(MathUtils.M2KM(route.getDistance()));
			tv_duration.setText(MathUtils.formatS(route.getDuration()));
			DrivingRouteOverlay overlay = new DrivingRouteOverlay(map);
			routeOverlay = overlay;
			map.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();

		}

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	public void back(View view) {
		this.finish();
	}

	public void routeDetils(View view) {
		Intent intent = new Intent(this, ShowRouteDetilsActivity.class);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < route.getAllStep().size(); i++) {
			list.add(((DrivingRouteLine.DrivingStep) route.getAllStep().get(i)).getInstructions());
		}
		intent.putStringArrayListExtra("list", list);
		System.out.println("list=" + list.size());
		startActivity(intent);
	}

	int nodeIndex = -1;
	private TextView popupText;

	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex == -1 && v.getId() == R.id.pre) {
			return;
		}
		// 设置节点索引
		if (v.getId() == R.id.next) {
			if (nodeIndex < route.getAllStep().size() - 1) {
				nodeIndex++;
			} else {
				return;
			}
		} else if (v.getId() == R.id.pre) {
			if (nodeIndex > 0) {
				nodeIndex--;
			} else {
				return;
			}
		}
		// 获取节结果信息
		LatLng nodeLocation = null;
		String nodeTitle = null;
		Object step = route.getAllStep().get(nodeIndex);
		if (step instanceof DrivingRouteLine.DrivingStep) {
			nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
			nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
		} else if (step instanceof WalkingRouteLine.WalkingStep) {
			nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
			nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
		} else if (step instanceof TransitRouteLine.TransitStep) {
			nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
			nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
		}

		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		// 移动节点至中心
		map.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		// show popup
		popupText = new TextView(DriveRoutePlan.this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFFffffff);
		popupText.setText(nodeTitle);
		//popupText.sett
		map.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

	}

	@Override
	public void onMapClick(LatLng point) {
		map.hideInfoWindow();
		//System.out.println("++++++");
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
