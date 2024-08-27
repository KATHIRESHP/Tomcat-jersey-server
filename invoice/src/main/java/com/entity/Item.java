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
@JsonPropertyOrder({"item_id", "name", "description", "price"})
public class Item {
	
	@JsonProperty("item_id")
	private int itemId;
	private String name;
	private String description;
	private int price;
	private static Map<String, String> allowedFilterMap = new HashMap<String, String>();
	private static Map<String, String> allowedSortMap = new HashMap<String, String>();
	private static List<String> allowedParameters = new ArrayList<String>();
	private static int idIndex = 1;

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

		allowedFilterMap.put("item_id", "itemId");
		allowedFilterMap.put("name", "itemName");

		allowedSortMap.put("item_id", "itemId");
		allowedSortMap.put("name", "itemName");
		allowedSortMap.put("price", "price");
	}

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
