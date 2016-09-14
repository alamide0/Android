package com.car.loader.map;

import java.util.List;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.car.loader.R;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 建议请求结果查询，百度提供的一个功能，可以帮助我们更好的实现路线规划
 * @author Zsy
 *
 */
public class SuggestionResultActivity extends Activity implements TextWatcher,OnGetSuggestionResultListener{
	
	private AutoCompleteTextView actv_search;
	private ListView lv_list;
	private SuggestionSearch suggestion;
	private String city;
	private MyAdapter adapter;
	private HeaderView h_header;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_suggestion);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SuggestionResultActivity.this.finish();
			}
		});
		city = getIntent().getStringExtra("city");
		
		lv_list = (ListView) findViewById(R.id.lv_list);
		adapter = new MyAdapter();
		lv_list.setAdapter(adapter);
		
		lv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String str = infos.get(position).key;
				Intent intent = SuggestionResultActivity.this.getIntent();
				intent.putExtra("place", str);
				SuggestionResultActivity.this.setResult(1, intent);
				SuggestionResultActivity.this.finish();
			}
		});
		
		suggestion = SuggestionSearch.newInstance();
		suggestion.setOnGetSuggestionResultListener(this);
		
		actv_search = (AutoCompleteTextView) findViewById(R.id.actv_search);
		lv_list = (ListView) findViewById(R.id.lv_list);
		actv_search.addTextChangedListener(this);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String str = s.toString();
		suggestion.requestSuggestion((new SuggestionSearchOption()).keyword(str).city(city==null?"":city));
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}
	private List<SuggestionInfo> infos = null;
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if(res==null||res.getAllSuggestions()==null){
			return;
		}
		infos = res.getAllSuggestions();
		adapter.notifyDataSetChanged();
		
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return infos==null?0:infos.size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView==null)
			{
				view = View.inflate(SuggestionResultActivity.this, R.layout.item_map_list, null);
				holder = new ViewHolder();
				holder.tv_place = (TextView) view.findViewById(R.id.tv_place);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			if(infos.get(position).key!=null)
				holder.tv_place.setText(infos.get(position).key);
			else
				holder.tv_place.setText("");
			return view;
		}
		@Override
		public Object getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

			
	}
	@Override
	protected void onDestroy() {
		suggestion.destroy();
		super.onDestroy();
	}
	
	private static class ViewHolder{
		private TextView tv_place;
	}
}
