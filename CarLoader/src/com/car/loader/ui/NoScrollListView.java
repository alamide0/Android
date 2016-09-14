package com.car.loader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * 
 * 
 * 这个方法是重写ListView，使ListView没有滑动效果
 * **/
public class NoScrollListView extends ListView {

	public NoScrollListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
