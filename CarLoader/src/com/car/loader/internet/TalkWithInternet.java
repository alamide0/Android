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
	 * ���������ģ�������ȡSP�б�����û������������Ϣ
	 * @param context APP������
	 */
	public  static void setContext(Context context){
		TalkWithInternet.context = context;
	}
	
	/**
	 * ��ȡע���õ���֤��
	 * @param phone �ֻ���
	 * @return ���������ڱ�������Ĵ�����
	 * @throws Exception �����׳�������������п��ܳ��ֵ��쳣���������糬ʱ��
	 */
	public static String toGetPassCode(String phone) throws Exception {
		String data = "phone=" + phone;
		String url = preUrl + "/servlet/GetPassCodeServlet";
		return samePart(url, data);

	}

	/**
	 * ����������ע��
	 * 
	 * @param phone
	 *            �ֻ�����
	 * @param passcode
	 *            �������֤��
	 * @return �Ƿ�ע��ɹ�����Ϣ��String
	 * @throws Exception
	 */
	public static String toRegister(String phone, String passcode) throws Exception {
		String data = "phone=" + phone + "&passcode=" + passcode;
		String url = preUrl + "/servlet/RegisterServlet";
		return samePart(url, data);
	}
	/**
	 * ��֤����֤�ɹ�֮����еĲ�����ȥע���û���
	 * @param phone �ֻ���
	 * @param username �û��ǳ�
	 * @param password �û�����
	 * @return
	 * @throws Exception
	 */
	public static String toRegisterUser(String phone, String username, String password) throws Exception {
		String data = "phone=" + phone + "&username=" + username + "&password=" + password;
		String url = preUrl + "/servlet/RegisterUserServlet";
		return samePart(url, data);
	}
	/**
	 * ��½�û���
	 * @param phone �ֻ���
	 * @param password ����
	 * @return �Ƿ��½�ɹ��Ľ��
	 * @throws Exception
	 */
	public static String toLogin(String phone, String password) throws Exception {
		String data = "phone=" + phone + "&password=" + password;
		String url = preUrl + "/servlet/LoginServlet";
		return samePart(url, data);
	}
	/**
	 * �޸�����
	 * @param phone �ֻ���
	 * @param password ������
	 * @param username �û��ǳ�
	 * @param olderPwd ������
	 * @return ���ط�����������
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
	 * �����������ʱ������ȡ��֤��
	 * @param phone �ֻ���
	 * @return ����������Ľ��
	 * @throws Exception
	 */
	public static String toGetPCodeForForget(String phone) throws Exception {
		String data = "phone=" + phone;
		String url = preUrl + "/servlet/ForgetPwdServlet";
		return samePart(url, data);
	}
	/**
	 * ȥ��ȡ�û����ǳ�
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
	 * ����û��������֤���Ƿ�ͷ������б������֤���Ƿ���ͬ
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
	 * ������������
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
	 * ���������ά�������Ļ�����Ϣ
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
	 * ȥά��������״̬��Ϣ
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
	 * ����ͼƬ��URL����ͼƬ��Դ
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
 * ��ȡ������ά����Ϣ
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
 * ȥ��ñ��ֻ������󶨵����г����Ļ�����Ϣ
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
	 * ȥ�洢���Ͷ�������Ϣ
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
 * ��ѯ����
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
 * ��ȡ�����Ļ�����Ϣ���ݳ��ƺ�
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
 * ��Ӷ����Ļ�����Ϣ���Ե�����ͷ��Ϣ��
 */
	public static String toAddCarOilBaseInfo(CarOrderBaseInfo info) throws Exception {
		String url = preUrl + "/servlet/CarOilOrderBaseIndoAddServlet";
		String data = "phone=" + info.getPhone() + "&carnumber=" + info.getCarnumber();

		return samePart(url, data);
	}
/**
 * ��ѯ�����Ļ�����Ϣ
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
	 * ���������ע�᱾�ֻ���Ψһ��ʶ�룬�˴���Ψһ��ʶ������UUID����ʾ
	 * �����豸�ŵ�ԭ���ǿ����е��豸�������豸�ţ����ܻ������ָ���쳣��Ȼ�������жϣ����Ϊ������UUID
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
	 * ȥ�������в�ѯΨһ��ʶ��
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
	 * ͨ�ò���
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
 * �õ����ֻ�����ĳ���ά����Ϣ
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
	 * ��ȡ�����쳣��Ϣ
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
