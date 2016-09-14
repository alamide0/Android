package com.car.loader.ui;



import com.car.loader.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HeaderView extends RelativeLayout {

	private TextView tv_header;
	private ImageView iv_back;
	private String title;
	public HeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.car.loader", "title");
		tv_header.setText(title);
	}

	public HeaderView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context){
		View view = View.inflate(context, R.layout.header, this);
		tv_header = (TextView) view.findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		
	}
	
	public ImageView getImageView(){
		return iv_back;
	}
	
	public void setTxt(String str){
		tv_header.setText(str);
	}
}
