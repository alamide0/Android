package com.car.loader.map;

import com.baidu.mapapi.SDKInitializer;
import com.thinkland.sdk.android.JuheSDKInitializer;

import android.app.Application;

public class SdkApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		
		//com.thinkland.sdk.android.SDKInitializer.initialize(getApplicationContext());
		JuheSDKInitializer.initialize(getApplicationContext());
		SDKInitializer.initialize(this);
	}
}
