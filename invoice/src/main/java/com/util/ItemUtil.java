package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.database.ContactDb;
import com.database.ItemDb;
import com.entity.Contact;
import com.entity.Error;
import com.entity.Item;

public class ItemUtil {
	public static List<Error> validateItem(Item item) {
		List<Error> errorList = new ArrayList<Error>();
		if (item.getPrice() < 0) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Price should be greater than -1");
			errorList.add(error);
		}
		if (item.getName().length() > 25 || item.getName().trim().isEmpty()) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Item name should be less than 25 chars and not empty");
			errorList.add(error);
		}
		return errorList;
	}

	public static Response addOrEditItem(Item item, int itemId) {
		List<Error> errorList = validateItem(item);
		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid data", "error", errorList);
		}
		if (itemId != 0) {
			if (ItemDb.updateItem(itemId, item)) {
				Item itemUpdated = ItemDb.getItem(itemId);
				return ResponseUtil.generateResponse(200, "Item updation success", Item.responseKey, itemUpdated);
			}
			return ResponseUtil.generateResponse(500, "Error in updating item");
		}
		item.setItemId();
		if (ItemDb.addItem(item)) {
			Item itemCreated = ItemDb.getItem(item.getItemId());
			return ResponseUtil.generateResponse(201, "Item created successfully", Item.responseKey, itemCreated);
		}
		return ResponseUtil.generateResponse(500, "Error in creating Item");
	}

	public static Response getItems(UriInfo uriInfo)
	{
		List<Error> errorList = SecurityUtil.validateRequestParams(uriInfo, Item.getAllowedParameters(), Item.getAllowedFilterMap());
		String criteria = "";
		String orderBy = "";
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();
		for (Map.Entry<String, List<String>> entry : queryParamsMap.entrySet()) {
			String key = entry.getKey();
			System.out.println("Entry key: " + key + " Criteria: " + criteria);
			if (Item.getAllowedFilterMap().containsKey(key)) {
				for (String value : entry.getValue()) {
					criteria = QueryUtil.getCriteria(criteria, "ItemTable." + Item.getAllowedFilterMap().get(key), value, "and", "=");
				}
			}
			else if (!Item.getAllowedSortMap().containsKey(key) && !key.equals("sort") && !key.equals("sort_order") && !key.equals("search_text")) {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Parameter " + key + " was unknown");
				errorList.add(error);
			}
		}
		if (queryParamsMap.containsKey("sort") && queryParamsMap.containsKey("sort_order")) {
			if (!Item.getAllowedSortMap().containsKey(queryParamsMap.get("sort").get(0))) {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Column " + queryParamsMap.get("sort").get(0) + " was not sortable or unknown");
				errorList.add(error);
			} else {
				String sortOrder = queryParamsMap.get("sort_order").get(0);
				String sortCol = Item.getAllowedSortMap().get(queryParamsMap.get("sort").get(0));
				sortOrder = sortOrder.toUpperCase();
				if (!sortCol.isEmpty()) {
					orderBy = " order by ItemTable." + sortCol + " " + (sortOrder.equals("D") ? "desc" : "");
				}
			}
		}
		if (queryParamsMap.containsKey("search_text")) {
			String criteria1 = QueryUtil.getCriteria("", "ItemTable.itemName", queryParamsMap.get("search_text").get(0), "and", "like");
			String criteria2 = QueryUtil.getCriteria("", "ItemTable.description", queryParamsMap.get("search_text").get(0), "or", "like");
			String searchTextCriteria = QueryUtil.groupCriteria(criteria1, criteria2, "or");
			criteria = QueryUtil.groupCriteria(criteria, searchTextCriteria, "and");
		}
		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}
		List<Item> itemList = ItemDb.getItems(criteria, orderBy);
		return ResponseUtil.generateResponse(200, "Item retrieval success", Item.responseKey, itemList);
	}
}
