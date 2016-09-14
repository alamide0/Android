package com.car.loader.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.car.loader.domain.CarInfo;

public class AnswerJSON {
	private AnswerJSON(){
		
	}
	
	public static CarInfo answerJ(String data) throws JSONException{
		CarInfo info = new CarInfo();
		JSONObject obj = new JSONObject(data);
		//obj.getJSONObject(name);
		info.setPhone("18362983813");
		info.setCarbrand(obj.getString("carbrand"));
		info.setCarsign(obj.getString("carsign"));
		info.setCartype(obj.getString("cartype"));
		info.setCarnumber(obj.getString("carnumber"));
		info.setCarenginnumber(obj.getString("carenginnumber"));
		info.setCarbodylevel(obj.getString("carbodylevel"));
		info.setCarmileage(obj.getString("carmileage"));
		info.setCaroil(obj.getString("caroil"));
		info.setCarenginstate(obj.getString("carenginstate"));
		info.setCartranstate(obj.getString("cartranstate"));
		info.setCarlightstate(obj.getString("carlightstate"));
		return info;
	}
	
	
}
