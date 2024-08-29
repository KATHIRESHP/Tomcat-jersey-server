package com.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.database.BaseDb;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.util.QueryUtil;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonPropertyOrder({"invoice_id", "contact_id", "total_amount", "status", "line_items"})
public class Invoice
{

	@JsonProperty("invoice_id")
	private int invoiceId;
	@JsonProperty("contact_id")
	private int contactId;
	@JsonProperty("total_amount")
	private int total;

	private String status;
	@JsonProperty("line_items")
	private List<InvoiceLineItem> lineItems = new ArrayList<InvoiceLineItem>();

	private static Map<String, String> allowedFilterMap = new HashMap<String, String>();
	private static Map<String, String> allowedSortMap = new HashMap<String, String>();
	private static List<String> allowedParameters = new ArrayList<String>();
	private static int idIndex = 1;
	public static String responseKey = "invoices";

	static
	{
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
	public int getInvoiceId()
	{
		return invoiceId;
	}

	public void setInvoiceId()
	{
		this.invoiceId = idIndex++;
	}

	public void setInvoiceId(int id)
	{
		this.invoiceId = id;
	}

	@XmlElement(name = "contact_id")
	public int getContactId()
	{
		return contactId;
	}

	public void setContactId(int contactId)
	{
		this.contactId = contactId;
	}

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}

	@XmlElement(name = "line_items")
	public List<InvoiceLineItem> getLineItems()
	{
		return lineItems;
	}

	public void setLineItems(List<InvoiceLineItem> lineItems)
	{
		this.lineItems = lineItems;
	}

