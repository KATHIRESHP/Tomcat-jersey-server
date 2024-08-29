package com.entity;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import com.database.BaseDb;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;
import com.util.QueryUtil;

@XmlRootElement
@JsonPropertyOrder({"contact_id", "name", "email"})
public class Contact {
	@JsonProperty("contact_id")
	private int contactId;
	private String name;
	private String email;
	private static Map<String, String> allowedFilterMap = new HashMap<String, String>();
	private static Map<String, String> allowedSortMap = new HashMap<String, String>();
	private static List<String> allowedParameters = new ArrayList<String>();
	private static int idIndex = 1;
	
	public static String responseKey = "contacts";

	static {
		initializeStatics();
	}

	private static void initializeStatics()
	{
		allowedParameters.add("sort");
		allowedParameters.add("sort_order");
		allowedParameters.add("search_text");
		allowedParameters.add("page");
		allowedParameters.add("size");

		allowedFilterMap.put("contact_id", "contactId");
		allowedFilterMap.put("name", "name");

		allowedSortMap.put("contact_id", "contactId");
		allowedSortMap.put("email", "email");
		allowedSortMap.put("name", "name");
	}

	@XmlElement(name = "contact_id")
	public int getContactId() {
		return contactId;
	}
	public void setContactId() {
		this.contactId = idIndex++;
	}
	public void setContactId(int id) {
		this.contactId = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public static Map<String, String> getAllowedFilterMap()
	{
		return allowedFilterMap;
	}

	public static Map<String, String> getAllowedSortMap()
	{
		return allowedSortMap;
	}

	public static List<String> getAllowedParameters() {
		return allowedParameters;
	}


	public List<Error> validateContact() {
		List<Error> errorList = new ArrayList<Error>();
		if (this.getName() == null || this.getName().trim().isEmpty() || this.getName().length() > 25) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Name should be less than 25 chars and not empty");
			errorList.add(error);
		}
		if (this.getEmail() == null || this.getEmail().trim().length() > 25 || this.getEmail().isEmpty()) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Email should be less than 25 chars and not empty");
			errorList.add(error);
		}
		return errorList;
	}

	private static final String selectAllQuery = "Select email, name, contactId from ContactTable";
	private static final String selectQuery = "Select email, name, contactId from ContactTable where contactId = ?";
	private static final String insertQuery = "insert into ContactTable (contactId, name, email) values (?, ?, ?)";
	private static final String updateQuery = "update ContactTable set name = ?, email = ? where contactId = ?";
	private static final String deleteQuery = "delete from ContactTable where contactId = ?";
	
	public static List<Contact> getContacts(String criteria, String orderBy, String pageLimit) {
		String query = selectAllQuery;
		query = QueryUtil.appendCriOrderLimit(query, criteria, orderBy, pageLimit);
		try {
			return getContacts(BaseDb.executeQuery(query));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static List<Contact> getContacts(ResultSet rs) throws SQLException
	{
		List<Contact> contactList = new ArrayList<>();
		while(rs.next()) {
			contactList.add(mapObject(rs));
		}
		return contactList;
	}


	public static Contact getContact(int contactId) {
		try {
			ResultSet rs = BaseDb.executeQuery(selectQuery, contactId);
			if (rs.next()) {
				return mapObject(rs);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean create() {
		this.setContactId();
		return BaseDb.executeUpdate(insertQuery, this.getContactId(), this.getName(), this.getEmail());
	}


	public boolean update(int contactId) {
		return BaseDb.executeUpdate(updateQuery, this.getName(), this.getEmail(), contactId);
	}

	public boolean delete() {
		return BaseDb.executeUpdate(deleteQuery, this.getContactId());
	}

	private static Contact mapObject(ResultSet rs) throws SQLException
	{
		Contact contact = new Contact();
		contact.setEmail(rs.getString("email"));
		contact.setName(rs.getString("name"));
		contact.setContactId(rs.getInt("contactId"));
		return contact;
	}
}
