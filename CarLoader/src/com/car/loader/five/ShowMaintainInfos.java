package com.car.loader.five;

import java.util.List;

import com.car.loader.R;
import com.car.loader.domain.SimpleInfo;
import com.car.loader.internet.TalkWithInternet;
import com.car.loader.ui.HeaderView;
import com.car.loader.util.DisPlayUtil;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 显示该车牌号的车的所有维护信息
 * @author Zsy
 *
 */
public class ShowMaintainInfos extends Activity {
	private ListView lv_infos;
	private ProgressBar pb_load;
	private List<SimpleInfo> list = null; 
	private String carnumber;
	private String phone;
	private HeaderView h_header;
	private int mTouchSlop ;
	private float mFirstY,mCurrentY;
	private int direction;
	private boolean mShow;
	private ObjectAnimator mAnimator = null;
	private View.OnTouchListener myTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				mFirstY = event.getY();
				//System.out.println("--"+mFirstY);
				break;
			case MotionEvent.ACTION_MOVE:
				mCurrentY = event.getY();
				//System.out.println("--"+mCurrentY+"--");
				if((mCurrentY-mFirstY)>mTouchSlop){
					direction = 0;
				}else if((mFirstY-mCurrentY)>mTouchSlop){
					direction = 1;
				}
				//System.out.println("rirection="+direction);
				if(direction==1){
					if(mShow){
						headerViewAnim(1);
						mShow = !mShow;
						//System.out.println("zhixing up");
					}
				}else if(direction == 0){
					if(!mShow){
						headerViewAnim(0);
						mShow = !mShow;
						//System.out.println("zhixing down");
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			return false;
		}
	};
	
	private void headerViewAnim(int flag){
		if(mAnimator != null&& mAnimator.isRunning()){
			mAnimator.cancel();
		}
		//System.out.println(h_header.getHeight()+"00000000000");
		if(flag == 0){
			mAnimator = ObjectAnimator.ofFloat(h_header, "translationY", 
					h_header.getTranslationY(),0);
			//System.out.println("zhixing flag==0");
		}else{
			mAnimator = ObjectAnimator.ofFloat(h_header, "translationY", 
					h_header.getTranslationY(),-h_header.getHeight());
			//System.out.println("zhixing flag==1");
		}
		mAnimator.start();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_infos);
		
		
		mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
		mShow = true;
		h_header = (HeaderView) findViewById(R.id.h_header);
		h_header.getImageView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowMaintainInfos.this.finish();
			}
		});
		View header = new View(this);
		measureView(h_header);
		header.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, h_header.getMeasuredHeight()));
		header.setBackgroundColor(Color.parseColor("#00000000"));
		lv_infos = (ListView) findViewById(R.id.lv_infos);
		lv_infos.setOnTouchListener(myTouchListener);
		lv_infos.addHeaderView(header);
		
		pb_load = (ProgressBar) findViewById(R.id.pb_load);
		carnumber = getIntent().getExtras().getString("carnumber");
		phone = getIntent().getExtras().getString("phone");
		new Thread(new Runnable() {
			public void run() {
				try {
					list = TalkWithInternet.toGetMaintainInfos(carnumber, "20");
					
					runOnUiThread(new  Runnable() {
						public void run() {
							if(list==null){
								Toast.makeText(ShowMaintainInfos.this, "登陆过期，请重新登陆", 0).show();
								Intent i = new Intent();
								i.setAction("com.car.loader.logout");
								i.addCategory("android.intent.category.DEFAULT");
								sendBroadcast(i);
							}else{
								lv_infos.setAdapter(new MyAdapter());
							}
							
							pb_load.setVisibility(View.INVISIBLE);
							lv_infos.setVisibility(View.VISIBLE);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView==null){
				view = View.inflate(ShowMaintainInfos.this, R.layout.item_listview_infos, null);
				holder = new ViewHolder();
				holder.iv_engin = (ImageView) view.findViewById(R.id.iv_engin);
				holder.iv_light = (ImageView) view.findViewById(R.id.iv_light);
				holder.iv_trans = (ImageView) view.findViewById(R.id.iv_trans);
				holder.tv_oil = (TextView) view.findViewById(R.id.tv_oil);
				holder.tv_mileage = (TextView) view.findViewById(R.id.tv_mileage);
				holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			SimpleInfo t = list.get(position);
			if(!t.getPhone().equals(phone)){
				holder.tv_mileage.setTextColor(Color.RED);
				holder.tv_oil.setTextColor(Color.RED);
				holder.tv_time.setTextColor(Color.RED);
				holder.tv_mileage.setText(t.getCarmileage()+"KM");
				holder.tv_oil.setText(t.getCaroil());
				holder.tv_time.setText(t.getTime());
			}else{
				holder.tv_mileage.setTextColor(Color.BLACK);
				holder.tv_oil.setTextColor(Color.BLACK);
				holder.tv_time.setTextColor(Color.BLACK);
				holder.tv_mileage.setText(t.getCarmileage()+"KM");
				holder.tv_oil.setText(t.getCaroil());
				holder.tv_time.setText(t.getTime());
			}
			
			holder.iv_engin.setImageResource("1".equals(t.getCarenginstate())?R.drawable.good:R.drawable.sad);
			holder.iv_light.setImageResource("1".equals(t.getCarlightstate())?R.drawable.good:R.drawable.sad);
			holder.iv_trans.setImageResource("1".equals(t.getCartranstate())?R.drawable.good:R.drawable.sad);
			return view;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
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
	
	private static class ViewHolder{
		private View view ;
		private TextView tv_time ;
		private TextView tv_mileage ;
		private TextView tv_oil ;
		private ImageView iv_light ;
		private ImageView iv_trans ;
		private ImageView iv_engin ;
		
	}
	private void measureView(View v) {
		ViewGroup.LayoutParams p = v.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int height;
		int tempheight = p.height;
		if (tempheight > 0) {
			height = MeasureSpec.makeMeasureSpec(p.height, MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		v.measure(width, height);
	}
	
}
