package com.avic.chs.sercurity.util;

import java.io.IOException;
import java.util.Properties;


public class Constants {

	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	public static final String JDBC_PROPERTY = "jdbc.properties";
	
	public static final String CM_DRIVER_CLASS_NAME = "dataSource.driverClassName";//driverClassName
	public static final String CM_DATASOURCE_URL = "dataSource.url";//url
	public static final String CM_DATASOURCE_USERNAME = "dataSource.username";//数据库用户名
	public static final String CM_DATASOURCE_PASSWORD = "dataSource.password";//数据库密码
	
	//源串"CHENGFEI.CAC.132"通过DesUtils加密
	public static String BAO_MI_STR_SRC = "CHENGFEI.CAC.132";
	public static String BAO_MI_STR_ENC = "a83d78f9ef0b81d845509797a63f7644da192abd308b3db7";
	
	private static Properties prop;

	public static String getDbParam(String paramName)  {
		if(prop == null){
			prop = new Properties();
			try {
				prop.load(Constants.class
						.getResourceAsStream("/"
								+ Constants.JDBC_PROPERTY));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		String paramValue = prop.getProperty(paramName);
		if(paramValue == null) {
			return null;
		}else{
			return  paramValue;
		}
	}
}
