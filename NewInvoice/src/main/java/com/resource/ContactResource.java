package com.resource;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.entity.*;
import com.util.ContactUtil;
import com.util.ResponseUtil;

@Path("/contacts")
public class ContactResource
{

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContacts(@Context UriInfo uriInfo)
	{
		return ContactUtil.getContacts(uriInfo);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@PathParam("{id}")
	public Response getContact(@PathParam("id") int id)
	{
		Contact contact = Contact.getContact(id);
		if(contact != null)
		{
			return ResponseUtil.generateResponse(200, "Contact retrival success", Contact.responseKey, contact);
		}
		return ResponseUtil.generateResponse(404, "Contact not found");
	}

	@GET
	@Path("/{id}/invoices")
	@PathParam("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContactInvoice(@PathParam("id") int id)
	{
		if(Contact.getContact(id) != null)
		{
			List<Invoice> invoiceList = Invoice.getContactInvoices(id);
			return ResponseUtil.generateResponse(200, "Invoice retrieval success", Invoice.responseKey, invoiceList);
		}
		return ResponseUtil.generateResponse(404, "Contact not found");
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createContact(Contact contact)
	{
		return ContactUtil.addOrEditContact(contact, 0);
	}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@PathParam("{id}")
	public Response editContact(@PathParam("id") int id, Contact contact)
	{
		if(Contact.getContact(id) != null)
		{
			return ContactUtil.addOrEditContact(contact, id);
		}
		return ResponseUtil.generateResponse(404, "Contact not found");
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@PathParam("{id}")
	public Response deleteContact(@PathParam("id") int id)
	{
		Contact contact = Contact.getContact(id);
		if(contact != null)
		{
			List<Invoice> invoiceList = Invoice.getContactInvoices(id);
			if(!invoiceList.isEmpty())
			{
				return ResponseUtil.generateResponse(409, "Unable to delete contact since it has invoice associated");
			}
			if(contact.delete())
			{
				return ResponseUtil.generateResponse(200, "Contact deletion success");
			}
		}
		return ResponseUtil.generateResponse(404, "Contact not found");
	}

}
