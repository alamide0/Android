package com.car.loader.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.car.loader.domain.Petrol;
import com.car.loader.domain.Station;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


/**
 * 作用：本方法用于获取聚合的数据
 * @author Zsy
 *
 */
public class StationData {
	
    class MyComparator implements Comparator{  
        
        public int compare(Object o1,Object o2) {  
           Station s1=(Station)o1;  
           Station s2=(Station)o2;   
           if(s1.getDistance() < s2.getDistance())
        	   return 1;
           else
        	   return 0;
           }  
    }  
	
	
	Handler mHandler;

	public StationData(Handler mHandler) {
		super();
		this.mHandler = mHandler;
	}

	public   void getStationData(double lat, double lon, int distance,Context context) {
		Parameters params = new Parameters();
		params.add("lat", lat);// 维度
		params.add("lon", lon);// 经度
		params.add("r", distance);
		params.add("key", "150e996466d578ad35a6484e40c9f121");
		//
		// 1d517d7b48f8918fc1d0b483c77bc02a
//		JuheData.executeWithAPI(7, "http://apis.juhe.cn/oil/local",
//				JuheData.GET, params, new DataCallBack() {
//
//					@Override
//					public void resultLoaded(int err, String reason,
//							String result) {
//						// TODO Auto-generated method stub
//						if (err == 0) {
//							try {
//								ArrayList<Station> list = parser(result);
//								//注意这个地方利用Handler往前台传送是否获取数据成功
//								if (list != null && mHandler != null) {
//									Message msg = Message.obtain(mHandler,
//											0x01, list);
//									msg.sendToTarget();
//								} else {
//									Message msg = Message.obtain(mHandler,
//											0x02, err);
//									msg.sendToTarget();
//
//								}
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//
//				});
		JuheData.executeWithAPI(context, 7, "http://apis.juhe.cn/oil/local", JuheData.GET, params, new DataCallBack() {
			
			@Override
			public void onSuccess(int arg0, String arg1) {
				System.out.println("zzzzzzzzzzzzzzzzzzzzz"+arg1);
					try {
						ArrayList<Station> list = parser(arg1);
						//注意这个地方利用Handler往前台传送是否获取数据成功
						if (list != null && mHandler != null) {
							Message msg = Message.obtain(mHandler,
									0x01, list);
							msg.sendToTarget();
						} else {
							Message msg = Message.obtain(mHandler,
									0x02,list);
							msg.sendToTarget();

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//				
			}
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1, Throwable arg2) {
				// TODO Auto-generated method stub
				System.out.println("调用失败");
			}
		});
	}

	//
	private ArrayList<Station> parser(String str) throws JSONException {
		ArrayList<Station> list = new ArrayList<Station>();
		JSONObject object = new JSONObject(str);
		JSONObject resultJson = object.optJSONObject("result");
		if (object.getInt("resultcode") == 200) {
			JSONArray dataJson = resultJson.getJSONArray("data");
			for (int i = 0; i < dataJson.length(); i++) {
				JSONObject json = dataJson.getJSONObject(i);
				Station station = new Station();
				station.setName(json.getString("name"));
				station.setArea(json.getString("areaname"));
				station.setAddr(json.getString("address"));
				station.setBrand(json.getString("brandname"));
				station.setDistance(json.getInt("distance"));
				station.setLat(json.getDouble("lat"));
				station.setLon(json.getDouble("lon"));
				Log.d("StationData",
						station.getAddr() + " " + station.getName());
				JSONObject priceJson = json.getJSONObject("price");
				ArrayList<Petrol> priceList = new ArrayList<Petrol>();

				Iterator<String> priceI = priceJson.keys();

				while (priceI.hasNext()) {

					Petrol p = new Petrol();

					String key = priceI.next();

					String value = priceJson.getString(key);
					Log.d("StationData", value);

					p.setType(key.replace("E", "") + "#");

					p.setPrice(value + "元/升");

					priceList.add(p);

				}
				station.setPriceList(priceList);
				// System.out.println(json.getJSONObject("gastprice"));
				// if((JSONObject)json.getJSONObject("gastprice")==null){
				// System.out.println("--------");
				// continue;
				// }

				// if(json.getJSONObject("gastprice")==null)
				// continue;
				System.out.println(json);
				//String sttr = json.optJSONObject("gastprice").toString();
				System.out.println("-----------------------");
				JSONObject gastPriceJson = json.optJSONObject("gastprice");
				ArrayList<Petrol> gastPriceList = new ArrayList<Petrol>();
				if (gastPriceJson != null) {
					Iterator<String> gastPriceI = gastPriceJson.keys();
					while (gastPriceI.hasNext()) {

						Petrol p = new Petrol();

						String key = gastPriceI.next();

						String value = gastPriceJson.getString(key);

						p.setType(key);

						p.setPrice(value + "元/升");

						gastPriceList.add(p);

					}

					station.setGastPriceList(gastPriceList);

					list.add(station);

				}

				

			}
			
			MyComparator mc = new MyComparator() ;  
			Collections.sort(list, mc) ;  
		} else {//如果放回数据有误，就将错误的返回码返给前台
			Message msg = Message.obtain(mHandler, 0x02,
					object.getInt("resultcode"));

			msg.sendToTarget();
		}
		return list;
	}
	
	

}
