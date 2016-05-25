package com.avic.chs.sercurity.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
	public static final String FILE_SEPERATOR = System.getProperty("file.separator");
	public static final String CONF_PROPERTY = "conf.properties";
	
	public static final String USERINFO_LOCK_DEFAULT = "userinfo_lock_default";
	public static final String USERINFO_SECRETLEVEL_DEFAULT = "userinfo_secretlevel_default";
	
	private static Properties prop;
	
	public static String getConfig(String paramName)  {
		if(prop == null){
			prop = new Properties();
			try {
				prop.load(PropertyUtil.class
						.getResourceAsStream("/"
								+ PropertyUtil.CONF_PROPERTY));
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
