package com.entity;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonPropertyOrder({"invoice_id", "contact_id", "total_amount", "status", "line_items"})
public class Invoice {

	@JsonProperty("invoice_id")
	private int invoiceId;
	@JsonProperty("contact_id")
	private int contactId;
	@JsonProperty("total_amount")
	private int total;
	
	private String status;
	@JsonProperty("line_items")
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
		allowedParameters.add("page");
		allowedParameters.add("size");

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
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	@XmlElement( name = "line_items")
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
	
	@Override
	public String toString() {
		return "Contact id: " + this.getContactId() + " lineItems length: " + this.getLineItems().size();
	}
}
