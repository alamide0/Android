package com.car.loader.map;

import com.car.loader.R;
import com.car.loader.ui.HeaderView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 输入起始地点和目标地点
 * @author Zsy
 *
 */
public class SelectStartAndEndPlaceActivity extends Activity implements OnClickListener {
	private TextView tv_start;
	private TextView tv_end;
	private ImageView iv_swap;
	private HeaderView h_header;
	private String nowPlace, nowCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_select);

		nowCity = getIntent().getStringExtra("city");
		nowPlace = getIntent().getStringExtra("place");
		// Toast.makeText(this, nowPlace, 0).show();
		startNode = nowPlace;
		endNode = "";

		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SelectStartAndEndPlaceActivity.this.finish();
			}
		});
		tv_end = (TextView) findViewById(R.id.tv_end);
		tv_start = (TextView) findViewById(R.id.tv_start);
		iv_swap = (ImageView) findViewById(R.id.iv_swap);
		tv_end.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		iv_swap.setOnClickListener(this);
	}

	private String startNode;
	private String endNode;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_end:

			break;
		case R.id.tv_start:

			break;
		case R.id.iv_swap:
			String start = tv_start.getText().toString().trim();
			String end = tv_end.getText().toString().trim();

			if (TextUtils.isEmpty(startNode)) {

				tv_end.setText("输入终点");
				tv_end.setTextColor(0x77000000);

				tv_start.setText(end);
				tv_start.setTextColor(0xff0a7dd2);

			} else if (TextUtils.isEmpty(endNode)) {
				tv_start.setText("输入起点");
				tv_start.setTextColor(0x77000000);
				tv_end.setText(start.equals("输入起点") ? "输入终点" : start);
				tv_end.setTextColor(0xff0a7dd2);
			} else {
				tv_start.setText(end);
				tv_start.setTextColor(0xff0a7dd2);
				tv_end.setText(start);
				tv_end.setTextColor(0xff0a7dd2);
			}
			String temp = startNode;

			startNode = endNode;
			endNode = temp;
			break;
		default:
			break;
		}
	}

	public void search(View view) {
		if (TextUtils.isEmpty(startNode) || TextUtils.isEmpty(endNode)) {
			Toast.makeText(this, "含有空项！！", 0).show();
			return;
		}
		String start = tv_start.getText().toString().trim();
		String end = tv_end.getText().toString().trim();

		Intent intent = new Intent(this, DriveRoutePlan.class);
		intent.putExtra("city", nowCity);
		intent.putExtra("start", startNode);
		intent.putExtra("end", endNode);
		intent.putExtra("locLat", getIntent().getDoubleExtra("locLat", 0));
		intent.putExtra("locLon", getIntent().getDoubleExtra("locLon", 0));
		if (start.equals("当前位置")) {
			intent.putExtra("useStart", true);
		
		}
		
		if(end.equals("当前位置")){
			intent.putExtra("useEnd", true);
		}
		startActivity(intent);
	}

	public void startPlace(View view) {
		// Toast.makeText(this, "start", 0).show();
		Intent intent = new Intent(this, SuggestionResultActivity.class);
		startActivityForResult(intent, 0);
	}

	public void endPlace(View view) {
		// Toast.makeText(this, "end", 0).show();

		Intent intent = new Intent(this, SuggestionResultActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Toast.makeText(this, "requestCode="+requestCode+":"+resultCode,
		// 0).show();
		if (resultCode == 1) {
			if (requestCode == 1) {
				tv_end.setText(data.getStringExtra("place"));
				tv_end.setTextColor(0xff0a7dd2);
				endNode = data.getStringExtra("place");
			} else if (requestCode == 0) {

				tv_start.setText(data.getStringExtra("place"));
				startNode = data.getStringExtra("place");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}
}
