package com.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlConnection {
	
	private static String name = "root";
	private static String password = "";
	private static String dbName = "Invoice";
	private static Connection con = null;

	public static Connection getConnection() {
		if (con != null) {
			return con;
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, name, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

}
