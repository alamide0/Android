package com.car.loader.domain;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * 
 * @author Zsy
 *
 */
public class CarBaseInfo implements Serializable{
	private String carnumber;
	private String phone;
	private String carenginnumber;
	private Bitmap bitmap;
	private String carbrand;
	private String cartype;
	private String carbodylevel;
	public String getCarnumber() {
		return carnumber;
	}
	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCarenginnumber() {
		return carenginnumber;
	}
	public void setCarenginnumber(String carenginnumber) {
		this.carenginnumber = carenginnumber;
	}
	public String getCarbrand() {
		return carbrand;
	}
	public void setCarbrand(String carbrand) {
		this.carbrand = carbrand;
	}
	
	public String getCartype() {
		return cartype;
	}
	public void setCartype(String cartype) {
		this.cartype = cartype;
	}
	public String getCarbodylevel() {
		return carbodylevel;
	}
	public void setCarbodylevel(String carbodylevel) {
		this.carbodylevel = carbodylevel;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	
	
}
