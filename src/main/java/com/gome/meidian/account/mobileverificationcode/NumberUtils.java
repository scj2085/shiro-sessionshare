package com.gome.meidian.account.mobileverificationcode;

import java.math.BigDecimal;

import org.apache.commons.lang3.RandomStringUtils;

public class NumberUtils {
	
	/**
	 * @description: 两个值相减
	 * @param first
	 * @param last
	 * @return 相减后的结果
	 * @author: yaoliang
	 * @time: 2016年7月16日 下午6:52:31
	 */
	public static int subtract(BigDecimal first, BigDecimal last) {
		
		return first.subtract(last).intValue();
	}
	
	/**
	 * @description: 两个值相减
	 * @param first
	 * @param last
	 * @return 相减后的结果
	 * @author: yaoliang
	 * @time: 2016年7月16日 下午6:52:31
	 */
	public static int subtract(String first, String last) {
		
		BigDecimal b1 = new BigDecimal(first);
		BigDecimal b2 = new BigDecimal(last);
		return b1.subtract(b2).intValue();
	}
	
	/**
	 * 
	 * @description:生成指定位数的随机字符串，字符串组成：小写字母+数字
	 * @param digit
	 * @return 随机字符串
	 * @author: yaoliang
	 * @time: 2016年7月29日 下午12:15:55
	 */
	public static String random(int digit) {
		String s = RandomStringUtils.random(digit, new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1','2', '3', '4', '5', '6', '7', '8', '9', '0' });
		return s;
	}



}
