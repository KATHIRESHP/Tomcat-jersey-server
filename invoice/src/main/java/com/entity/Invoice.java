package com.entity;

import java.util.ArrayList;
import java.util.List;

import com.database.ContactDb;
import com.database.ItemDb;
import com.database.LineItemDb;
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
	private static int idIndex = 1;
	public static String responseKey = "invoices";
	
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
}
