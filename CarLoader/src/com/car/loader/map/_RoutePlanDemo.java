package com.car.loader.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.car.loader.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 路线规划的类
 * @author Zsy
 *
 */
public class _RoutePlanDemo extends Activity implements OnClickListener ,OnGetGeoCoderResultListener{
	
	private MapView mapView;
	private BaiduMap map;
	private LocationMode mode;
	//private GeoCoder msearch = null;
	private boolean isFirstLoc = true;
	//private BDLocation place;
	private LocationClient client;
	private SensorManager sensorManager;
	private MySensorEventListner listener;
	private ImageView iv_open_traffic;
	//private ImageView iv_mode;
//	private RelativeLayout rl_mode;
	private ImageView iv_drection;
	
	private TextView tv_state;
	private TextView tv_place;
	private GeoCoder search = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mapView = (MapView) findViewById(R.id.map);
		iv_open_traffic = (ImageView) findViewById(R.id.iv_open_traffic);
		//iv_mode = (ImageView) findViewById(R.id.iv_mode);
		//rl_mode = (RelativeLayout) findViewById(R.id.rl_mode);
		iv_drection = (ImageView) findViewById(R.id.iv_derction);
		
		
		tv_state = (TextView) findViewById(R.id.tv_state);
		tv_place = (TextView) findViewById(R.id.tv_place);
		
	
		map = mapView.getMap();
		mapView.showZoomControls(false);//不显示缩放按钮
		search = GeoCoder.newInstance();//获取地理编码的实例
		search.setOnGetGeoCodeResultListener(this);//设置结果监听者
		
		MapStatus ms = new MapStatus.Builder().overlook(-30).zoom(nowZoom).build();//设置缩放级别等
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);//描述地图将要发生的变化

		map.animateMapStatus(u, 1000);//动画时长1000ms
		map.setMyLocationEnabled(true);//使得定位功能可用
		map.setTrafficEnabled(false);//关闭实时交通路况图
		map.setBuildingsEnabled(true);//建筑物可见
		//mapView.showZoomControls(false);

		client = new LocationClient(this);
		client.registerLocationListener(new MyLocationListenner());//注册位置监听
		mode = LocationMode.NORMAL;
		map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));//设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效

		LocationClientOption option = new LocationClientOption();// 定位用
		option.setOpenGps(true);//开启GPS
		//LocationClientOption.LocationMode.;
		option.setCoorType("bd09ll");//坐标类型
		option.setScanSpan(1000);// 设置扫描间隔
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);

		client.setLocOption(option);//设置定位方式
		client.start();//开始定位

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		listener = new MySensorEventListner();
		
		initGPS();
	}
	/**
	 * 交通状况信息是否已经开启
	 */
	private boolean open = false;
	/**
	 * 开启关闭交通状况图
	 * @param view
	 */
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
	/**
	 * 这里是回到当前位置，这种做法有点欠妥但是是可行
	 * @param view
	 */
	public void changeMode(View view) {
		switch (mode) {
		case NORMAL:
			mode = LocationMode.FOLLOWING;
			break;
		default:
			break;
		}
		if (mode != LocationMode.COMPASS) {
			map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));
			mode = LocationMode.NORMAL;
			map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));
		}
	}
	/**
	 * 改变定位图层显示方式，一共有三种
	 * COMPASS  罗盘态，显示定位方向圈，保持定位图标在地图中心
	 * FOLLOWING 跟随态，保持定位图标在地图中心
	 * NORMAL  普通态： 更新定位数据时不对地图做任何操作
	 * @param view
	 */
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
	/**
	 * 当前位置
	 */
	private BDLocation nowLocation;
	/**
	 * 实时位置监听器
	 * 
	 *
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置

			if (location == null || map == null) {
				return;
			}
			nowLocation = location;
			
			search.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(location.getLatitude(), location.getLongitude())));//坐标转地理位置
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(x).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			map.setMyLocationData(locData);
			
			if (isFirstLoc) {//是否是第一次
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
	/**
	 * 手机的方向传感器
	 * @author Zsy
	 *
	 */
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
		mapView.onPause();
		if(sensorManager!=null)
			sensorManager.unregisterListener(listener);
		//sensorManager = null;
		//listener = null;
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
	/**
	 * 用来记录当前地图定位的状态是卫星地图还是普通地图
	 */
	private int state = 0;
	/**
	 * 改变地图状态
	 * @param view
	 */
	public void changeState(View view){
		if(state==0){
			tv_state.setText("卫星");
			state = 1;
			map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		}else if(state==1){
			state = 0;
			tv_state.setText("普通");
			map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		}
		
	}
	/**
	 * 显示当前地点位置的详细信息
	 * @param view
	 */
	public void showPlaceDetil(View view){
		Intent intent = new Intent(this,NowPlaceActivity.class);
		intent.putExtra("place", nowPlace);
		startActivity(intent);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 坐标转地理
	 * @param result
	 */
	private String nowPlace;
	private String city;
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
       
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			nowPlace = "位置不可用";
			tv_place.setText(nowPlace);
			
			return;
		}
		city = result.getAddressDetail().city;
		nowPlace = result.getAddress();
		tv_place.setText(nowPlace);
		//Toast.makeText(this, "抱歉，未能找到结果"+city, Toast.LENGTH_LONG)
		//.show();
	}
	/**
	 * 路线规划
	 * @param view
	 */
	public void routePlan(View view){
		Intent intent = new Intent(this,SelectStartAndEndPlaceActivity.class);
		intent.putExtra("place", nowPlace);
		intent.putExtra("city", city);
		intent.putExtra("locLat", nowLocation.getLatitude());
		intent.putExtra("locLon", nowLocation.getLongitude());
		startActivity(intent);
	}
	/**
	 * 初始化GPS
	 */
	private void initGPS(){
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
			Toast.makeText(this, "GPS定位未打开!", 0).show();
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("开启GPS定位更精确哦！");
			dialog.setPositiveButton("开启", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0);
				}
			});
			
			dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}else{
			Toast.makeText(this, "GPS已开启", 0).show();
		}
	}
	
	public void back(View view){
		this.finish();
	}
}
