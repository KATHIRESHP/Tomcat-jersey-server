package com.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Invoice {
	
	@SerializedName("invoice_id")
	private int invoiceId;
	
	@SerializedName("contact_id")
	private int contactId;
	private int total;
	
	private String status;
	@SerializedName("line_items")
	private List<InvoiceLineItem> lineItems = new ArrayList<InvoiceLineItem>();

	private static Map<String,String> allowedFilterMap = new HashMap<String, String>();
	private static Map<String,String> allowedSortMap = new HashMap<String, String>();
	private static List<String> allowedParameters = new ArrayList<String>();
	private static int idIndex = 1;
	public static String responseKey = "invoices";


	static {
		initializeStatics();
	}

	private static void initializeStatics()
	{
		allowedParameters.add("sort");
		allowedParameters.add("sort_order");

		allowedFilterMap.put("contact_id", "contactId");
		allowedFilterMap.put("invoice_id", "invoiceId");
		allowedFilterMap.put("status", "status");

		allowedSortMap.put("contact_id", "contactId");
		allowedSortMap.put("invoice_id", "invoiceId");
		allowedSortMap.put("total", "total");
	}

	@XmlElement(name = "invoice_id")
	public int getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId() {
		this.invoiceId = idIndex++;
	}
	public void setInvoiceId(int id) {
		this.invoiceId = id;
	}
	
	@XmlElement(name = "contact_id")
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public int getTotalAmount() {
		return total;
	}
	public void setTotalAmount(int total) {
		this.total = total;
	}
	
	@XmlElement(name = "line_items")
	public List<InvoiceLineItem> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<InvoiceLineItem> lineItems) {
		this.lineItems = lineItems;
	}
	public void addLineItem(InvoiceLineItem lineItem) {
		this.getLineItems().add(lineItem);
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public void printLineItem(){
		System.out.println("printing invoice line items");
		for (InvoiceLineItem lineItem: this.getLineItems()) {
			System.out.println("line item id: "+lineItem.getLineItemId() + " invoice id: " + lineItem.getInvoiceid());
		}

	}

	public static Map<String, String> getAllowedFilterMap()
	{
		return allowedFilterMap;
	}

	public static Map<String, String> getAllowedSortMap()
	{
		return allowedSortMap;
	}
	public static List<String> getAllowedParameters() {
		return allowedParameters;
	}
}
