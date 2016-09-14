package com.car.loader.ui;




import com.car.loader.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridAdapter extends BaseAdapter {

	private Context mContext;
	public String[] img_txt = {"个人信息","路线导航","车辆维护","附近加油站","我的车辆","我的音乐","违章查询","预定加油","设置"};
	public int[] imgs = {R.drawable.personinfo,R.drawable.route,R.drawable.maintenance,R.drawable.locatepetrol,R.drawable.carmaintaininfo,R.drawable.music,R.drawable.violation,R.drawable.petrolstation,R.drawable.setting};
	
	public MyGridAdapter(Context mContext){
		super();
		this.mContext = mContext;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		return imgs.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent,false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
		iv.setBackgroundResource(imgs[position]);
		tv.setText(img_txt[position]);
		return convertView;
	}

}
