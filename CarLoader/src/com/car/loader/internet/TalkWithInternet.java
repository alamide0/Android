package com.car.loader.internet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.car.loader.domain.CarBaseInfo;
import com.car.loader.domain.CarInfo;
import com.car.loader.domain.CarOrderBaseInfo;
import com.car.loader.domain.OrderOilInfo;
import com.car.loader.domain.SimpleInfo;
import com.car.loader.util.StreamTools;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public  class TalkWithInternet {
	 private static String preUrl = "http://139.129.53.67/CarLoader";
	//private static String preUrl = "http://10.0.2.2/CarLoader";

	private static Context context;
	
	private TalkWithInternet(){
		
	}
	/**
	 * 传递上下文，用来获取SP中保存的用户名，密码等信息
	 * @param context APP上下文
	 */
	public  static void setContext(Context context){
		TalkWithInternet.context = context;
	}
	
	/**
	 * 获取注册用的验证码
	 * @param phone 手机号
	 * @return 服务器对于本次请求的处理结果
	 * @throws Exception 向上抛出网络请求过程中可能出现的异常，比如网络超时等
	 */
	public static String toGetPassCode(String phone) throws Exception {
		String data = "phone=" + phone;
		String url = preUrl + "/servlet/GetPassCodeServlet";
		return samePart(url, data);

	}

	/**
	 * 到服务器中注册
	 * 
	 * @param phone
	 *            手机号码
	 * @param passcode
	 *            填入的验证码
	 * @return 是否注册成功的信息，String
	 * @throws Exception
	 */
	public static String toRegister(String phone, String passcode) throws Exception {
		String data = "phone=" + phone + "&passcode=" + passcode;
		String url = preUrl + "/servlet/RegisterServlet";
		return samePart(url, data);
	}
	/**
	 * 验证码验证成功之后进行的操作，去注册用户用
	 * @param phone 手机号
	 * @param username 用户昵称
	 * @param password 用户密码
	 * @return
	 * @throws Exception
	 */
	public static String toRegisterUser(String phone, String username, String password) throws Exception {
		String data = "phone=" + phone + "&username=" + username + "&password=" + password;
		String url = preUrl + "/servlet/RegisterUserServlet";
		return samePart(url, data);
	}
	/**
	 * 登陆用户用
	 * @param phone 手机号
	 * @param password 密码
	 * @return 是否登陆成功的结果
	 * @throws Exception
	 */
	public static String toLogin(String phone, String password) throws Exception {
		String data = "phone=" + phone + "&password=" + password;
		String url = preUrl + "/servlet/LoginServlet";
		return samePart(url, data);
	}
	/**
	 * 修改密码
	 * @param phone 手机号
	 * @param password 新密码
	 * @param username 用户昵称
	 * @param olderPwd 旧密码
	 * @return 返回服务器处理结果
	 * @throws Exception
	 */
	public static String toUpdatePwd(String phone, String password, String username, String olderPwd) throws Exception {
		String data = "phone=" + phone + "&password=" + password + "&username=" + username + "&olderpwd=" + olderPwd;
		// System.out.println(olderPwd);
		// System.out.println();
		String url = preUrl + "/servlet/UpdatePwdServlet";
		return samePart(url, data);
	}
	/**
	 * 忘记密码操作时用来获取验证码
	 * @param phone 手机号
	 * @return 服务器处理的结果
	 * @throws Exception
	 */
	public static String toGetPCodeForForget(String phone) throws Exception {
		String data = "phone=" + phone;
		String url = preUrl + "/servlet/ForgetPwdServlet";
		return samePart(url, data);
	}
	/**
	 * 去获取用户的昵称
	 * @param phone
	 * @return
	 * @throws Exception
	 */
	public static String toGetNickName(String phone) throws Exception {
		String data = "phone=" + phone;
		String url = preUrl + "/servlet/SupplyNickNameServlet";
		return samePart(url, data);
	}
	/**
	 * 检测用户输入的验证码是否和服务器中保存的验证码是否相同
	 * @param phone
	 * @param passcode
	 * @return
	 * @throws Exception
	 */
	public static String toCheckPCodeForForget(String phone, String passcode) throws Exception {
		String data = "phone=" + phone + "&passcode=" + passcode;
		String url = preUrl + "/servlet/CheckPCodeServlet";
		return samePart(url, data);
	}
	/**
	 * 重新设置密码
	 * @param phone
	 * @param password
	 * @param passcode
	 * @return
	 * @throws Exception
	 */
	public static String toResetPwd(String phone, String password, String passcode) throws Exception {
		String data = "phone=" + phone + "&password=" + password + "&passcode=" + passcode;
		String url = preUrl + "/servlet/ReSetPasswordServlet";
		return samePart(url, data);
	}
	/**
	 * 向服务器中维护车辆的基本信息
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public static String toMaintainBaseCarInfo(CarInfo info) throws Exception {
		String data = "phone=" + info.getPhone() + "&carnumber=" + info.getCarnumber() + "&carenginnumber="
				+ info.getCarenginnumber() + "&carbrand=" + info.getCarbrand() + "&carsign=" + info.getCarsign()
				+ "&cartype=" + info.getCartype() + "&carbodylevel=" + info.getCarbodylevel();
		// System.out.println(data);
		String url = preUrl + "/servlet/MaintainCarBaseInfoServlet";
		return samePart(url, data);
	}
	/**
	 * 去维护车辆的状态信息
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public static String toMaintainCarMaintainInfo(CarInfo info) throws Exception {
		String data = "carnumber=" + info.getCarnumber() + "&carmileage=" + info.getCarmileage() + "&caroil="
				+ info.getCaroil() + "&carenginstate=" + info.getCarenginstate() + "&cartranstate="
				+ info.getCartranstate() + "&carlightstate=" + info.getCarlightstate() + "&phone=" + info.getPhone();
		String url = preUrl + "/servlet/MaintainCarMaintainInfoServlet";

		return samePart(url, data);
	}
	/**
	 * 根据图片的URL加载图片资源
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Bitmap toGetBitmap(String str) throws Exception {
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		InputStream in = null;

		if (conn.getResponseCode() == 200) {
			in = conn.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (in != null) {
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.close();
				in.close();
			}
			byte[] b = out.toByteArray();
			Bitmap map = BitmapFactory.decodeByteArray(b, 0, b.length);
			return map;
		}
		return null;

	}
/**
 * 获取车辆的维护信息
 * @param carnumber
 * @param count
 * @return
 * @throws Exception
 */
	public static List<SimpleInfo> toGetMaintainInfos(String carnumber, String count) throws Exception {
		String data = "carnumber=" + carnumber + "&count=" + count;
		String url = preUrl + "/servlet/SupplyMaintainInfoServlet";
		String result = samePart(url, data);
		if(result.equals("0001")||result.equals("0002")){
			return null;
		}
		List<SimpleInfo> list = new ArrayList<SimpleInfo>();
		JSONArray array = new JSONArray(result);
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = new JSONObject(array.get(i).toString());
			SimpleInfo info = new SimpleInfo();
			info.setTime(obj.getString("time"));
			String oil = obj.getString("caroil");
			Double d = Double.valueOf(oil);
			int t = (int) (d * 100);
			info.setCaroil(t + "%");
			info.setCartranstate(obj.getString("cartranstate"));
			info.setCarlightstate(obj.getString("carlightstate"));
			info.setCarmileage(obj.getString("carmileage"));
			info.setCarenginstate(obj.getString("carenginstate"));
			info.setPhone(obj.getString("phone"));
			list.add(info);
		}
		return list;
	}
