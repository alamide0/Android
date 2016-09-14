package com.car.loader.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class SimpleInfo implements Serializable{
	private String carmileage;
	private String caroil;
	private String carenginstate;
	private String cartranstate;
	private String carlightstate;
	private String time;
	private String phone;
	
	
	public String getCarmileage() {
		return carmileage;
	}
	public void setCarmileage(String carmileage) {
		this.carmileage = carmileage;
	}
	public String getCaroil() {
		return caroil;
	}
	public void setCaroil(String caroil) {
		this.caroil = caroil;
	}
	public String getCarenginstate() {
		return carenginstate;
	}
	public void setCarenginstate(String carenginstate) {
		this.carenginstate = carenginstate;
	}
	public String getCartranstate() {
		return cartranstate;
	}
	public void setCartranstate(String cartranstate) {
		this.cartranstate = cartranstate;
	}
	public String getCarlightstate() {
		return carlightstate;
	}
	public void setCarlightstate(String carlightstate) {
		this.carlightstate = carlightstate;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
