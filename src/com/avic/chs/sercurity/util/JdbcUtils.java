package com.avic.chs.sercurity.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class JdbcUtils {
	/**
	 * 从jdbc.properties文件中获取配置
	 */
	private static String driver = Constants.getDbParam(Constants.CM_DRIVER_CLASS_NAME);
	private static String url = Constants.getDbParam(Constants.CM_DATASOURCE_URL);
	private static String username = Constants.getDbParam(Constants.CM_DATASOURCE_USERNAME);
	private static String password = Constants.getDbParam(Constants.CM_DATASOURCE_PASSWORD);
	
	// 单例
	private JdbcUtils() {
	}

	/*static {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}*/
	// cm ---获取cm的className
	public static void getCmClassName() throws ClassNotFoundException {
		Class.forName(driver);
	}
	
	// 获取连接
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
	
	// 释放资源
	public static void close(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		}

	}
}
