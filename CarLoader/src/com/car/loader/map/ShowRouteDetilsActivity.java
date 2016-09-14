package com.car.loader.map;

import java.util.List;

import com.car.loader.R;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 显示路线规划的详情，即路上指导
 * @author Zsy
 *
 */
public class ShowRouteDetilsActivity extends Activity {
	private HeaderView h_header;
	private ListView lv_list;
	private List<String> list = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_route_detils);
		list = getIntent().getStringArrayListExtra("list");
		System.out.println("list2="+list.size());
		h_header = (HeaderView) findViewById(R.id.h_header);
		lv_list = (ListView) findViewById(R.id.lv_list);
		lv_list.setAdapter(new MyAdapter());
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowRouteDetilsActivity.this.finish();
			}
		});
		
		
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list==null?0:list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView == null){
				view = View.inflate(ShowRouteDetilsActivity.this, R.layout.item_route_detils, null);
				holder = new ViewHolder();
				holder.tv_text = (TextView) view.findViewById(R.id.tv_text);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			
			holder.tv_text.setText(list.get(position));
			return view;
		}
		

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
	
	static class ViewHolder{
		private TextView tv_text;
	}
	
}
