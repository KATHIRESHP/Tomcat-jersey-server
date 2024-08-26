package com.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.database.ContactDb;
import com.database.InvoiceDb;
import com.database.ItemDb;
import com.database.LineItemDb;
import com.entity.Contact;
import com.entity.Error;
import com.entity.Invoice;
import com.entity.InvoiceLineItem;
import com.entity.Item;

public class InvoiceUtil { 

	public static Response changeInvoiceStatus(int id, String status) {
		Invoice invoice = InvoiceDb.getInvoice(id);
		if (invoice == null) {
			return ResponseUtil.generateResponse(404, "Invoice not found");
		}
		if (invoice.getStatus().equals(status)) {
			return ResponseUtil.generateResponse(200, "Invoice already in " + status + " status");
		}
		if (InvoiceDb.changeInvoiceStatus(id, status)) {
			return ResponseUtil.generateResponse(200, "Invoice status changed to " + status);
		}
		return ResponseUtil.generateResponse(500, "Error in updating invoice status to " + status);
	}

	public static Response getContact(int id) {
		Invoice invoice = InvoiceDb.getInvoice(id);
		if (invoice != null) {
			Contact contact = ContactDb.getContact(invoice.getContactId());
			return ResponseUtil.generateResponse(200, "Invoice contact retrival success", Contact.responseKey, contact);
		}
		return null;
	}

	public static Response addOrEditInvoice(Invoice invoice, int invoiceId) {
		List<Error> errorList = validateInvoice(invoice);
		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid data", "error", errorList);
		}
		calculateInvoice(invoice, invoiceId);
		if (invoiceId != 0) {
			invoice.setInvoiceId(invoiceId);
			if (InvoiceDb.updateInvoice(invoiceId, invoice)) {
				Invoice invoiceUpdated = InvoiceDb.getInvoice(invoiceId);
				return ResponseUtil.generateResponse(200, "Invoice updation success", Invoice.responseKey,
						invoiceUpdated);
			}
			return ResponseUtil.generateResponse(500, "Error while updating invoice");
		}
		invoice.setInvoiceId();
		if (InvoiceDb.createInvoice(invoice)) {
			Invoice invoiceCreated = InvoiceDb.getInvoice(invoice.getInvoiceId());
			return ResponseUtil.generateResponse(201, "Invoice creation success", Invoice.responseKey, invoiceCreated);
		}
		return ResponseUtil.generateResponse(500, "Error while  creating invoice");
	}

	public static void calculateInvoice(Invoice invoice, int invoiceId) {

		Map<Integer, InvoiceLineItem> lineItems = new HashMap<Integer, InvoiceLineItem>();
		int totalAmount = 0;
		for (InvoiceLineItem lineItem : invoice.getLineItems()) {
			if (lineItem.getLineItemId() == 0 || invoiceId == 0
					|| LineItemDb.getInvoiceLineItem(lineItem.getLineItemId(), invoiceId) == null) {
				System.out.println("Setting up lineitem id for the line item with id: " + lineItem.getLineItemId());
				lineItem.setLineItemId();
			}
			InvoiceLineItemUtil.calculateAmount(lineItem);
			if (lineItems.containsKey(lineItem.getLineItemId())) {
				totalAmount -= lineItems.get(lineItem.getLineItemId()).getAmount();
			}
			lineItems.put(lineItem.getLineItemId(), lineItem);
			totalAmount += lineItem.getAmount();
		}
		invoice.setTotalAmount(totalAmount);
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
			error.setMessage("Invoice has associated contact person");
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

	public static Response getInvoices(UriInfo uriInfo) {
		List<Error> errorList = SecurityUtil.validateRequestParams(uriInfo, Invoice.getAllowedParameters(), Invoice.getAllowedFilterMap());
		String criteria = "";
		String orderBy = "";
		
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();
		
		for (Map.Entry<String, List<String>> entry : queryParamsMap.entrySet()) {
			String key = entry.getKey();
			System.out.println("Entry key: " + key + " Criteria: " + criteria);
			if (Invoice.getAllowedFilterMap().containsKey(key)) {
				for (String value : entry.getValue()) {
					criteria = QueryUtil.getCriteria(criteria, "InvoiceTable." + Invoice.getAllowedFilterMap().get(key), value, "and", "=");
				}
			}
		}
		if (queryParamsMap.containsKey("sort") && queryParamsMap.containsKey("sort_order")) {
			if (!Invoice.getAllowedSortMap().containsKey(queryParamsMap.get("sort").get(0))) {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Column " + queryParamsMap.get("sort").get(0) + " was not sortable");
				errorList.add(error);
			} else {
				String sortOrder = queryParamsMap.get("sort_order").get(0);
				String sortCol = Invoice.getAllowedSortMap().get(queryParamsMap.get("sort").get(0));
				sortOrder = sortOrder.toUpperCase();
				if (!sortCol.isEmpty()) {
					orderBy = " order by InvoiceTable." + sortCol + " " + (sortOrder.equals("D") ? "desc" : "");
				}
			}
		}
		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}
		List<Invoice> invoiceList = InvoiceDb.getInvoices(criteria, orderBy);
		return ResponseUtil.generateResponse(200, "Invoice retrieval success", Invoice.responseKey, invoiceList);
	}
}
