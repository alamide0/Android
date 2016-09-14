package com.car.loader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CarOrderOilDB extends SQLiteOpenHelper {

	public CarOrderOilDB(Context context){
		super(context, "order.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table orderoil(_id integer primary key autoincrement, carnumber varchar(20),name varchar(20), _time varchar(20),station varchar(50),oil_type varchar(10),money varchar(20) ,phone varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
