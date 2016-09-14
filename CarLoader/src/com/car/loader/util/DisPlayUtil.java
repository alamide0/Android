package com.car.loader.util;

import android.content.Context;

public class DisPlayUtil {
	private DisPlayUtil(){
		
	}
	/**
	 * ÆÁÄ»ÊÊÅä×ª»» dip ×ª px
	 * @param context
	 * @param dipValue
	 * @return
	 */
	 public static int dip2px(Context context, float dipValue) {  
         final float scale = context.getResources().getDisplayMetrics().density;  
         return (int) (dipValue * scale + 0.5f);  
     }  
}
