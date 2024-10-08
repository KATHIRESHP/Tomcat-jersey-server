
package com.resource;

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

import com.entity.Contact;
import com.entity.Invoice;
import com.util.InvoiceUtil;
import com.util.ResponseUtil;

/**
 * Example resource class hosted at the URI path "/myresource"
 */
@Path("/invoices")
public class InvoiceResource
{

	/**
	 * Method processing HTTP GET requests, producing "text/plain" MIME media type.
	 *
	 * @return String that will be send back as a response of type "text/plain".
	 */

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInvoices(@Context UriInfo uriInfo)
	{
		return InvoiceUtil.getInvoices(uriInfo);
	}

	@Path("/{id}")
	@GET
	@PathParam("id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInvoice(@PathParam("id") int id)
	{
		Invoice invoice = Invoice.getInvoice(id);
		if(invoice != null)
		{
			return ResponseUtil.generateResponse(200, "Invoice retrieval success", Invoice.responseKey, invoice);
		}
		return ResponseUtil.generateResponse(404, "Invoice not found");
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createInvoice(Invoice invoice)
	{
		return InvoiceUtil.addOrEditInvoice(invoice, 0);
	}

	@Path("/{id}")
	@PUT
	@PathParam("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editInvoice(@PathParam("id") int id, Invoice invoice)
	{
		if(Invoice.getInvoice(id) != null)
		{
			invoice.setInvoiceId(id);
			return InvoiceUtil.addOrEditInvoice(invoice, id);
		}
		return ResponseUtil.generateResponse(404, "Invoice not found");
	}

	@Path("/{id}/contact")
	@GET
	@PathParam("id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInvoiceContact(@PathParam("id") int id)
	{
		Invoice invoice = Invoice.getInvoice(id);
		if(invoice != null)
		{
			Contact contact = Contact.getContact(invoice.getContactId());
			return ResponseUtil.generateResponse(200, "Invoice contact retrieval success", Contact.responseKey, contact);
		}
		return ResponseUtil.generateResponse(404, "Invoice not found");
	}

	@Path("/{id}")
	@DELETE
	@PathParam("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInvoice(@PathParam("id") int id)
	{
		Invoice invoice = Invoice.getInvoice(id);
		if(invoice != null)
		{
			if(invoice.delete())
			{
				return ResponseUtil.generateResponse(200, "Invoice deletion success");
			}
		}
		return ResponseUtil.generateResponse(404, "Invoice not found");
	}

	@Path("/{id}/sent")
	@PUT
	@PathParam("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sentInvoice(@PathParam("id") int id)
	{
		return InvoiceUtil.changeInvoiceStatus(id, "sent");
	}

	@Path("/{id}/draft")
	@PUT
	@PathParam("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response draftInvoice(@PathParam("id") int id)
	{
		return InvoiceUtil.changeInvoiceStatus(id, "draft");
	}

	@Path("/{id}/record-payment")
	@PUT
	@PathParam("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recordPaymentInvoice(@PathParam("id") int id)
	{
		return InvoiceUtil.changeInvoiceStatus(id, "paid");
	}
}
