package com.car.loader.two;

import com.car.loader.R;
import com.car.loader.ui.HeaderView;
import com.car.loader.util.DisPlayUtil;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ShowCodeInfoActivity extends Activity {
	private ImageView iv_code;
	private HeaderView h_header;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_code);
		
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowCodeInfoActivity.this.finish();
			}
		});
		iv_code = (ImageView) findViewById(R.id.iv_code);
		String str = getIntent().getStringExtra("info");
		try {
			Bitmap map = EncodingHandler.createQRCode(str, DisPlayUtil.dip2px(ShowCodeInfoActivity.this, 400));
			iv_code.setImageBitmap(map);
		} catch (WriterException e) {
			
			e.printStackTrace();
		}
	}
}
