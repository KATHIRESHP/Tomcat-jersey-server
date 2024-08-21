package com.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement
public class Item {
	
	@SerializedName("item_id")
	private int itemId;
	private String name;
	private String description;
	private int price;
	private static int idIndex = 1;
	
	public static String responseKey = "items";
	
	@XmlElement(name = "item_id")
	public int getItemId() {
		return itemId;
	}
	
	public void setItemId() {
		this.itemId = idIndex++;
	}
	public void setItemId(int id) {
		this.itemId = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
}
