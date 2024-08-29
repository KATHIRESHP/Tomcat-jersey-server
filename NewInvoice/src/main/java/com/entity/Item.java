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

import com.database.BaseDb;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;
import com.util.QueryUtil;

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

	public List<Error> validateItem() {
		List<Error> errorList = new ArrayList<Error>();
		if (this.getPrice() < 0) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Price should be greater than -1");
			errorList.add(error);
		}
		if (this.getName() == null || this.getName().length() > 25 || this.getName().trim().isEmpty()) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Item name should be less than 25 chars and not empty");
			errorList.add(error);
		}
		if (this.getDescription().length() > 60000) {
			System.out.println("description length: " + this.getDescription().length());
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Description too long should be below 60000 chars");
			errorList.add(error);
		}
		return errorList;
	}

	private static final String selectAllQuery = "Select itemName, itemId, description, price from ItemTable";
	private static final String selectQuery = "Select itemName, itemId, description, price from ItemTable where itemId = ?";
	private static final String updateQuery = "Update ItemTable set itemName = ?, description = ?, price = ? where itemId = ?";
	private static final String insertQuery = "Insert into ItemTable (itemId , itemName, description, price) values(?, ?, ?, ?)";
	private static final String deleteQuery = "Delete from ItemTable where itemId = ?";


	public static List<Item> getItems() {
		try {
			return getItems(BaseDb.executeQuery(selectAllQuery));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static List<Item> getItems(String criteria, String orderBy, String limit) {
		String query = selectAllQuery;
		query = QueryUtil.appendCriOrderLimit(query, criteria, orderBy, limit);
		try {
			return getItems(BaseDb.executeQuery(query));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static List<Item> getItems(List<Integer> itemIdList)
	{
		if (itemIdList.isEmpty()) {
			return new ArrayList<Item>();
		}
		StringBuilder query = new StringBuilder("select itemId, itemName, price, description from ItemTable where itemId in (");
		for(int i = 0; i < itemIdList.size(); i++)
		{
			query.append(itemIdList.get(i));
			if(i < itemIdList.size() - 1)
			{
				query.append(",");
			}
		}
		query.append(")");
		try
		{
			return getItems(BaseDb.executeQuery(query.toString()));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private static List<Item> getItems(ResultSet rs) {
		List<Item> itemList = new ArrayList<>();
		try {
			while(rs.next()) {
				itemList.add(mapObject(rs));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return itemList;
	}

	public static Item getItem(int itemId) {
		try {
			ResultSet rs = BaseDb.executeQuery(selectQuery, itemId);
			if (rs.next()) {
				return mapObject(rs);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean update(int itemId) {
		return BaseDb.executeUpdate(updateQuery, this.getName(), this.getDescription(), this.getPrice(), itemId);
	}

	public boolean create() {
		this.setItemId();
		return BaseDb.executeUpdate(insertQuery, this.getItemId(), this.getName(), this.getDescription(), this.getPrice());
	}

	public boolean delete() {
		return BaseDb.executeUpdate(deleteQuery, this.getItemId());
	}

	private static Item mapObject(ResultSet rs) throws SQLException
	{
		Item item = new Item();
		item.setDescription(rs.getString("description"));
		item.setName(rs.getString("itemName"));
		item.setItemId(rs.getInt("itemId"));
		item.setPrice(rs.getInt("price"));
		return item;
	}
}
