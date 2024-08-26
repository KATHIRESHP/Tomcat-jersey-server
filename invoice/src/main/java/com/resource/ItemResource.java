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

import com.database.ContactDb;
import com.database.InvoiceDb;
import com.database.ItemDb;
import com.entity.Invoice;
import com.entity.Item;
import com.util.*;

@Path("/items")
public class ItemResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItems(@Context UriInfo uriInfo) {
		return ItemUtil.getItems(uriInfo);
	}

	@Path("/{id}")
	@GET
	@PathParam("id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItem(@PathParam("id") int id) {
		Item item = ItemDb.getItem(id);
		if (item != null) {
			return ResponseUtil.generateResponse(200, "Item retrival success", Item.responseKey, item);
		}
		return ResponseUtil.generateResponse(404, "Item not found");
	}

	@GET
	@Path("/{id}/invoices")
	@PathParam("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItemInvoice(@PathParam("id") int id) {
		if (ItemDb.getItem(id) == null) {
			return ResponseUtil.generateResponse(404, "Item not found");
		}
		List<Invoice> invoiceList = InvoiceDb.getItemInvoice(id);
		return ResponseUtil.generateResponse(200, "Invoice retrival success", Invoice.responseKey, invoiceList);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createItem(Item item) {
		return ItemUtil.addOrEditItem(item, 0);
	}

	@Path("/{id}")
	@PUT
	@PathParam("id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editItem(@PathParam("id") int id, Item item) {
		if (ItemDb.getItem(id) != null) {
			return ItemUtil.addOrEditItem(item, id);
		}
		return ResponseUtil.generateResponse(404, "Item not available");
	}

	@Path("/{id}")
	@DELETE
	@PathParam("id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteItem(@PathParam("id") int id) {
		System.out.println(" delete item id " + id);
		List<Invoice> invoiceList = InvoiceDb.getItemInvoice(id);
		System.out.println("invoice list " + invoiceList);
		if (!invoiceList.isEmpty()) {
			return ResponseUtil.generateResponse(409, "Unable to delete item since it has invoice associated");
		}
		if (ItemDb.deleteItem(id)) {
			return ResponseUtil.generateResponse(200, "Item deletion success");
		}
		return ResponseUtil.generateResponse(404, "Item not found");
	}
}
