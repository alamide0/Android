package com.car.loader.map;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
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
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.car.loader.R;

/**
 * 原来用的类，现在已经作废，但保留以防以后查验
 * @author Zsy
 *
 */
public class RoutePlanDemo extends Activity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener , OnGetSuggestionResultListener,OnGetGeoCoderResultListener{
	private LinearLayout ll_surround = null;
    private BaiduMap map = null;
    private MapView mapView = null;
    
    private int nodeIndex =0 ;
    private RoutePlanSearch search = null;
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    LocationClient client;
    private AutoCompleteTextView et_start ;
    private AutoCompleteTextView et_end ;
    private SuggestionSearch suggestion;
    private ArrayAdapter<String> adapter;
    private BDLocation place;
    private GeoCoder msearch = null;
    boolean isFirstLoc = true;
    private String start_p;
    private UiSettings uiSettings ;
    private LocationMode mode;
    
    private Button btn_change_mode;
    private Button btn_open_traffic;
    private ImageView myImage1;
    private ListView myContent1;
    private SlidingDrawer sd ;
    private ImageView iv_next;
    private ImageView iv_pre;
    private TextView tv_content;
    private RelativeLayout rl_show_route;
    private Button btn_back;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map);
        ll_surround = (LinearLayout) findViewById(R.id.ll_surround);
        btn_change_mode = (Button) findViewById(R.id.btn_change_mode);
        btn_open_traffic = (Button) findViewById(R.id.btn_open_traffic);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_pre = (ImageView) findViewById(R.id.iv_pre);
        tv_content = (TextView) findViewById(R.id.tv_content);
        rl_show_route = (RelativeLayout) findViewById(R.id.rl_show_route);
        btn_back = (Button) findViewById(R.id.btn_back);
        
        myImage1 = (ImageView) findViewById(R.id.myImage1);
        myContent1 = (ListView) findViewById(R.id.myContent1);
        myContent1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LatLng nodeLocation = ((DrivingRouteLine.DrivingStep)(route.getAllStep().get(position))).getEntrance().getLocation();
				map.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
				sd.close();
				nodeIndex = position;
				tv_content.setText(((DrivingRouteLine.DrivingStep)(route.getAllStep().get(position))).getEntranceInstructions());
				rl_show_route.setVisibility(View.VISIBLE);
				sd.setVisibility(View.INVISIBLE);
				btn_back.setVisibility(View.VISIBLE);
				
			}
		});
        
        iv_pre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(nodeIndex>0){
					nodeIndex--;
				}else{
					return;
				}
				LatLng location = null;
				String info = null;
				Object step = route.getAllStep().get(nodeIndex);
				info = ((DrivingRouteLine.DrivingStep)step).getInstructions();
				location = ((DrivingRouteLine.DrivingStep)step).getEntrance().getLocation();
				if(info==null||location==null)return;
				map.setMapStatus(MapStatusUpdateFactory.newLatLng(location));
				tv_content.setText(info);
			}
		});
        
        iv_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(nodeIndex<route.getAllStep().size()-1){
					nodeIndex++;
				}else{
					return;
				}
				LatLng location = null;
				String info = null;
				Object step = route.getAllStep().get(nodeIndex);
				info = ((DrivingRouteLine.DrivingStep)step).getEntranceInstructions();
				location = ((DrivingRouteLine.DrivingStep)step).getEntrance().getLocation();
				if(info==null||location==null)return;
				map.setMapStatus(MapStatusUpdateFactory.newLatLng(location));
				tv_content.setText(info);
				
			}
		});
        sd = (SlidingDrawer) findViewById(R.id.sd);
        sd.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				myImage1.setImageResource(R.drawable.good);
			}
		});
        sd.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				myImage1.setImageResource(R.drawable.sad);
			}
		});
        
        suggestion = SuggestionSearch.newInstance();
        suggestion.setOnGetSuggestionResultListener(this);//根据输入的信息进行地点联想
        
        
        
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        msearch = GeoCoder.newInstance();
        msearch.setOnGetGeoCodeResultListener(this);//根据坐标查地点，地理编码
        
        mapView =  (MapView) findViewById(R.id.map);
        map = mapView.getMap();
        mode = LocationMode.NORMAL;
        
        MapStatus ms = new MapStatus.Builder().overlook(-30).build();
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
		
        map.animateMapStatus(u, 1000);
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(false);
        map.setBuildingsEnabled(true);
        mapView.showZoomControls(false);
        
        map.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
        
        client = new LocationClient(this);//定位用
        client.registerLocationListener(new MyLocationListenner());
        
        map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));
        
        
        
        LocationClientOption option = new LocationClientOption();//定位用
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        client.setLocOption(option);
        client.start();
        
        search = RoutePlanSearch.newInstance();
        search.setOnGetRoutePlanResultListener(this);//获取路线监听的接口
        
        et_start =  (AutoCompleteTextView) findViewById(R.id.et_start);
        et_start.setAdapter(adapter);
    	et_end =  (AutoCompleteTextView) findViewById(R.id.et_end);
    	et_end.setAdapter(adapter);
    	et_start.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
				String str = s.toString();
				if(str.length()<=0)return;
				suggestion.requestSuggestion((new SuggestionSearchOption()).keyword(str).city(""));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
    	
    	et_end.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = s.toString();
				if(str.length()<=0)return;
				suggestion.requestSuggestion((new SuggestionSearchOption()).keyword(str).city(""));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
    }
    
    public void searchRoute(View view){
    	
    	//PlanNode start = PlanNode.withLocation(new LatLng(place.getLatitude(), place.getAltitude()));
    	//System.out.println(place.getLatitude()+":"+place.getLongitude());
    	// uiSettings.setCompassEnabled(true);
    	
    	//System.out.println(start_p);
    	//place.;
    	//System.out.println(place.getAddrStr()+":"+place.getAddress().address);
    	PlanNode start = PlanNode.withCityNameAndPlaceName("南京", start_p);

    	PlanNode end = PlanNode.withCityNameAndPlaceName("南京", et_end.getText().toString().trim());
    	search.drivingSearch((new DrivingRoutePlanOption()).from(start).to(end).policy(DrivingPolicy.ECAR_AVOID_JAM));//选择路线的策略
    	ll_surround.setVisibility(View.GONE);
    }
    /**
     * 规划驾车行驶的路线
     */
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		 if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
	            Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
	        }
	        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
	            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
	            //result.getSuggestAddrInfo()
	            return;
	        }
	        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
	            //nodeIndex = -1;
	            route = result.getRouteLines().get(0);
	            DrivingRouteOverlay overlay = new DrivingRouteOverlay(map);
	            routeOverlay = overlay;
	            map.setOnMarkerClickListener(overlay);
	            overlay.setData(result.getRouteLines().get(0));
	            overlay.addToMap();
	            overlay.zoomToSpan();
	            myContent1.setAdapter(new MyAdapter());
