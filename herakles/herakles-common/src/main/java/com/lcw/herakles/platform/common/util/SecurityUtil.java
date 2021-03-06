package com.lcw.herakles.platform.common.util;

import java.security.MessageDigest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 加密工具.
 * 
 * @author chenwulou
 *
 */
@Deprecated
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

	/**
	 * MD5 加密
	 * 
	 */
	public static String getMD5(String reqStr) {
		return SecurityUtil.getSecurity(reqStr, "MD5");
	}

	/**
	 * SHA-1加密
	 * 
	 */
	public static String getSHA1(String reqStr) {
		return SecurityUtil.getSecurity(reqStr, "SHA-1");
	}

	private static String getSecurity(String str, String securityType) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance(securityType);
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (Exception e) {
			log.error("密文转换异常！{}", e.getMessage());
		}
		return SecurityUtil.byte2hex(messageDigest.digest());
	}

	private static String byte2hex(byte[] byteArray) {
		StringBuffer resp = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				resp.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				resp.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return resp.toString();

	}

	public static void main(String[] args) {
		//4c78b8daf75be30acbfb1005ae3e1daf
		System.out.println(SecurityUtil.getMD5("admin"));
		System.out.println(SecurityUtil.getSHA1("admin"));
	}
}
