package com.car.loader.two;

import java.util.List;

import com.car.loader.R;
import com.car.loader.db.dao.CarOrderOilDBDao;
import com.car.loader.domain.OrderOilInfo;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShowAllOrdersActivity extends Activity {
	protected static final int SUCCESS = 0;
	protected static final int FAILURE = 1;
	private ListView lv_list;
	private List<OrderOilInfo> list;
	
	private TextView tv_header;
	private HeaderView h_header;
	private String phone;
	private ProgressBar pb_loader;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				if(list==null){
					Toast.makeText(ShowAllOrdersActivity.this, "登陆过期，请重新登陆", 0).show();
					Intent i = new Intent();
					i.setAction("com.car.loader.logout");
					i.addCategory("android.intent.category.DEFAULT");
					sendBroadcast(i);
					return;
				}
				pb_loader.setVisibility(View.INVISIBLE);
				tv_header.setText("你一共有" + list.size() + "个订单");

				lv_list.setAdapter(new MyAdapter());
				lv_list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						OrderOilInfo info = list.get(position);
						String str = info.getCarnumber() + "," + info.getName() + "," + info.get_time() + ","
								+ info.getStation() + "," + info.getOiltype() + "," + info.getMoney();
						Intent intent = new Intent(ShowAllOrdersActivity.this,ShowOrderDetilsActivity.class);
						intent.putExtra("info", str);
						startActivity(intent);
						
					}
				});
				break;
			case FAILURE:
				pb_loader.setVisibility(View.INVISIBLE);
				Toast.makeText(ShowAllOrdersActivity.this, "网络错误", 0).show();
				break;

			default:
				break;
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detil);
		pb_loader = (ProgressBar) findViewById(R.id.pb_loader);
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowAllOrdersActivity.this.finish();
			}
		});
		
		tv_header = (TextView) findViewById(R.id.tv_header);
		lv_list = (ListView) findViewById(R.id.lv_list);
		
		phone = getSharedPreferences("config", MODE_PRIVATE).getString("phone", "");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					list = TalkWithInternet.toQueryStorageCarOilInfo(phone);
					msg.what = SUCCESS;
				} catch (Exception e) {
					msg.what = FAILURE;
					e.printStackTrace();
				}finally{
					handler.sendMessage(msg);
				}
				
			}
		}).start();
		
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list == null ? 0 : list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(ShowAllOrdersActivity.this, R.layout.item_order, null);
				holder = new ViewHolder();
				holder.tv_carnumber = (TextView) view.findViewById(R.id.tv_carnumber);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.tv_carnumber.setText(list.get(position).getCarnumber());
			holder.tv_name.setText(list.get(position).getName());
			holder.tv_time.setText(list.get(position).get_time());

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

	static class ViewHolder {
		private TextView tv_time;
		private TextView tv_name;
		private TextView tv_carnumber;
	}

}