//	            
//	            System.out.println(route.getDistance()+":"+route.getDuration());
//	            int t = route.getAllStep().size();
//	            for(int i=0; i<t; i++){
//	            	Object step = route.getAllStep().get(i);
//	            	LatLng s = ((DrivingRouteLine.DrivingStep)step).getEntrance().getLocation();
//	            	System.out.println(s.latitude+":"+s.longitude);
//	            }
	        }
		
	}
	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		
	}
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		
	}
	@Override
	public void onMapClick(LatLng arg0) {
		
	}
	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}
	/**
	 * 根据输入的文字联想地点
	 */
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if(res==null||res.getAllSuggestions()==null){
			return;
		}
		adapter.clear();
		for(SuggestionResult.SuggestionInfo info: res.getAllSuggestions()){
			if(info.key!=null){
				adapter.add(info.key);
			}
		}
		adapter.notifyDataSetChanged();
		
	}
	/**
	 * 定位用的监听
	 * @author Zsy
	 *
	 */
	public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
        	
            if (location == null || map == null) {
                return;
            }
            place = location;
        	msearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng((float)place.getLatitude(), (float)place.getLongitude())));

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            map.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                map.animateMapStatus(u);
            }
        }
        public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		
	}
	/**
	 * 坐标转地理位置
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		start_p = result.getAddress();
	}
	
	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		client.stop();
		// 关闭定位图层
		map.setMyLocationEnabled(false);
		mapView.onDestroy();
		mapView = null;
		super.onDestroy();
	}
   
	 private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

	        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
	            super(baiduMap);
	        }

	        @Override
	        public BitmapDescriptor getStartMarker() {
	            if (true) {
	                return BitmapDescriptorFactory.fromResource(R.drawable.good);
	            }
	            return null;
	        }

	        @Override
	        public BitmapDescriptor getTerminalMarker() {
	            if (true) {
	                return BitmapDescriptorFactory.fromResource(R.drawable.sad);
	            }
	            return null;
	        }
	    }
	 
	 public void changeMode(View view){
		 switch(mode){
		 case NORMAL:
			 btn_change_mode.setText("跟随");
			 mode = LocationMode.FOLLOWING;
			 
			 break;
		 case FOLLOWING: 
			 btn_change_mode.setText("罗盘");
			 mode = LocationMode.COMPASS;
			 break;
		 case COMPASS:
			 btn_change_mode.setText("普通");
			 mode = LocationMode.NORMAL;
			 break;
		 default:
			 break;
		 }
		 map.setMyLocationConfigeration(new MyLocationConfiguration(mode, true, null));
	 }
	 
	 public void openTraffic(View view){
		 if(btn_open_traffic.getText().equals("开启路况信息")){
			map.setTrafficEnabled(true); 
			 btn_open_traffic.setText("关闭路况信息");
		 }else{
			 map.setTrafficEnabled(false);
			 btn_open_traffic.setText("开启路况信息");
		 }
	 }
	 
	 private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return route.getAllStep().size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder;
			if(convertView!=null){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(RoutePlanDemo.this, R.layout.item_route, null);
				holder = new ViewHolder();
				holder.tv_node_info = (TextView) view.findViewById(R.id.tv_node_info);
				view.setTag(holder);
			}
			holder.tv_node_info.setText(((DrivingRouteLine.DrivingStep)route.getAllStep().get(position)).getEntranceInstructions());
			return view;
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		
		 
	 }
	 private static class ViewHolder{
		 private TextView tv_node_info;
	 }
	 
	 public void back(View view){
//		 switch(btn_back.getVisibility()){
//		 case View.VISIBLE:
//			 btn_back.setVisibility(View.INVISIBLE);
//			 rl_show_route.setVisibility(View.INVISIBLE);
//			sd.setVisibility(View.VISIBLE);
//			 break;
//		 case View.INVISIBLE:
//			 btn_back.setVisibility(View.VISIBLE);
//			 break;
//		 }
		
	 }
}
