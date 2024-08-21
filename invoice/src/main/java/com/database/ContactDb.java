package com.database;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.entity.Contact;
import com.entity.Invoice;

public class ContactDb {
	

	public static List<Contact> getContacts() {
		String query = "Select * from ContactTable";
		List<Contact> contactsList = new ArrayList<Contact>();
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				Contact contact = new Contact();
				contact.setEmail(rs.getString("email"));
				contact.setName(rs.getString("name"));
				contact.setContactId(rs.getInt("contactId"));
				contactsList.add(contact);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contactsList;
	}
	
	public static Contact getContact(int id) {
		String query = "Select * from ContactTable where contactId = ?";
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				Contact contact = new Contact();
				contact.setEmail(rs.getString("email"));
				contact.setName(rs.getString("name"));
				contact.setContactId(rs.getInt("contactId"));
				return contact;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static boolean addContact(Contact contact) {
		String query = "insert into ContactTable (contactId, name, email) values (?, ?, ?)";
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
		String query = "update ContactTable set name = ?, email = ? where contactId = ?";
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
		String query = "delete from ContactTable where contactId = ?";
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
