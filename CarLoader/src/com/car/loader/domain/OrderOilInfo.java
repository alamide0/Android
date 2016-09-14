package com.car.loader.domain;

public class OrderOilInfo {
	private String phone;
	private String carnumber;
	private int _id;
	private String name;
	private String _time;
	private String station;
	private String oiltype;
	private String money;
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String get_time() {
		return _time;
	}
	public void set_time(String _time) {
		this._time = _time;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getOiltype() {
		return oiltype;
	}
	public void setOiltype(String oiltype) {
		this.oiltype = oiltype;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
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
	
	
}
