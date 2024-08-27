package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.database.ItemDb;
import com.entity.Error;
import com.entity.Item;

public class ItemUtil {

	public static Response addOrEditItem(Item item, int itemId) {
		List<Error> errorList = SecurityUtil.validateItem(item);
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
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();

		String criteria = QueryUtil.handleParamCriteria(queryParamsMap, Item.getAllowedFilterMap(), "ItemTable");
		String orderBy = QueryUtil.handleParamSortOrder(queryParamsMap, Item.getAllowedSortMap(), "ItemTable", errorList);
		String pageLimit = QueryUtil.handlePagination(queryParamsMap, errorList);

		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}
		if (queryParamsMap.containsKey("search_text")) {
			String criteria1 = QueryUtil.getCriteria("", "ItemTable.itemName", queryParamsMap.get("search_text").get(0), "and", "like");
			String criteria2 = QueryUtil.getCriteria("", "ItemTable.description", queryParamsMap.get("search_text").get(0), "or", "like");
			String searchTextCriteria = QueryUtil.groupCriteria(criteria1, criteria2, "or");
			criteria = QueryUtil.groupCriteria(criteria, searchTextCriteria, "and");
		}
		List<Item> itemList = ItemDb.getItems(criteria, orderBy, pageLimit);
		return ResponseUtil.generateResponse(200, "Item retrieval success", Item.responseKey, itemList);
	}
}
