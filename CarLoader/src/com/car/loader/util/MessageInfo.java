package com.car.loader.util;

import com.car.loader.R;

import android.content.Context;

public class MessageInfo {
	private MessageInfo(){
		
	}
	
	public static String getMessageInfo(Context context,String str){
		if("1001".equals(str)){
			return context.getResources().getString(R.string._1001);
		}else if("1002".equals(str)){
			return context.getResources().getString(R.string._1002);
		}else if("1003".equals(str)){
			return context.getResources().getString(R.string._1003);
		}else if("1004".equals(str)){
			return context.getResources().getString(R.string._1004);
		}else if("1005".equals(str)){
			return context.getResources().getString(R.string._1005);
		}else if("1006".equals(str)){
			return context.getResources().getString(R.string._1006);
		}else if("1007".equals(str)){
			return context.getResources().getString(R.string._1007);
		}else if("1008".equals(str)){
			return context.getResources().getString(R.string._1008);
		}else if("1009".equals(str)){
			return context.getResources().getString(R.string._1009);
		}else if("1010".equals(str)){
			return context.getResources().getString(R.string._1010);
		}else if("1011".equals(str)){
			return context.getResources().getString(R.string._1011);
		}else if("1012".equals(str)){
			return context.getResources().getString(R.string._1012);
		}else if("1013".equals(str)){
			return context.getResources().getString(R.string._1013);
		}else if("1014".equals(str)){
			return context.getResources().getString(R.string._1014);
		}else if("1015".equals(str)){
			return context.getResources().getString(R.string._1015);
		}else if("1016".equals(str)){
			return context.getResources().getString(R.string._1016);
		}else if("3001".equals(str)){
			return context.getResources().getString(R.string._3001);
		}else if("3002".equals(str)){
			return context.getResources().getString(R.string._3002);
		}else if("3003".equals(str)){
			return context.getResources().getString(R.string._3003);
		}else if("0001".equals(str)){
			return context.getResources().getString(R.string._0001);
		}else if("0002".equals(str)){
			return context.getResources().getString(R.string._0002);
		}
		
		return "ÍøÂç´íÎó£¡";
	}
}
