package com.car.loader.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.car.loader.db.CarOrderOilDB;
import com.car.loader.domain.OrderOilInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CarOrderOilDBDao {
	private CarOrderOilDB helper;
	public CarOrderOilDBDao(Context context){
		helper = new CarOrderOilDB(context);
	}
	
	public void insert(OrderOilInfo info){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("carnumber", info.getCarnumber());
		values.put("name", info.getName());
		values.put("_time", info.get_time());
		values.put("station", info.getStation());
		values.put("oil_type", info.getOiltype());
		values.put("money", info.getMoney());
		values.put("phone", info.getPhone());
		db.insert("orderoil", null, values);
		db.close();
	}
	
	public void delete(int _id){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("orderoil",  "_id=?",new String[]{_id+""});
		db.close();
	}
	
	public List<OrderOilInfo> query(String phone){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("orderoil", null, "phone=?", new String[]{phone}, null, null, null);
		List<OrderOilInfo> list = new ArrayList<OrderOilInfo>();
		while(cursor.moveToNext()){
			OrderOilInfo info = new OrderOilInfo();
			info.set_id(cursor.getInt(0));
			info.setCarnumber(cursor.getString(1));
			info.setName(cursor.getString(2));
			info.set_time(cursor.getString(3));
			info.setStation(cursor.getString(4));
			info.setOiltype(cursor.getString(5));
			info.setMoney(cursor.getString(6));
			info.setPhone(cursor.getString(7));
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}
}
