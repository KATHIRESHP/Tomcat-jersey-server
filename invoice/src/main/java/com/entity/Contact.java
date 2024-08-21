package com.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement
public class Contact {
	@SerializedName("contact_id")
	private int contactId;
	private String name;
	private String email;
	private static int idIndex = 1;
	
	public static String responseKey = "contacts";
	
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
	
}
