package com.car.loader.util;

import java.math.BigDecimal;

public class MathUtils {
	private MathUtils(){
		
	}
	/**
	 * 
	 * @param length
	 * @return
	 */
	public static String M2KM(int length){
		double km = length*1.0/1000;
		
		BigDecimal b = new BigDecimal(km);
		double result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		return String.valueOf(result)+"公里";
	}
	
	public static String formatS(int second){
		
		int s = second % 60;
		int m = (second / 60)%60;
		int h = second /3600;
		if(h>0){
			return h+"小时"+m+"分钟";
		}else{
			return second/60+"分钟";
		}	
	}
}
