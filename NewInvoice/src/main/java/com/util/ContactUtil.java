package com.util;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.entity.Contact;
import com.entity.Error;

public class ContactUtil {


	public static Response addOrEditContact(Contact contact, int contactId) {
		List<Error> errorList = contact.validateContact();
		if(!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid data", "error", errorList);
		}
		boolean isUpdate = contactId != 0;
		boolean processSuccess;
		if (isUpdate) {
			processSuccess = contact.update(contactId);
		}
		else {
			processSuccess = contact.create();
			contactId = contact.getContactId();
		}
		if (processSuccess) {
			String responseStr = isUpdate ? "Contact updated" : "Contact created";
			int responseCode = isUpdate ? 200 : 201;
			return ResponseUtil.generateResponse(responseCode, responseStr, Contact.responseKey, Contact.getContact(contactId));
		}
		return ResponseUtil.generateResponse(500, "Error in " + ((isUpdate) ? "updating" : "creating") + " contact");
	}

	public static Response getContacts(UriInfo uriInfo)
	{
		List<Error> errorList = SecurityUtil.validateRequestParams(uriInfo, Contact.getAllowedParameters(), Contact.getAllowedFilterMap(), Contact.getAllowedSortMap());
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();

		String criteria = QueryUtil.handleParamCriteria(queryParamsMap, Contact.getAllowedFilterMap(), "ContactTable");
		String orderBy = QueryUtil.handleParamSortOrder(queryParamsMap, Contact.getAllowedSortMap(), "ContactTable", errorList);
		String pageLimit = QueryUtil.handlePagination(queryParamsMap, errorList);

		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}
		if (queryParamsMap.containsKey("search_text")) {
			String criteria1 = QueryUtil.getCriteria("", "ContactTable.name", queryParamsMap.get("search_text").get(0), "", "like");
			String criteria2 = QueryUtil.getCriteria("", "ContactTable.email", queryParamsMap.get("search_text").get(0), "", "like");
			String searchCriteria = QueryUtil.groupCriteria(criteria1, criteria2, "or");
			criteria = QueryUtil.groupCriteria(searchCriteria, criteria, "and");
		}
		List<Contact> contactList = Contact.getContacts(criteria, orderBy, pageLimit);
		return ResponseUtil.generateResponse(200, "Contact retrieval success", Contact.responseKey, contactList);
	}
}
