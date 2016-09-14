package com.car.loader.two;

import java.util.List;

import com.car.loader.R;
import com.car.loader.db.dao.CarInfoDBDao;
import com.car.loader.domain.CarOrderBaseInfo;
import com.car.loader.internet.TalkWithInternet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SelectCarInfoActivity extends Activity {

	protected static final int SUCCESS = 0;
	protected static final int FAILURE = 1;
	private ListView lv_list;
	private CarInfoDBDao dao;
	private ProgressBar pb_loader;
	
	private List<String> lst;
	private String phone;
	private MyAdapter adapter;
	private TextView tv_header;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				pb_loader.setVisibility(View.INVISIBLE);
				if(lst==null){
					Toast.makeText(SelectCarInfoActivity.this, "登陆过期，请重新登陆", 0).show();
					Intent i = new Intent();
					i.setAction("com.car.loader.logout");
					i.addCategory("android.intent.category.DEFAULT");
					sendBroadcast(i);
					return;
				}
				if (lst.size() > 0)
					tv_header.setText("你一共有" + lst.size() + "辆车");
				else
					tv_header.setText("你一共有" + lst.size() + "辆车,快去添加吧!");
				adapter = new MyAdapter();
				lv_list.setAdapter(adapter);
				lv_list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						String carnumber = lst.get(position).toString();
						Intent intent = SelectCarInfoActivity.this.getIntent();
						intent.putExtra("carnumber", carnumber);
						SelectCarInfoActivity.this.setResult(1, intent);
						SelectCarInfoActivity.this.finish();
					}
				});
				break;
			case FAILURE:
				pb_loader.setVisibility(View.INVISIBLE);
				Toast.makeText(SelectCarInfoActivity.this, "网络错误", 0).show();
				break;
				
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);

		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SelectCarInfoActivity.this.finish();
			}
		});
		lv_list = (ListView) findViewById(R.id.lv_list);
		tv_header = (TextView) findViewById(R.id.tv_header);
		pb_loader = (ProgressBar) findViewById(R.id.pb_loader);
		dao = new CarInfoDBDao(this);
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		phone = sp.getString("phone", "");
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					lst = TalkWithInternet.toQueryCarOilBaseInfo(phone);
					msg.what = SUCCESS;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = FAILURE;
				}finally{
					handler.sendMessage(msg);
				}
			}
		}).start();
		
		
	}

	public void add(View view) {
		Intent intent = new Intent(this, AddCarInfoActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			String number = data.getStringExtra("carnumber");			
			lst.add(number);
			tv_header.setText("你一共有" + lst.size() + "辆车");
			adapter.notifyDataSetChanged();
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lst == null ? 0 : lst.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(SelectCarInfoActivity.this, R.layout.item_select, null);
				holder = new ViewHolder();
				holder.tv_select = (TextView) view.findViewById(R.id.tv_select);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.tv_select.setText(lst.get(position).toString());
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	static class ViewHolder {
		private TextView tv_select;
	}

}
