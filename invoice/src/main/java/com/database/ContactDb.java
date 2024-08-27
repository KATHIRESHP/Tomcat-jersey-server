package com.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.entity.Contact;
import com.util.QueryUtil;

public class ContactDb {

	private static final HashMap<String, String> queryMap = new HashMap<String, String>();
	static {
		initializeQueryMap();
	}
	private static void initializeQueryMap() {
		String selectAllQuery = "Select email, name, contactId from ContactTable";
		String selectQuery = "Select email, name, contactId from ContactTable where contactId = ?";
		String insertQuery = "insert into ContactTable (contactId, name, email) values (?, ?, ?)";
		String updateQuery = "update ContactTable set name = ?, email = ? where contactId = ?";
		String deleteQuery = "delete from ContactTable where contactId = ?";

		queryMap.put("SelectAllQuery", selectAllQuery);
		queryMap.put("SelectQuery", selectQuery);
		queryMap.put("InsertQuery", insertQuery);
		queryMap.put("UpdateQuery", updateQuery);
		queryMap.put("DeleteQuery", deleteQuery);
	}

	public static List<Contact> getContacts(String criteria, String orderBy, String pageLimit) {
		String query = queryMap.get("SelectAllQuery");
		query = QueryUtil.appendCriOrderLimit(query, criteria, orderBy, pageLimit);
		
		List<Contact> contactsList = new ArrayList<Contact>();
		try {
			System.out.println("Query " + query);
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				contactsList.add(getContact(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contactsList;
	}
	
	public static Contact getContact(int id) {
		String query = queryMap.get("SelectQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return getContact(rs);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static Contact getContact(ResultSet rs) throws SQLException
	{
		Contact contact = new Contact();
		contact.setEmail(rs.getString("email"));
		contact.setName(rs.getString("name"));
		contact.setContactId(rs.getInt("contactId"));
		return contact;
	}

	public static boolean addContact(Contact contact) {
		String query = queryMap.get("InsertQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, contact.getContactId());
			pst.setString(2, contact.getName());
			pst.setString(3, contact.getEmail());
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean updateContact(int id, Contact contact) {
		String query = queryMap.get("UpdateQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setString(1, contact.getName());
			pst.setString(2, contact.getEmail());
			pst.setInt(3, id);
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean deleteContact(int id) {
		String query = queryMap.get("DeleteQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