/**
 * 去获得本手机号所绑定的所有车辆的基本信息
 * @param phone
 * @return
 * @throws Exception
 */
	public static List<CarBaseInfo> toGetCarBaseInfo(String phone) throws Exception {
		String url = preUrl + "/servlet/SupplyCarBaseInfo";
		String data = "phone=" + phone;
		String result = samePart(url, data);
		if(result.equals("0001")||result.equals("0002")){
			return null;
		}
		List<CarBaseInfo> list = new ArrayList<CarBaseInfo>();
		JSONArray array = new JSONArray(result);
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = new JSONObject(array.get(i).toString());
			CarBaseInfo info = new CarBaseInfo();
			info.setCarbodylevel(obj.getString("carbodylevel"));
			info.setCarbrand(obj.getString("carbrand"));
			info.setCarenginnumber(obj.getString("carenginnumber"));
			info.setCarnumber(obj.getString("carnumber"));
			Bitmap map = toGetBitmap(obj.getString("carsign"));
			//Bitmap map = toGetBitmap("http://10.0.2.2/CarLoader/dazhong.bmp");
			info.setBitmap(map);
			info.setCartype(obj.getString("cartype"));
			info.setPhone(obj.getString("phone"));
			list.add(info);
		}
		return list;
	}
	/**
	 * 去存储加油订单的信息
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public static String toStorageCarOilInfo(OrderOilInfo info) throws Exception {

		String url = preUrl + "/servlet/CarOilOrderInfoStorageServlet";
		String data = "carnumber=" + info.getCarnumber() + "&name=" + info.getName() + "&ordertime=" + info.get_time()
				+ "&station=" + info.getStation() + "&oil_type=" + info.getOiltype() + "&money=" + info.getMoney()
				+ "&phone=" + info.getPhone();
		return samePart(url, data);
	}
/**
 * 查询订单
 * @param phone
 * @return
 * @throws Exception
 */
	public static List<OrderOilInfo> toQueryStorageCarOilInfo(String phone) throws Exception {
		String url = preUrl + "/servlet/CarOilOrderInfoStorageQuery";
		String data = "phone=" + phone;
		String str = samePart(url, data);
		if(str.equals("0001")||str.equals("0002")){
			return null;
		}
		List<OrderOilInfo> list = new ArrayList<OrderOilInfo>();
		JSONArray array = new JSONArray(str);
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = new JSONObject(array.get(i).toString());
			OrderOilInfo info = new OrderOilInfo();
			info.set_time(object.optString("ordertime"));
			info.setCarnumber(object.optString("carnumber"));
			info.setMoney(object.optString("money"));
			info.setName(object.optString("name"));
			info.setOiltype(object.optString("oil_type"));
			info.setPhone(object.optString("phone"));
			info.setStation(object.optString("station"));
			list.add(info);
		}
		return list;
	}
