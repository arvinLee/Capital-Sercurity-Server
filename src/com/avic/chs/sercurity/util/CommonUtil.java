package com.avic.chs.sercurity.util;

public class CommonUtil {
	private static final String number_regex = "^[01]$";
	
	public static boolean isBlank(String str){
		return str == null || "".equals(str);
	}
	
	public static boolean isOneOrZero(String str){
		if(!isBlank(str)){
			return str.matches(number_regex);
		}
		return false;
	}
	
}
