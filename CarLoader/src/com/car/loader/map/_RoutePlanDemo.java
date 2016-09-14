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
 * ·�߹滮����
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
		mapView.showZoomControls(false);//����ʾ���Ű�ť
		search = GeoCoder.newInstance();//��ȡ��������ʵ��
		search.setOnGetGeoCodeResultListener(this);//���ý��������
		
		MapStatus ms = new MapStatus.Builder().overlook(-30).zoom(nowZoom).build();//�������ż����
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);//������ͼ��Ҫ�����ı仯

		map.animateMapStatus(u, 1000);//����ʱ��1000ms
		map.setMyLocationEnabled(true);//ʹ�ö�λ���ܿ���
		map.setTrafficEnabled(false);//�ر�ʵʱ��ͨ·��ͼ
		map.setBuildingsEnabled(true);//������ɼ�
		//mapView.showZoomControls(false);

		client = new LocationClient(this);
		client.registerLocationListener(new MyLocationListenner());//ע��λ�ü���
		mode = LocationMode.NORMAL;
		map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));//���ö�λͼ��������Ϣ��ֻ��������λͼ������ö�λͼ��������Ϣ�Ż���Ч

		LocationClientOption option = new LocationClientOption();// ��λ��
		option.setOpenGps(true);//����GPS
		//LocationClientOption.LocationMode.;
		option.setCoorType("bd09ll");//��������
		option.setScanSpan(1000);// ����ɨ����
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);

		client.setLocOption(option);//���ö�λ��ʽ
		client.start();//��ʼ��λ

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		listener = new MySensorEventListner();
		
		initGPS();
	}
	/**
	 * ��ͨ״����Ϣ�Ƿ��Ѿ�����
	 */
	private boolean open = false;
	/**
	 * �����رս�ͨ״��ͼ
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
	 * �����ǻص���ǰλ�ã����������е�Ƿ�׵����ǿ���
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
	 * �ı䶨λͼ����ʾ��ʽ��һ��������
	 * COMPASS  ����̬����ʾ��λ����Ȧ�����ֶ�λͼ���ڵ�ͼ����
	 * FOLLOWING ����̬�����ֶ�λͼ���ڵ�ͼ����
	 * NORMAL  ��̬ͨ�� ���¶�λ����ʱ���Ե�ͼ���κβ���
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
	 * ��ǰλ��
	 */
	private BDLocation nowLocation;
	/**
	 * ʵʱλ�ü�����
	 * 
	 *
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��

			if (location == null || map == null) {
				return;
			}
			nowLocation = location;
			
			search.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(location.getLatitude(), location.getLongitude())));//����ת����λ��
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(x).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			map.setMyLocationData(locData);
			
			if (isFirstLoc) {//�Ƿ��ǵ�һ��
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
	 * �ֻ��ķ��򴫸���
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
		// �˳�ʱ���ٶ�λ
		client.stop();
		// �رն�λͼ��
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
	 * ������¼��ǰ��ͼ��λ��״̬�����ǵ�ͼ������ͨ��ͼ
	 */
	private int state = 0;
	/**
	 * �ı��ͼ״̬
	 * @param view
	 */
	public void changeState(View view){
		if(state==0){
			tv_state.setText("����");
			state = 1;
			map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		}else if(state==1){
			state = 0;
			tv_state.setText("��ͨ");
			map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		}
		
	}
	/**
	 * ��ʾ��ǰ�ص�λ�õ���ϸ��Ϣ
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
	 * ����ת����
	 * @param result
	 */
	private String nowPlace;
	private String city;
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
       
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
					.show();
			nowPlace = "λ�ò�����";
			tv_place.setText(nowPlace);
			
			return;
		}
		city = result.getAddressDetail().city;
		nowPlace = result.getAddress();
		tv_place.setText(nowPlace);
		//Toast.makeText(this, "��Ǹ��δ���ҵ����"+city, Toast.LENGTH_LONG)
		//.show();
	}
	/**
	 * ·�߹滮
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
	 * ��ʼ��GPS
	 */
	private void initGPS(){
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
			Toast.makeText(this, "GPS��λδ��!", 0).show();
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("����GPS��λ����ȷŶ��");
			dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0);
				}
			});
			
			dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}else{
			Toast.makeText(this, "GPS�ѿ���", 0).show();
		}
	}
	
	public void back(View view){
		this.finish();
	}
}
