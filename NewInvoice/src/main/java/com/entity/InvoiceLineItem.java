package com.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.database.BaseDb;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlRootElement
@JsonPropertyOrder({"line_item_id", "invoice_id", "item_id", "item_name", "rate", "quantity", "amount"})
public class InvoiceLineItem {
	@JsonProperty("line_item_id")
	private int lineItemId;
	@JsonProperty("invoice_id")
	private int invoiceId;
	@JsonProperty("item_id")
	private int itemId;
	@JsonProperty("item_name")
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
	public int getInvoiceId() {
		return invoiceId;
	}
	
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}

	@XmlElement(name = "item_name")
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	public void calculateAmount()  throws Exception
	{
		if (this.getRate() < 0) {
			Item item = Item.getItem(this.getItemId());
			if (item != null) {
				this.setRate(item.getPrice());
			}
		}
		if (this.getQuantity() <= 0) {
			this.setQuantity(1);
		}
		this.setAmount(this.getQuantity() * this.getRate());
	}

	private static final String selectInvoiceLineItemQuery = "select lineItemId, invoiceId, rate, quantity, amount, itemId from LineItemTable where lineItemId = ? and invoiceId = ?";
	private static final String insertQuery = "insert into LineItemTable (lineItemId, invoiceId, itemId, rate, quantity, amount) values (?, ?, ?, ?, ?, ?)";
	private static final String updateQuery = "update LineItemTable set itemId = ?, rate = ?, quantity = ?, amount = ? where lineItemId = ?";


	public boolean create(int invoiceId)
	{
		return BaseDb.executeUpdate(insertQuery, this.getLineItemId(), invoiceId, this.getItemId(), this.getRate(), this.getQuantity(), this.getAmount());
	}


	public boolean update()
	{
		return BaseDb.executeUpdate(updateQuery, this.getItemId(), this.getRate(), this.getQuantity(), this.getAmount(), this.getLineItemId());
	}

	public static InvoiceLineItem getInvoiceLineItem(int lineItemId, int invoiceId)
	{
		try
		{
			ResultSet rs = BaseDb.executeQuery(selectInvoiceLineItemQuery, lineItemId, invoiceId);
			if (rs.next()) {
				return mapObject(rs);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean deleteLineItems(List<Integer> existingLineItems)
	{
		if(existingLineItems.isEmpty())
		{
			return true;
		}
		StringBuilder query = new StringBuilder("delete from LineItemTable where lineItemId in (");
		for(int i = 0; i < existingLineItems.size(); i++)
		{
			query.append(existingLineItems.get(i));
			if(i < existingLineItems.size() - 1)
			{
				query.append(",");
			}
		}
		query.append(")");
		return BaseDb.executeUpdate(query.toString());
	}

	private static InvoiceLineItem mapObject(ResultSet rs) throws Exception
	{
		InvoiceLineItem lineItem = new InvoiceLineItem();

		lineItem.setInvoiceId(rs.getInt("invoiceId"));
		lineItem.setLineItemId(rs.getInt("lineItemId"));
		lineItem.setItemId(rs.getInt("itemId"));
		lineItem.setQuantity(rs.getInt("quantity"));
		lineItem.setRate(rs.getInt("rate"));
		lineItem.setAmount(rs.getInt("amount"));

		return lineItem;
	}
}