/**
 * 获取车辆的基本信息根据车牌号
 * @param carnumber
 * @return
 * @throws Exception
 */
	public static CarBaseInfo toGetBaseInfoByCarNumber(String carnumber) throws Exception {
		String url = preUrl + "/servlet/SupplyCarBaseInfoByCarNumber";
		String data = "carnumber=" + carnumber;
		String result = samePart(url, data);
		if(result.equals("0001")||result.equals("0002")){
			return null;
		}
		// System.out.println("result="+result);
		JSONObject obj = new JSONObject(result);
		CarBaseInfo info = new CarBaseInfo();
		info.setCarbodylevel(obj.getString("carbodylevel"));
		info.setCarbrand(obj.getString("carbrand"));
		info.setCarenginnumber(obj.getString("carenginnumber"));
		info.setCarnumber(obj.getString("carnumber"));
		Bitmap map = toGetBitmap(obj.getString("carsign"));
		info.setBitmap(map);
		info.setCartype(obj.getString("cartype"));
		info.setPhone(obj.getString("phone"));
		return info;

	}
/**
 * 添加订单的基本信息可以当作“头信息”
 */
	public static String toAddCarOilBaseInfo(CarOrderBaseInfo info) throws Exception {
		String url = preUrl + "/servlet/CarOilOrderBaseIndoAddServlet";
		String data = "phone=" + info.getPhone() + "&carnumber=" + info.getCarnumber();

		return samePart(url, data);
	}
