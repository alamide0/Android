package com.car.loader.two;

import com.car.loader.R;
import com.car.loader.db.dao.CarOrderOilDBDao;
import com.car.loader.domain.OrderOilInfo;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.util.MessageInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class OrderOilActivity extends Activity {
	
	protected static final int SUCCESS = 0;
	protected static final int FAILURE = 1;
	private TextView tv_carnumber;
	private EditText et_name;
	private EditText et_time;
	private EditText et_station;
	private RadioGroup rg;
	private RadioButton rg_button1;
	private RadioButton rg_button2;
	private RadioButton rg_button3;
	
	
	private EditText et_money;
	private DatePicker picker;
	private String phone;
	private String oiltype="93#";
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				String info  = msg.obj.toString();
				String str = MessageInfo.getMessageInfo(OrderOilActivity.this, info);
				Toast.makeText(OrderOilActivity.this, str, 0).show();
				if("3003".equals(info)){
					tv_carnumber.setText("");
					et_money.setText("");
					et_name.setText("");
					et_station.setText("");
					et_time.setText("");
					
				}
				break;
			case FAILURE:
				Toast.makeText(OrderOilActivity.this, "ÍøÂç´íÎó", 0).show();
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		phone = getSharedPreferences("config", MODE_PRIVATE).getString("phone", "");
		if(!getSharedPreferences("config", MODE_PRIVATE).getBoolean("isLogin", false)){
			OrderOilActivity.this.finish();
			Toast.makeText(this, "Î´µÇÂ½²»¿ÉÓÃ", 0).show();
			return;
		}
		setContentView(R.layout.activity_order_oil);
		
		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OrderOilActivity.this.finish();
			}
		});
		
		
		tv_carnumber = (TextView) findViewById(R.id.tv_carnumber);
		et_name = (EditText) findViewById(R.id.et_name);
		et_time = (EditText) findViewById(R.id.et_time);
		et_station = (EditText) findViewById(R.id.et_station);
		
		rg = (RadioGroup) findViewById(R.id.rg);
		rg_button1 = (RadioButton) findViewById(R.id.rg_button1);
		rg_button2 = (RadioButton) findViewById(R.id.rg_button2);
		rg_button3 = (RadioButton) findViewById(R.id.rg_button3);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.rg_button1){
					oiltype = "93#";
				}else if(checkedId==R.id.rg_button2){
					oiltype = "97#";
				}else if(checkedId==R.id.rg_button3){
					oiltype = "95#";
				}
			}
		});
		
		et_money = (EditText) findViewById(R.id.et_money);
		picker = new DatePicker(this);
		
		et_time.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyDatePickerTime pt = new MyDatePickerTime();
				DatePickerDialog dialog = new DatePickerDialog(OrderOilActivity.this, pt, picker.getYear(), picker.getMonth(), picker.getMonth());
				dialog.show();
				
			}
		});
	}
	public void selectTime(View view){
		MyDatePickerTime pt = new MyDatePickerTime();
		DatePickerDialog dialog = new DatePickerDialog(OrderOilActivity.this, pt, picker.getYear(), picker.getMonth(), picker.getMonth());
		dialog.show();
	}
	
	public void select(View view){
		Intent intent = new Intent(this,SelectCarInfoActivity.class);
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null){
			String carnumber = data.getStringExtra("carnumber");
			tv_carnumber.setText(carnumber);
		}
	}
	public void query(View view){
		Intent intent = new Intent(this,ShowAllOrdersActivity.class);
		startActivity(intent);
	}
	
	public void post(View view){
		String carnumber = tv_carnumber.getText().toString().trim();
		String name = et_name.getText().toString().trim();
		String time = et_time.getText().toString().trim();
		//String time = "2015-1-9";
		String station = et_station.getText().toString().trim();
		
		String money = et_money.getText().toString().trim();
		if(TextUtils.isEmpty(carnumber)||TextUtils.isEmpty(name)||TextUtils.isEmpty(time)||
				TextUtils.isEmpty(oiltype)||TextUtils.isEmpty(station)||TextUtils.isEmpty(money))
		{	
			Toast.makeText(this, "º¬ÓÐ¿ÕÏî", 0).show();
			return;
		}
		
		final OrderOilInfo info = new OrderOilInfo();
		info.set_time(time);
		info.setCarnumber(carnumber);
		info.setMoney(money);
		info.setOiltype(oiltype);
		info.setPhone(phone);
		info.setStation(station);
		info.setName(name);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					String str = TalkWithInternet.toStorageCarOilInfo(info);
					msg.what = SUCCESS;
					msg.obj = str;
				} catch (Exception e) {
					msg.what = FAILURE;
					e.printStackTrace();
				}finally{
					handler.sendMessage(msg);
				}
			}
		}).start();
		
		
		
		
		
	}
	public class MyDatePickerTime implements DatePickerDialog.OnDateSetListener{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			//System.out.println("+"+year+monthOfYear+dayOfMonth);
			picker.updateDate(year, monthOfYear, dayOfMonth);
			et_time.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
			
		}
		
	}
}
