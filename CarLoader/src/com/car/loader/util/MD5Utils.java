package com.car.loader.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	public static String md5(String passWord) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(passWord.getBytes());
			StringBuffer buffer = new StringBuffer();
			for (byte b : result) {
				int number = b & 0xff;
				String s = Integer.toHexString(number);
				if(s.length() == 1){
					buffer.append("0");
				}
				buffer.append(s);
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
