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
		return ResponseUtil.generateResponse(404, "Invoice not found");
	}

	public static Response addOrEditInvoice(Invoice invoice, int invoiceId) {
		List<Error> errorList = SecurityUtil.validateInvoice(invoice);
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
			if (lineItem.getLineItemId() == 0 || invoiceId == 0 || LineItemDb.getInvoiceLineItem(lineItem.getLineItemId(), invoiceId) == null) {
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
		invoice.setTotal(totalAmount);
	}

	public static Response getInvoices(UriInfo uriInfo) {
		List<Error> errorList = SecurityUtil.validateRequestParams(uriInfo, Invoice.getAllowedParameters(), Invoice.getAllowedFilterMap());
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();

		String criteria = QueryUtil.handleParamCriteria(queryParamsMap, Invoice.getAllowedFilterMap(), "InvoiceTable");
		String orderBy = QueryUtil.handleParamSortOrder(queryParamsMap, Invoice.getAllowedSortMap(), "InvoiceTable", errorList);
		String pageLimit = QueryUtil.handlePagination(queryParamsMap, errorList);

		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}

		List<Invoice> invoiceList = InvoiceDb.getInvoices(criteria, orderBy, pageLimit);
		return ResponseUtil.generateResponse(200, "Invoice retrieval success", Invoice.responseKey, invoiceList);
	}
}
