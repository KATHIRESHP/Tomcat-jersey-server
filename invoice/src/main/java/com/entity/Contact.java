package com.entity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

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
}
