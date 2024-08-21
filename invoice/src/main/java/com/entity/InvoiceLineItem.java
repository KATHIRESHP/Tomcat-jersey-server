package com.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement
public class InvoiceLineItem {
	@SerializedName("line_item_id")
	private int lineItemId;
	@SerializedName("invoice_id")
	private int invoiceid;
	@SerializedName("item_id")
	private int itemId;
	@SerializedName("item_name")
	private String itemName;
	private int rate;
	private int quantity = 1;
	private int amount;
	private static int idIndex = 1;
	
	@XmlElement(name = "line_item_id")
	public int getLineItemId() {
		return lineItemId;
	}
	public void setLineItemId() {
		this.lineItemId = idIndex++;
	}
	public void setLineItemId(int id) {
		this.lineItemId = id;
	}
	
	@XmlElement(name = "item_id")
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	@XmlElement(name = "invoice_id")
	public int getInvoiceid() {
		return invoiceid;
	}
	
	public void setInvoiceid(int invoiceid) {
		this.invoiceid = invoiceid;
	}
	
	@XmlElement(name = "item_name")
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
}
