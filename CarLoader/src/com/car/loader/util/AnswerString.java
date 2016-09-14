package com.car.loader.util;

import org.json.JSONObject;

import com.car.loader.domain.CarInfo;

public class AnswerString {
	private AnswerString(){
		
	}
	/**
	 * 将扫描的信息切割并封装好
	 * @param data
	 * @param phone
	 * @return
	 */
	public static CarInfo answerString(String data,String phone){
		String[] ss = data.split(",");
		if(ss.length!=11){
			return null;
		}
		
		CarInfo info = new CarInfo();
		info.setPhone(phone);
		info.setCarbrand(ss[0]);
		info.setCarsign(ss[1]);
		info.setCartype(ss[2]);
		info.setCarnumber(ss[3]);
		info.setCarenginnumber(ss[4]);
		info.setCarbodylevel(ss[5]);
		info.setCarmileage(ss[6]);
		info.setCaroil(ss[7]);
		info.setCarenginstate(ss[8]);
		info.setCartranstate(ss[9]);
		info.setCarlightstate(ss[10]);
		return info;
	}
}
