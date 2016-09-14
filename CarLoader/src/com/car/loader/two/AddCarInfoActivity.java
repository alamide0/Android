package com.car.loader.two;

import com.car.loader.R;
import com.car.loader.domain.CarOrderBaseInfo;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;
import com.car.loader.util.MessageInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class AddCarInfoActivity extends Activity {
	protected static final int SUCCESS = 0;
	protected static final int FAILURE = 1;
	private EditText et_carnumber;
	private HeaderView h_header;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				String str = msg.obj.toString();
				Toast.makeText(AddCarInfoActivity.this, MessageInfo.getMessageInfo(AddCarInfoActivity.this, str), 0).show();
				if ("3001".equals(str)) {
					Intent intent = getIntent();
					intent.putExtra("carnumber", number);
					AddCarInfoActivity.this.setResult(1, intent);
					AddCarInfoActivity.this.finish();
				}
				break;
			case FAILURE:
				Toast.makeText(AddCarInfoActivity.this, "ÍøÂç´íÎó", 0).show();
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_carinfo);

		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddCarInfoActivity.this.finish();
			}
		});
		et_carnumber = (EditText) findViewById(R.id.et_carnumber);
	}

	private String number;

	public void addFinish(View view) {
		number = et_carnumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "²»ÄÜÎª¿Õ", 0).show();
			return;
		}
		final String phone = getSharedPreferences("config", MODE_PRIVATE).getString("phone", "");

		new Thread(new Runnable() {

			@Override
			public void run() {
				CarOrderBaseInfo info = new CarOrderBaseInfo();
				info.setCarnumber(number);
				info.setPhone(phone);
				Message msg = Message.obtain();
				try {
					String str = TalkWithInternet.toAddCarOilBaseInfo(info);
					// System.out.println(str);
					msg.what = SUCCESS;
					msg.obj = str;
				} catch (Exception e) {
					msg.what = FAILURE;

				} finally {
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

}
