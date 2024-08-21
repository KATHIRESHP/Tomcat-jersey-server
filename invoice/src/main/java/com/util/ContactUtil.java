package com.util;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

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
}
