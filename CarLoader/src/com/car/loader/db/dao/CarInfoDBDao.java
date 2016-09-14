package com.car.loader.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.car.loader.db.CarInfoDB;
import com.car.loader.domain.CarBaseInfo;
import com.car.loader.domain.CarOrderBaseInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CarInfoDBDao {

	private CarInfoDB helper = null;
	public CarInfoDBDao(Context context){
		helper = new CarInfoDB(context);
	}
	
	public void insert(CarOrderBaseInfo info){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", info.getPhone());
		values.put("carnumber", info.getCarnumber());
		db.insert("carinfo", null, values);
		db.close();
	}
	
	public void delete(CarOrderBaseInfo info){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("carinfo", "carnumber=?", new String[]{info.getCarnumber()});
		db.close();
	}
	
	public List<CarOrderBaseInfo> query(String phone){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("carinfo", new String[]{"_id","phone","carnumber"}, "phone=?", new String[]{phone}, null, null, null);
		if(cursor==null){
			return null;
		}
		List<CarOrderBaseInfo> list = new ArrayList<CarOrderBaseInfo>();
		
		while(cursor.moveToNext()){
			CarOrderBaseInfo info = new CarOrderBaseInfo();
			info.set_id(cursor.getInt(0));
			info.setPhone(cursor.getString(1));
			info.setCarnumber(cursor.getString(2));
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}
	
}
