package com.util;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.entity.Error;
import com.entity.Item;

public class ItemUtil {

	public static Response addOrEditItem(Item item, int itemId)
	{
		List<Error> errorList = item.validateItem();
		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid data", "error", errorList);
		}
		boolean isUpdate = itemId != 0;
		boolean processSuccess;
		if (isUpdate) {
			processSuccess = item.update(itemId);
		}
		else  {
			processSuccess = item.create();
			itemId = item.getItemId();
		}
		if (processSuccess) {
			String responseStr = (isUpdate) ? "Item updated" : "Item created";
			int responseCode = (isUpdate) ? 200 : 201;
			return ResponseUtil.generateResponse(responseCode, responseStr, Item.responseKey, Item.getItem(itemId));
		}
		return ResponseUtil.generateResponse(409, "Error in " + ((isUpdate) ? "updating" : "creating") + " Item");
	}

	public static Response getItems(UriInfo uriInfo)
	{
		List<Error> errorList = SecurityUtil.validateRequestParams(uriInfo, Item.getAllowedParameters(), Item.getAllowedFilterMap(), Item.getAllowedSortMap());
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();

		String criteria = QueryUtil.handleParamCriteria(queryParamsMap, Item.getAllowedFilterMap(), "ItemTable");
		String orderBy = QueryUtil.handleParamSortOrder(queryParamsMap, Item.getAllowedSortMap(), "ItemTable");
		String pageLimit = QueryUtil.handlePagination(queryParamsMap);

		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}
		if (queryParamsMap.containsKey("search_text")) {
			String criteria1 = QueryUtil.getCriteria("", "ItemTable.itemName", queryParamsMap.get("search_text").get(0), "and", "like");
			String criteria2 = QueryUtil.getCriteria("", "ItemTable.description", queryParamsMap.get("search_text").get(0), "or", "like");
			String searchTextCriteria = QueryUtil.groupCriteria(criteria1, criteria2, "or");
			criteria = QueryUtil.groupCriteria(searchTextCriteria, criteria, "and");
		}
		List<Item> itemList = Item.getItems(criteria, orderBy, pageLimit);
		return ResponseUtil.generateResponse(200, "Item retrieval success", Item.responseKey, itemList);
	}
}
