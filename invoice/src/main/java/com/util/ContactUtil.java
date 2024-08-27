package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.Query;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.database.ContactDb;
import com.entity.Contact;
import com.entity.Error;

public class ContactUtil {


	public static Response addOrEditContact(Contact contact, int id) {
		List<Error> errorList = SecurityUtil.validateContact(contact);
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
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();

		String criteria = QueryUtil.handleParamCriteria(queryParamsMap, Contact.getAllowedFilterMap(), "ContactTable");
		String orderBy = QueryUtil.handleParamSortOrder(queryParamsMap, Contact.getAllowedSortMap(), "ContactTable", errorList);
		String pageLimit = QueryUtil.handlePagination(queryParamsMap, errorList);

		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}
		if (queryParamsMap.containsKey("search_text")) {
			String criteria1 = QueryUtil.getCriteria("", "ContactTable.name", queryParamsMap.get("search_text").get(0), "and", "like");
			String criteria2 = QueryUtil.getCriteria("", "ContactTable.email", queryParamsMap.get("search_text").get(0), "or", "like");
			String searchCriteria = QueryUtil.groupCriteria(criteria1, criteria2, "or");
			criteria = QueryUtil.groupCriteria(criteria, searchCriteria, "and");
		}
		List<Contact> contactList = ContactDb.getContacts(criteria, orderBy, pageLimit);
		return ResponseUtil.generateResponse(200, "Contact retrieval success", Contact.responseKey, contactList);
	}
}
