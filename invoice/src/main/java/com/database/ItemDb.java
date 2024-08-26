package com.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.entity.Item;

public class ItemDb {
	private static HashMap<String, String> queryMap = new HashMap<String, String>();
	static {
		intializeQueryMap();
	}
	private static void intializeQueryMap() {
		String selectAllQuery = "Select itemName, itemId, description, price from ItemTable";
		String selectQuery = "Select itemName, itemId, description, price from ItemTable where itemId = ?";
		String updateQuery = "Update ItemTable set itemName = ?, description = ?, price = ? "
				+ "where itemId = ?";
		String createQuery = "Insert into ItemTable (itemId , itemName, description, price) "
				+ "values(?, ?, ?, ?)";
		String deleteQuery = "Delete from ItemTable where itemId = ?";
		
		queryMap.put("DeleteQuery", deleteQuery);
		queryMap.put("UpdateQuery", updateQuery);
		queryMap.put("SelectAllQuery", selectAllQuery);
		queryMap.put("SelectQuery", selectQuery);
		queryMap.put("InsertQuery", createQuery);
	}
	
	public static boolean addItem(Item item) {
		String query = queryMap.get("InsertQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, item.getItemId());
			pst.setString(2, item.getName());
			pst.setString(3, item.getDescription());
			pst.setInt(4, item.getPrice());
			System.out.println("entered addItem db method");
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	

	public static boolean updateItem(int id, Item item) {
		String query = queryMap.get("UpdateQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setString(1, item.getName());
			pst.setString(2, item.getDescription());
			pst.setInt(3, item.getPrice());
			pst.setInt(4, id);
			System.out.println(item.toString());
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static Item getItem(int id) {
		String query = queryMap.get("SelectQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				Item item = new Item();
				item.setItemId(rs.getInt("itemId"));
				item.setName(rs.getString("itemName"));
				item.setPrice(rs.getInt("price"));
				item.setDescription(rs.getString("description"));
				return item;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Item> getItems(ArrayList<Integer> itemIdList) {
		List<Item>  itemList = new ArrayList<Item>();
		StringBuilder query = new StringBuilder("select * from ItemTable where itemId in (");
	    for (int i = 0; i < itemIdList.size(); i++) {
	        query.append(itemIdList.get(i));
	        if (i < itemIdList.size() - 1) {
	            query.append(",");
	        }
	    }
	    query.append(")");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query.toString());
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				Item item = new Item();
				item.setDescription(rs.getString("description"));
				item.setName(rs.getString("itemName"));
				item.setItemId(rs.getInt("itemId"));
				item.setPrice(rs.getInt("price"));
				itemList.add(item);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemList;
	}

	public static boolean deleteItem(int id) {
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

	public static List<Item> getItems(String criteria, String orderBy) {
		String query = queryMap.get("SelectAllQuery");

		if (!criteria.isEmpty()) {
			query += " where " + criteria;
		}
		if (!orderBy.isEmpty()) {
			query += orderBy;
		}
		List<Item> itemList = new ArrayList<Item>();
		try {
			System.out.println("Query " + query);
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				Item item = new Item();
				item.setDescription(rs.getString("description"));
				item.setName(rs.getString("itemName"));
				item.setItemId(rs.getInt("itemId"));
				item.setPrice(rs.getInt("price"));
				itemList.add(item);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemList;
	}
}
