package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.database.ContactDb;
import com.entity.Contact;
import com.entity.Error;

public class ContactUtil {

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

	public static Response addOrEditContact(Contact contact, int id) {
		List<Error> errorList = ContactUtil.validateContact(contact);
		if(!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid data", "error", errorList);
		}
		if (id != 0) {
			if (ContactDb.updateContact(id, contact)) {
				Contact contactUpdated = ContactDb.getContact(id);
				return ResponseUtil.generateResponse(200, "Contact updation success", Contact.responseKey, contactUpdated);
			}
		}
		contact.setContactId();
		if (ContactDb.addContact(contact)) {
			Contact contactCreated = ContactDb.getContact(contact.getContactId());
			return ResponseUtil.generateResponse(201, "Contact creation success", Contact.responseKey, contactCreated);
		}
		return ResponseUtil.generateResponse(500, "Error in creating");
	}

	public static Response getContacts(UriInfo uriInfo)
	{
		List<Error> errorList = SecurityUtil.validateRequestParams(uriInfo, Contact.getAllowedParameters(), Contact.getAllowedFilterMap());
		String criteria = "";
		String orderBy = "";
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();
		for (Map.Entry<String, List<String>> entry : queryParamsMap.entrySet()) {
			String key = entry.getKey();
			System.out.println("Entry key: " + key + " Criteria: " + criteria);
			if (Contact.getAllowedFilterMap().containsKey(key)) {
				for (String value : entry.getValue()) {
					criteria = QueryUtil.getCriteria(criteria, "ContactTable." + Contact.getAllowedFilterMap().get(key), value, "and", "=");
				}
			}
		}
		if (queryParamsMap.containsKey("sort") && queryParamsMap.containsKey("sort_order")) {
			if (!Contact.getAllowedSortMap().containsKey(queryParamsMap.get("sort").get(0))) {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Column " + queryParamsMap.get("sort").get(0) + " was not sortable or unknown");
				errorList.add(error);
			} else {
				String sortOrder = queryParamsMap.get("sort_order").get(0);
				String sortCol = Contact.getAllowedSortMap().get(queryParamsMap.get("sort").get(0));
				sortOrder = sortOrder.toUpperCase();
				if (!sortCol.isEmpty()) {
					orderBy = " order by ContactTable." + sortCol + " " + (sortOrder.equals("D") ? "desc" : "");
				}
			}
		}
		if (queryParamsMap.containsKey("search_text")) {
			String criteria1 = QueryUtil.getCriteria("", "ContactTable.name", queryParamsMap.get("search_text").get(0), "and", "like");
			String criteria2 = QueryUtil.getCriteria("", "ContactTable.email", queryParamsMap.get("search_text").get(0), "or", "like");
			String searchCriteria = QueryUtil.groupCriteria(criteria1, criteria2, "or");
			criteria = QueryUtil.groupCriteria(criteria, searchCriteria, "and");
		}
		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}
		List<Contact> contactList = ContactDb.getContacts(criteria, orderBy);
		return ResponseUtil.generateResponse(200, "Contact retrieval success", Contact.responseKey, contactList);
	}
}
