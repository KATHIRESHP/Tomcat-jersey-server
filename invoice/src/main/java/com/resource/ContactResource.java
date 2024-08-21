package com.resource;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.database.ContactDb;
import com.database.InvoiceDb;
import com.entity.*;
import com.util.ContactUtil;
import com.util.ResponseUtil;

@Path("/contacts")
public class ContactResource {
	
	@GET 
    @Produces(MediaType.APPLICATION_JSON)
	public Response getContacts() {
		List<Contact> contactsList = ContactDb.getContacts();
		return ResponseUtil.generateResponse(200, "contacts retrival successfull", Contact.responseKey, contactsList);
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@PathParam("{id}")
	public Response getContact(@PathParam("id") int id) {
		Contact contact = ContactDb.getContact(id);
		if (contact != null) {
			return ResponseUtil.generateResponse(200, "Contact retrival success", Contact.responseKey, contact);
		}
		return ResponseUtil.generateResponse(404, "Contact not found");
	}
	
	@GET
	@Path("/{id}/invoices")
	@PathParam("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContactInvoice(@PathParam("id") int id) {
		if (ContactDb.getContact(id) == null) {
			return ResponseUtil.generateResponse(404, "Contact not found");
		}
		List<Invoice> invoiceList = InvoiceDb.getContactInvoice(id);
		return ResponseUtil.generateResponse(200, "Invoice retrival success", Invoice.responseKey, invoiceList);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createContact(Contact contact) {
		return ContactUtil.addOrEditContact(contact, 0);
	}
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@PathParam("{id}")
	public Response editContact(@PathParam("id") int id, Contact contact) {
		if (ContactDb.getContact(id) != null) {			
			return ContactUtil.addOrEditContact(contact, id);
		}
		return ResponseUtil.generateResponse(404, "Contact not found");
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@PathParam("{id}")
	public Response deleteContact(@PathParam("id") int id) {
		System.out.println(" delete contact Contact id " + id);
		List<Invoice> invoiceList = InvoiceDb.getContactInvoice(id);
		System.out.println("invoice list " + invoiceList);
		if (invoiceList != null && !invoiceList.isEmpty()) {
			return ResponseUtil.generateResponse(409, "Unable to delete contact since it has invoice associated");
		}
		if (ContactDb.deleteContact(id)) {
			return ResponseUtil.generateResponse(200, "Contact deletion success");
		}
		return ResponseUtil.generateResponse(404, "Contact not found");
	}
	
}
