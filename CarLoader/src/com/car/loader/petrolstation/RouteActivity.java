package com.car.loader.petrolstation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.car.loader.R;
import com.car.loader.map.DrivingRouteOverlay;
import com.car.loader.ui.HeaderView;


public class RouteActivity extends Activity implements OnGetRoutePlanResultListener{

	private Context mContext;
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private ImageView iv_back = null;
	private RoutePlanSearch mSearch=null;
	
	private HeaderView h_header;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_petrol_route);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RouteActivity.this.finish();
			}
		});
		mContext = this;
		initView();
	}

	private void initView() {
		
		Intent intent=getIntent();
		Bundle extras=intent.getExtras();
		
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.showScaleControl(false);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();
		
		this.mSearch=RoutePlanSearch.newInstance();
		
		mSearch.setOnGetRoutePlanResultListener(this);
		
		LatLng locLatLng=new LatLng(extras.getDouble("locLat"), extras.getDouble("locLon"));
		
		LatLng desLatLng =new LatLng(extras.getDouble("lat"), extras.getDouble("lon"));
		System.out.println(locLatLng.toString());
		System.out.println(desLatLng.toString());
		PlanNode st = PlanNode.withLocation(locLatLng);
		PlanNode en = PlanNode.withLocation(desLatLng);
		
		mSearch.drivingSearch(new DrivingRoutePlanOption().from(st).to(en));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}
	/**
	 * 
	 * 
	 * ‰∏ãÈù¢ÁöÑ‰∏â‰∏™ÊñπÊ≥ïÊòØÂÆûÁé∞OnGetRoutePlanResultListenerÊé•Âè£ÂêéË¶ÅÈáçÂÜôÁöÑ‰∏â‰∏™ÊñπÊ≥?
	 * **/

	/**
	 * Âº?ËΩ¶ÊúÄ‰Ω≥Ë∑ØÁ∫?
	 * **/
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		
		if(result==null||result.error!=SearchResult.ERRORNO.NO_ERROR)
		{
			Toast.makeText(mContext, "±ß«∏£¨√ª”–’“µΩΩ·π˚!", Toast.LENGTH_SHORT).show();
		}
		
		if(result.error==SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR)
		{
			return;
		}
		
		if(result.error==SearchResult.ERRORNO.NO_ERROR)
		{
			
			DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
		
	}
	
	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		
		
	}
	
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		
		
	}

}
