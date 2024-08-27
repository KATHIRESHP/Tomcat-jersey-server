package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.database.ContactDb;
import com.database.ItemDb;
import com.entity.Contact;
import com.entity.Error;
import com.entity.Invoice;
import com.entity.InvoiceLineItem;
import com.entity.Item;

public class SecurityUtil
{
	private static String alphaNumericPattern = "^[a-zA-Z0-9_ ]*$";
	
	public static boolean isValidParamValue(String value) {
		return (value != null) && (!value.isEmpty()) && (value.matches(alphaNumericPattern));
	}

	public static List<Error> validateRequestParams(UriInfo uriInfo, List<String> allowedParameters, Map<String, String> allowedFilterMap)
	{
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();
		List<Error> errorList = new ArrayList<Error>();
		for (Map.Entry<String, List<String>> entry : queryParamsMap.entrySet()) {
			String key = entry.getKey();
			if (allowedParameters.contains(key) || allowedFilterMap.containsKey(key)) {
				for (String value: queryParamsMap.get(key)) {
					if (!SecurityUtil.isValidParamValue(value)) {
						Error error = new Error();
						error.setCode(400);
						error.setMessage("Parameter "  + key + "'s value [" + value + "] is not valid");
						errorList.add(error);
					}
				}
			}
			else {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Parameter "  + key + " was unknown");
				errorList.add(error);
			}
		}
		return errorList;
	}

	public static List<Error> validateContact(Contact contact) {
		List<Error> errorList = new ArrayList<Error>();
		if (contact.getName().trim().isEmpty() || contact.getName().length() > 25) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Name should be less than 25 chars and not empty");
			errorList.add(error);
		}
		if (contact.getEmail().trim().length() > 25 || contact.getEmail().isEmpty()) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Email should be less than 25 chars and not empty");
			errorList.add(error);
		}
		return errorList;
	}

	public static List<Error> validateInvoice(Invoice invoice) {
		List<InvoiceLineItem> invoiceLineItems = invoice.getLineItems();
		ArrayList<Integer> itemIdList = new ArrayList<Integer>();
		List<Error> errorList = new ArrayList<Error>();
		if (invoiceLineItems.isEmpty()) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Invoice has no line items");
			errorList.add(error);
		}
		if (ContactDb.getContact(invoice.getContactId()) == null) {
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Contact person associated with invoice not found");
			errorList.add(error);
		}
		for (InvoiceLineItem lineItem : invoiceLineItems) {
			itemIdList.add(lineItem.getItemId());
		}

		List<Item> itemList = ItemDb.getItems(itemIdList);
		List<Integer> dbItemIdList = new ArrayList<Integer>();
		for (Item item : itemList) {
			dbItemIdList.add(item.getItemId());
		}

		for (InvoiceLineItem lineItem : invoiceLineItems) {
			if (!dbItemIdList.contains(lineItem.getItemId())) {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Item with id: " + lineItem.getItemId() + " not found");
				errorList.add(error);
			}
		}

		return errorList;
	}

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
}
