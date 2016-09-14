package com.car.loader.util;

public class AnswerInfo {
	private AnswerInfo(){
		
	}
	/**
	 * 根据状态码判断车辆的异常信息，并用文本表示
	 * @param t 车辆状态信息码
	 * @return
	 */
	public static String answerInfo(int t){
		StringBuffer buffer = new StringBuffer();
		//System.out.println("t======"+t+"7&16="+(7&16));
		if ((t & 1) == 1) {
			buffer.append("汽油量不足20%，请及时加油哦\n");
		}
		if ((t & 2) == 2) {
			buffer.append("超过15000KM，请及时保养车辆哦\n");
		}
		if ((t & 4) == 4) {
			buffer.append("发动机异常，请及时维修哦\n");
		}
		if ((t & 8) == 8) {
			buffer.append("制动器异常，请及时维修哦\n");
		}
		if ((t & 16) == 16) {
			//System.out.println("zhi-------------------------");
			buffer.append("车灯异常，请及时维修哦\n");
		}
		
		return buffer.toString();
	}
}
