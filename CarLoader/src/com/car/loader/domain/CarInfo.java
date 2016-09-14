package com.car.loader.domain;

public class CarInfo {
	private String phone;
	private String carbrand;
	private String carsign;
	private String cartype;
	private String carnumber;
	private String carenginnumber;
	private String carbodylevel;
	private String carmileage;
	private String caroil;
	private String carenginstate;
	private String cartranstate;
	private String carlightstate;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCarbrand() {
		return carbrand;
	}
	public void setCarbrand(String carbrand) {
		this.carbrand = carbrand;
	}
	public String getCarsign() {
		return carsign;
	}
	public void setCarsign(String carsign) {
		this.carsign = carsign;
	}
	public String getCartype() {
		return cartype;
	}
	public void setCartype(String cartype) {
		this.cartype = cartype;
	}
	public String getCarnumber() {
		return carnumber;
	}
	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}
	public String getCarenginnumber() {
		return carenginnumber;
	}
	public void setCarenginnumber(String carenginnumber) {
		this.carenginnumber = carenginnumber;
	}
	public String getCarbodylevel() {
		return carbodylevel;
	}
	public void setCarbodylevel(String carbodylevel) {
		this.carbodylevel = carbodylevel;
	}
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
	@Override
	public String toString() {
		return "CarInfo [phone=" + phone + ", carbrand=" + carbrand + ", carsign=" + carsign + ", cartype=" + cartype
				+ ", carnumber=" + carnumber + ", carenginnumber=" + carenginnumber + ", carbodylevel=" + carbodylevel
				+ ", carmileage=" + carmileage + ", caroil=" + caroil + ", carenginstate=" + carenginstate
				+ ", cartranstate=" + cartranstate + ", carlightstate=" + carlightstate + "]";
	}
	
	
}
