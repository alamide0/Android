package com.car.loader.ui;

import com.car.loader.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	private TextView tv_parent;
	private TextView tv_child;
	private ImageView iv_turn;
	
	private String desc_on, desc_off, parent;

	public void initView(Context context){
		View view = View.inflate(context, R.layout.setting_item, this);
		tv_parent = (TextView) view.findViewById(R.id.tv_parent);
		tv_child = (TextView) view.findViewById(R.id.tv_child);
		iv_turn = (ImageView) view.findViewById(R.id.iv_turn);
	}
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		parent = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.car.loader", "parent");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.car.loader", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.car.loader", "desc_off");
		tv_parent.setText(parent);
		tv_child.setText(desc_off);
	}
	private boolean turn = false;
	public boolean getTurnState(){
		return turn;
	}
	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}
	
	public void setTurnOn(boolean b){
		if(b){
			iv_turn.setImageResource(R.drawable.btn_on);
			tv_child.setText(desc_on);
			turn = true;
		}else{
			iv_turn.setImageResource(R.drawable.btn_off);
			tv_child.setText(desc_off);
			turn = false;
		}
	}
	
}
