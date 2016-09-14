package com.car.loader.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.car.loader.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class SlideShowView extends FrameLayout {

	// ʹ��universal-image-loader�����ȡ����ͼƬ����Ҫ���̵���universal-image-loader-1.8.6-with-sources.jar
	// private ImageLoader imageLoader = ImageLoader.getInstance();

	// �ֲ�ͼͼƬ����
	private final static int IMAGE_COUNT = 5;
	// �Զ��ֲ���ʱ����
	private final static int TIME_INTERVAL = 5;
	// �Զ��ֲ����ÿ���
	private final static boolean isAutoPlay = true;

	// �Զ����ֲ�ͼ����Դ
	private String[] imageUrls;
	// ���ֲ�ͼƬ��ImageView ��list
	private List<ImageView> imageViewsList;
	// ��Բ���View��list
	private List<View> dotViewsList;

	private ViewPager viewPager;
	// ��ǰ�ֲ�ҳ
	private int currentItem = 0;
	private View v_dot1, v_dot2, v_dot3;
	// ��ʱ����
	private ScheduledExecutorService scheduledExecutorService;

	private Context context;

	// Handler
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem);
			changeDot(currentItem);
		}

	};

	public SlideShowView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
		this.context = context;

		initData();
		if (isAutoPlay) {
			startPlay();
		}
	}

	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

		initData();
		if (isAutoPlay) {
			startPlay();
		}

	}

	/**
	 * ��ʼ�ֲ�ͼ�л�
	 */
	private void startPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
	}

	/**
	 * ֹͣ�ֲ�ͼ�л�
	 */
	private void stopPlay() {
		scheduledExecutorService.shutdown();
	}

	/**
	 * ��ʼ�����Data
	 */
	private void initData() {
		imageViewsList = new ArrayList<ImageView>();
		dotViewsList = new ArrayList<View>();
		initUI(context);
	}

	/**
	 * ��ʼ��Views��UI
	 */
	private void initUI(Context context) {
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);

		LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
		dotLayout.removeAllViews();
		v_dot1 = findViewById(R.id.v_dot1);
		v_dot2 = findViewById(R.id.v_dot2);
		v_dot3 = findViewById(R.id.v_dot3);
		for (int i = 0; i < 3; i++) {
			ImageView view = new ImageView(context);
			if (i == 0) {
				view.setImageResource(R.drawable.firstshow);
			} else if (i == 1) {
				view.setImageResource(R.drawable.secondshow);

			} else {
				view.setImageResource(R.drawable.thirdshow);
			}
			view.setScaleType(ScaleType.FIT_CENTER);
			imageViewsList.add(view);

		}

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);
		
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * ���ViewPager��ҳ��������
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			// ((ViewPag.er)container).removeView((View)object);
			((ViewPager) container).removeView(imageViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			// ImageView imageView = imageViewsList.get(position);

			// imageLoader.displayImage(imageView.getTag() + "", imageView);

			((ViewPager) container).addView(imageViewsList.get(position));
			return imageViewsList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

	public void changeDot(int nowPosition) {
		switch (nowPosition) {
		case 0:
			v_dot1.setBackgroundResource(android.R.drawable.presence_online);
			v_dot2.setBackgroundResource(android.R.drawable.presence_invisible);
			v_dot3.setBackgroundResource(android.R.drawable.presence_invisible);
			break;
		case 1:
			v_dot1.setBackgroundResource(android.R.drawable.presence_invisible);
			v_dot2.setBackgroundResource(android.R.drawable.presence_online);
			v_dot3.setBackgroundResource(android.R.drawable.presence_invisible);
			break;
		case 2:
			v_dot1.setBackgroundResource(android.R.drawable.presence_invisible);
			v_dot2.setBackgroundResource(android.R.drawable.presence_invisible);
			v_dot3.setBackgroundResource(android.R.drawable.presence_online);
			break;
		default:
			break;
		}
	}

	/**
	 * ViewPager�ļ����� ��ViewPager��ҳ���״̬�����ı�ʱ����
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			switch (arg0) {
			case 1:// ���ƻ�����������
				isAutoPlay = false;
				// System.out.println("�����л���");
				break;
			case 2:// �����л���
					// System.out.println("�����л����");
				isAutoPlay = true;
				break;
			case 0:// �������������л���ϻ��߼������
					// ��ǰΪ���һ�ţ���ʱ�������󻬣����л�����һ��
				//System.out.println("������");
				if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
					viewPager.setCurrentItem(0);
					// System.out.println("changeDot(0);");
					changeDot(0);
				}
				// ��ǰΪ��һ�ţ���ʱ�������һ������л������һ��
				else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
					viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
					changeDot(viewPager.getAdapter().getCount() - 1);
					// System.out.println("viewPager.getAdapter().getCount() -
					// 1");
				}
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int pos) {
			changeDot(pos);
		}

	}

	/**
	 * ִ���ֲ�ͼ�л�����
	 * 
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % 3;
				// System.out.println(currentItem+"==");
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	/**
	 * ����ImageView��Դ�������ڴ�
	 * 
	 */
	private void destoryBitmaps() {

		for (int i = 0; i < 3; i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null) {
				// ���drawable��view������
				drawable.setCallback(null);
			}
		}
	}

}