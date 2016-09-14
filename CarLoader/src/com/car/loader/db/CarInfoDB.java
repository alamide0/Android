package com.car.loader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CarInfoDB extends SQLiteOpenHelper {

	public CarInfoDB(Context context){
		super(context, "carinfo.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table carinfo(_id integer primary key autoincrement, phone varchar(15), carnumber varchar(20))");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
