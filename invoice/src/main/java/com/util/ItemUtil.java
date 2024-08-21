package com.util;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import com.database.ItemDb;
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
}
