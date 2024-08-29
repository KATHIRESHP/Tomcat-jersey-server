package com.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseDb {
	public static boolean executeUpdate(String query, Object... params) {
        try  {
        	Connection con = SqlConnection.getConnection();
        	System.out.println(query);
            PreparedStatement pst = con.prepareStatement(query);
            setParameters(pst, params);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ResultSet executeQuery(String query, Object... params) {
        try {
            Connection con = SqlConnection.getConnection();
        	System.out.println(query);
            PreparedStatement pst = con.prepareStatement(query);
            setParameters(pst, params);
            return pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setParameters(PreparedStatement pst, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pst.setObject(i + 1, params[i]);
        }
    }
}