/**
 * 查询订单的基本信息
 * @param phone
 * @return
 * @throws Exception
 */
	public static List<String> toQueryCarOilBaseInfo(String phone) throws Exception {
		String url = preUrl + "/servlet/CarOilOrderBaseInfoQuery";
		String data = "phone=" + phone;
		String str = samePart(url, data);
		if(str.equals("0001")||str.equals("0002")){
			return null;
		}
		List<String> list = new ArrayList<String>();
		JSONArray array = new JSONArray(str);
		for (int i = 0; i < array.length(); i++) {
			list.add(array.get(i).toString());
		}
		return list;
	}
	/**
	 * 向服务器中注册本手机的唯一标识码，此处的唯一标识码是用UUID来表示
	 * 不用设备号的原因是可能有的设备不具有设备号，可能会产生空指针异常当然可以先判断，如果为空则用UUID
	 * @param phone
	 * @param onlycode
	 * @return
	 * @throws Exception
	 */
	public static String toStorageLoginOnlyCode(String phone, String onlycode) throws Exception {
		String url = preUrl + "/servlet/UserSignlLoginServlet";
		String data = "phone=" + phone + "&onlycode=" + onlycode;
		return samePart(url, data);
	}
	/**
	 * 去服务器中查询唯一标识码
	 * @param phone
	 * @return
	 * @throws Exception
	 */
	public static String toQueryLoginOnlyCode(String phone) throws Exception {
		String url = preUrl + "/servlet/UserSignlLoginQueryServlet";
		String data = "phone=" + phone;
		return samePart(url, data);
	}
	/**
	 * 通用部分
	 * @param url
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String samePart(String url, String data) throws Exception {
		URL u = new URL(url);
		// System.out.println(url);
		SharedPreferences sp =  context.getSharedPreferences("config", context.MODE_PRIVATE);
		String token = sp.getString("onlycode", "");
		String phonefilter = sp.getString("phone", "");
		String passwordfilter = sp.getString("password", "");
		String realdata = "token="+token+"&phonefilter="+phonefilter+"&passwordfilter="+passwordfilter+"&"+data;
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		conn.setDoOutput(true);

		OutputStream out = conn.getOutputStream();
		// System.out.println("===" + data);
		out.write(realdata.getBytes("utf-8"));
		out.flush();
		out.close();

		int code = conn.getResponseCode();
		// System.out.println(code);
		if (code == 200) {
			InputStream in = conn.getInputStream();
			String str = StreamTools.readFromStream(in);
			System.out.println("str=" + str);

			return str;
		}
		return "110";
	}
/**
 * 得到本手机号码的车辆维护信息
 * @param carnumber
 * @param phone
 * @param count
 * @return
 * @throws Exception
 */
	public static List<SimpleInfo> toGetMyMaintainInfos(String carnumber, String phone, String count) throws Exception {
		String data = "carnumber=" + carnumber + "&count=" + count + "&phone=" + phone;
		String url = preUrl + "/servlet/SupplyMyMaintainInfoServlet";
		String result = samePart(url, data);
		if(result.equals("0001")||result.equals("0002")){
			return null;
		}
		List<SimpleInfo> list = new ArrayList<SimpleInfo>();
		JSONArray array = new JSONArray(result);
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = new JSONObject(array.get(i).toString());
			SimpleInfo info = new SimpleInfo();
			info.setTime(obj.getString("time"));
			String oil = obj.getString("caroil");
			Double d = Double.valueOf(oil);
			int t = (int) (d * 100);
			info.setCaroil(t + "%");
			info.setCartranstate(obj.getString("cartranstate"));
			info.setCarlightstate(obj.getString("carlightstate"));
			info.setCarmileage(obj.getString("carmileage"));
			info.setCarenginstate(obj.getString("carenginstate"));
			info.setPhone(obj.getString("phone"));
			list.add(info);
		}
		return list;
	}
	/**
	 * 获取车辆异常信息
	 * @param phone
	 * @return
	 * @throws Exception
	 */
	public static String toGetCarAbnormalInfo(String phone) throws Exception {
		String data = "phone=" + phone;
		String url = preUrl + "/servlet/SupplyCarAbnormalInfo";
		String result = samePart(url, data);
		// System.out.println("result====="+result);
		return result;
	}
}