	public void addLineItem(InvoiceLineItem lineItem)
	{
		this.getLineItems().add(lineItem);
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public static Map<String, String> getAllowedFilterMap()
	{
		return allowedFilterMap;
	}

	public static Map<String, String> getAllowedSortMap()
	{
		return allowedSortMap;
	}

	public static List<String> getAllowedParameters()
	{
		return allowedParameters;
	}

	@Override
	public String toString()
	{
		return "Contact id: " + this.getContactId() + " lineItems length: " + this.getLineItems().size();
	}

	public List<Error> validateInvoice() throws Exception
	{
		List<InvoiceLineItem> invoiceLineItems = this.getLineItems();
		ArrayList<Integer> itemIdList = new ArrayList<Integer>();
		List<Error> errorList = new ArrayList<Error>();
		if(invoiceLineItems.isEmpty() || invoiceLineItems.size() > 10)
		{
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Invoice has no line items or more than 10");
			errorList.add(error);
		}
		if(Contact.getContact(this.getContactId()) == null)
		{
			Error error = new Error();
			error.setCode(400);
			error.setMessage("Contact person associated with invoice not found");
			errorList.add(error);
		}
		for(InvoiceLineItem lineItem : invoiceLineItems)
		{
			itemIdList.add(lineItem.getItemId());
		}

		List<Item> itemList = Item.getItems(itemIdList);
		List<Integer> dbItemIdList = new ArrayList<Integer>();
		for(Item item : itemList)
		{
			dbItemIdList.add(item.getItemId());
		}

		for(InvoiceLineItem lineItem : invoiceLineItems)
		{
			if(!dbItemIdList.contains(lineItem.getItemId()))
			{
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Item with id: " + lineItem.getItemId() + " not found");
				errorList.add(error);
			}
		}
		return errorList;
	}

	public void calculateInvoice(int invoiceId) throws Exception
	{
		Map<Integer, InvoiceLineItem> lineItems = new HashMap<Integer, InvoiceLineItem>();
		int totalAmount = 0;
		for(InvoiceLineItem lineItem : this.getLineItems())
		{
			if(lineItem.getLineItemId() == 0 || invoiceId == 0 || InvoiceLineItem.getInvoiceLineItem(lineItem.getLineItemId(), invoiceId) == null)
			{
				System.out.println("Setting up line item id for the line item with id: " + lineItem.getLineItemId() + " invoiceId: " + invoiceId);
				lineItem.setLineItemId();
			}
			if(!lineItems.containsKey(lineItem.getLineItemId()))
			{
				lineItem.calculateAmount();
				// totalAmount -= lineItems.get(lineItem.getLineItemId()).getAmount();
				lineItems.put(lineItem.getLineItemId(), lineItem);
				totalAmount += lineItem.getAmount();
			}
		}
		this.setTotal(totalAmount);
	}

	private static final String selectAllQuery = "Select InvoiceTable.contactId, InvoiceTable.invoiceId,  InvoiceTable.total,  InvoiceTable.status, ItemTable.itemId, ItemTable.itemName, LineItemTable.rate, LineItemTable.quantity, LineItemTable.amount, LineItemTable.lineItemId from InvoiceTable "
		+ "join ContactTable on InvoiceTable.contactId = ContactTable.contactId "
		+ "join LineItemTable on InvoiceTable.invoiceId = LineItemTable.invoiceId "
		+ "join ItemTable on LineItemTable.itemId = ItemTable.itemId ";
	private static final String editQuery = "update InvoiceTable set contactId = ?, total = ? where InvoiceTable.invoiceId = ? ";
	private static final String insertQuery = "insert into InvoiceTable (invoiceId, contactId, total) values (?, ?, ?)";
	private static final String deleteQuery = "delete from InvoiceTable where invoiceId = ?";
	private static final String updateStatusQuery = "update InvoiceTable set status = ? where invoiceId = ? ";

	public static List<Invoice> getItemInvoices(int id)
	{
		String query = selectAllQuery + " where ItemTable.itemId = ?";
		try
		{
			return getInvoices(BaseDb.executeQuery(query, id));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static List<Invoice> getContactInvoices(int id)
	{
		String query = selectAllQuery + " where ContactTable.ContactId = ?";
		try
		{
			return getInvoices(BaseDb.executeQuery(query, id));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static List<Invoice> getInvoices(String criteria, String orderBy, String pageLimit) throws Exception
	{
		String query = selectAllQuery;
		query = QueryUtil.appendCriOrderLimit(query, criteria, orderBy, pageLimit);
		try
		{
			ResultSet rs = BaseDb.executeQuery(query);
			return getInvoices(rs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private static List<Invoice> getInvoices(ResultSet rs) throws Exception
	{
		LinkedHashMap<Integer, Invoice> invoiceMap = new LinkedHashMap<>();
		while(rs.next())
		{
			Invoice invoice = mapObject(rs);
			if(invoiceMap.containsKey(invoice.getInvoiceId()))
			{
				invoiceMap.get(invoice.getInvoiceId()).addLineItem(invoice.getLineItems().get(0));
			}
			else
			{
				invoiceMap.put(invoice.getInvoiceId(), invoice);
			}
		}
		return new ArrayList<Invoice>(invoiceMap.values());
	}

	public static Invoice getInvoice(int invoiceId)
	{
		String query = selectAllQuery + " where InvoiceTable.invoiceId = ?";
		try
		{
			Invoice invoice = null;
			ResultSet rs = BaseDb.executeQuery(query, invoiceId);
			while(rs.next())
			{
				Invoice mapInvoice = mapObject(rs);
				if(invoice == null)
				{
					invoice = mapInvoice;
				}
				else
				{
					invoice.addLineItem(mapInvoice.getLineItems().get(0));
				}
			}
			return invoice;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public boolean create() throws Exception
	{
		this.setInvoiceId();
		if(BaseDb.executeUpdate(insertQuery, this.invoiceId, this.getContactId(), this.getTotal()))
		{
			for(InvoiceLineItem lineItem : this.getLineItems())
			{
				if(!lineItem.create(this.getInvoiceId()))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean changeStatus(String status)
	{
		return BaseDb.executeUpdate(updateStatusQuery, status, this.getInvoiceId());
	}

	public boolean update(int invoiceId)
	{
		if(BaseDb.executeUpdate(editQuery, this.getContactId(), this.getTotal(), invoiceId))
		{
			try
			{
				String selectLineItemsQuery = "select lineItemId from LineItemTable where invoiceId = ?";
				ResultSet rs = BaseDb.executeQuery(selectLineItemsQuery, invoiceId);
				List<Integer> existingLineItems = new ArrayList<>();
				while(rs.next())
				{
					existingLineItems.add(rs.getInt("lineItemId"));
				}
				rs.close();
				for(InvoiceLineItem lineItem : this.getLineItems())
				{
					System.out.println("line item id: " + lineItem.getLineItemId());
					if(lineItem.getLineItemId() != 0 && existingLineItems.contains(lineItem.getLineItemId()))
					{
						lineItem.update();
					}
					else
					{
						lineItem.create(invoiceId);
					}
					existingLineItems.remove(new Integer(lineItem.getLineItemId()));
				}
				return InvoiceLineItem.deleteLineItems(existingLineItems);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean delete()
	{
		return BaseDb.executeUpdate(deleteQuery, this.getInvoiceId());
	}

	private static Invoice mapObject(ResultSet rs) throws Exception
	{
		Invoice invoice = new Invoice();
		invoice.setInvoiceId(rs.getInt("invoiceId"));
		invoice.setContactId(rs.getInt("contactId"));
		invoice.setTotal(rs.getInt("total"));
		invoice.setStatus(rs.getString("status"));

		InvoiceLineItem lineItem = new InvoiceLineItem();
		lineItem.setInvoiceId(rs.getInt("invoiceId"));
		lineItem.setLineItemId(rs.getInt("lineItemId"));
		lineItem.setItemId(rs.getInt("itemId"));
		lineItem.setQuantity(rs.getInt("quantity"));
		lineItem.setRate(rs.getInt("rate"));
		lineItem.setAmount(rs.getInt("amount"));
		lineItem.setItemName(rs.getString("itemName"));
		invoice.addLineItem(lineItem);
		return invoice;
	}
}
