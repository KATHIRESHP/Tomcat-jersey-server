package com.util;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.entity.Contact;
import com.entity.Error;
import com.entity.Invoice;

public class InvoiceUtil { 

	public static Response changeInvoiceStatus(int id, String status) {
		Invoice invoice = Invoice.getInvoice(id);
		if (invoice == null) {
			return ResponseUtil.generateResponse(404, "Invoice not found");
		}
		if (invoice.getStatus().equals(status)) {
			return ResponseUtil.generateResponse(200, "Invoice already in " + status + " status");
		}
		if (invoice.changeStatus(status)) {
			return ResponseUtil.generateResponse(200, "Invoice status changed to " + status);
		}
		return ResponseUtil.generateResponse(500, "Error in updating invoice status to " + status);
	}

	public static Response addOrEditInvoice(Invoice invoice, int invoiceId) {
		List<Error> errorList = invoice.validateInvoice();
		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid data", "error", errorList);
		}
		invoice.calculateInvoice(invoiceId);
		boolean isUpdate = invoiceId != 0;
		boolean processSuccess;
		if (isUpdate) {
			processSuccess = invoice.update(invoiceId);
		}
		else {
			processSuccess = invoice.create();
			invoiceId = invoice.getInvoiceId();
		}
		if (processSuccess) {
			String responseStr = isUpdate ? "Invoice updated" : "Invoice created";
			int responseCode = isUpdate ? 200 : 201;
			return ResponseUtil.generateResponse(responseCode, responseStr, Invoice.responseKey, Invoice.getInvoice(invoiceId));
		}
		return ResponseUtil.generateResponse(500, "Error in " + ((isUpdate) ? "updating" : "creating") + " invoice");
	}

	public static Response getInvoices(UriInfo uriInfo) {
		List<Error> errorList = SecurityUtil.validateRequestParams(uriInfo, Invoice.getAllowedParameters(), Invoice.getAllowedFilterMap(), Invoice.getAllowedSortMap());
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();

		String criteria = QueryUtil.handleParamCriteria(queryParamsMap, Invoice.getAllowedFilterMap(), "InvoiceTable");
		String orderBy = QueryUtil.handleParamSortOrder(queryParamsMap, Invoice.getAllowedSortMap(), "InvoiceTable", errorList);
		String pageLimit = QueryUtil.handlePagination(queryParamsMap, errorList);

		if (!errorList.isEmpty()) {
			return ResponseUtil.generateResponse(400, "Invalid request", "error", errorList);
		}

		List<Invoice> invoiceList = Invoice.getInvoices(criteria, orderBy, pageLimit);
		return ResponseUtil.generateResponse(200, "Invoice retrieval success", Invoice.responseKey, invoiceList);
	}
}
