package com.database;

import com.entity.*;
import com.util.QueryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InvoiceDb
{
	private static final HashMap<String, String> queryMap = new HashMap<>();

	static {
		initializeQueryMap();
	}

	private static void initializeQueryMap()
	{
		String selectAllQuery = "Select InvoiceTable.contactId, InvoiceTable.invoiceId,  InvoiceTable.total,  InvoiceTable.status, ItemTable.itemId, ItemTable.itemName, LineItemTable.rate, LineItemTable.quantity, LineItemTable.amount, LineItemTable.lineItemId from InvoiceTable "
			+ "join ContactTable on InvoiceTable.contactId = ContactTable.contactId "
			+ "join LineItemTable on InvoiceTable.invoiceId = LineItemTable.invoiceId "
			+ "join ItemTable on LineItemTable.itemId = ItemTable.itemId ";
		String invoiceEditQuery = "update InvoiceTable set contactId = ?, total = ? where InvoiceTable.invoiceId = ? ";
		String invoiceDeleteQuery = "delete from InvoiceTable where InvoiceTable.invoiceId = ? ";
		String updateInvoiceStatus = "update InvoiceTable set status = ? where invoiceId = ? ";
		String insertQuery = "insert into InvoiceTable (invoiceId, contactId, total) values (?, ?, ?)";

		queryMap.put("SelectAllQuery", selectAllQuery);
		queryMap.put("EditQuery", invoiceEditQuery);
		queryMap.put("DeleteQuery", invoiceDeleteQuery);
		queryMap.put("ChangeStatusQuery", updateInvoiceStatus);
		queryMap.put("InsertQuery", insertQuery);
	}

	public static List<Invoice> getInvoices(String criteria, String orderBy, String pageLimit)
	{
		String query = queryMap.get("SelectAllQuery");
		query = QueryUtil.appendCriOrderLimit(query, criteria, orderBy, pageLimit);
		return getInvoiceList(query, 0);
	}

	public static List<Invoice> getInvoiceList(String query, int id)
	{
		LinkedHashMap<Integer, Invoice> invoicesMap = new LinkedHashMap<>();
		try
		{
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			if(id != 0)
			{
				pst.setInt(1, id);
			}
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				Invoice invoice;
				if(invoicesMap.containsKey(rs.getInt("invoiceId")))
				{
					invoice = invoicesMap.get(rs.getInt("invoiceId"));
				}
				else
				{
					invoice = new Invoice();
					invoicesMap.put(rs.getInt("invoiceId"), invoice);
					setInvoiceValues(invoice, rs);
				}
				setLineItemValue(invoice, rs);
			}
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>(invoicesMap.values());
	}

	public static Invoice getInvoice(int id)
	{
		String query = queryMap.get("SelectAllQuery") + " where InvoiceTable.invoiceId = ?";
		try
		{
			System.out.println(query);
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			Invoice invoice = null;
			while(rs.next())
			{
				if(invoice == null)
				{
					invoice = new Invoice();
					setInvoiceValues(invoice, rs);
				}
				setLineItemValue(invoice, rs);
			}
			return invoice;
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void setInvoiceValues(Invoice invoice, ResultSet rs) throws SQLException
	{
		invoice.setInvoiceId(rs.getInt("invoiceId"));
		invoice.setContactId(rs.getInt("contactId"));
		invoice.setTotal(rs.getInt("total"));
		invoice.setStatus(rs.getString("status"));
	}

	private static void setLineItemValue(Invoice invoice, ResultSet rs) throws SQLException
	{
		InvoiceLineItem lineItem = new InvoiceLineItem();
		lineItem.setInvoiceid(rs.getInt("invoiceId"));
		lineItem.setLineItemId(rs.getInt("lineItemId"));
		lineItem.setItemId(rs.getInt("itemId"));
		lineItem.setQuantity(rs.getInt("quantity"));
		lineItem.setRate(rs.getInt("rate"));
		lineItem.setAmount(rs.getInt("amount"));
		lineItem.setItemName(rs.getString("itemName"));
		invoice.addLineItem(lineItem);
	}

	public static boolean createInvoice(Invoice invoice)
	{
		String query = queryMap.get("InsertQuery");
		Connection con = SqlConnection.getConnection();
		try
		{
			con.setAutoCommit(false);
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, invoice.getInvoiceId());
			pst.setInt(2, invoice.getContactId());
			pst.setInt(3, invoice.getTotal());
			if(pst.executeUpdate() > 0)
			{
				if(LineItemDb.addLineItems(invoice.getLineItems(), invoice.getInvoiceId()))
				{
					con.commit();
					return true;
				}
			}
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteInvoice(int id)
	{
		String query = queryMap.get("DeleteQuery");
		try
		{
			System.out.println(query);
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			return pst.executeUpdate() > 0;
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateInvoice(int id, Invoice invoice)
	{
		try
		{
			Connection con = SqlConnection.getConnection();
			con.setAutoCommit(false);

			String updateInvoiceQuery = "update InvoiceTable set total = ?, contactId = ? where invoiceId = ?";
			PreparedStatement pst = con.prepareStatement(updateInvoiceQuery);
			pst.setInt(1, invoice.getTotal());
			pst.setInt(2, invoice.getContactId());
			pst.setInt(3, id);
			pst.executeUpdate();
			pst.close();

			String selectLineItemsQuery = "select lineItemId from LineItemTable where invoiceId = ?";
			pst = con.prepareStatement(selectLineItemsQuery);
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();

			Map<Integer, Boolean> existingLineItems = new HashMap<Integer, Boolean>();

			while(rs.next())
			{
				existingLineItems.put(rs.getInt("lineItemId"), false);
			}
			rs.close();
			pst.close();

			List<InvoiceLineItem> lineItemCreationList = new ArrayList<InvoiceLineItem>();
			List<InvoiceLineItem> lineItemUpdationList = new ArrayList<InvoiceLineItem>();

			for(InvoiceLineItem lineItem : invoice.getLineItems())
			{
				if(lineItem.getLineItemId() != 0 && existingLineItems.containsKey(lineItem.getLineItemId()))
				{
					lineItemUpdationList.add(lineItem);
				}
				else
				{
					lineItemCreationList.add(lineItem);
				}
				existingLineItems.put(lineItem.getLineItemId(), true);
			}

			List<Integer> lineItemDeletionList = new ArrayList<Integer>();
			for(Map.Entry<Integer, Boolean> entry : existingLineItems.entrySet())
			{
				if(!entry.getValue())
				{
					lineItemDeletionList.add(entry.getKey());
				}
			}

			if(!LineItemDb.addLineItems(lineItemCreationList, id) || !LineItemDb.updateLineItem(lineItemUpdationList, id) || !LineItemDb.deleteLineItems(lineItemDeletionList))
			{
				return false;
			}
			con.commit();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static List<Invoice> getItemInvoice(int itemId)
	{
		String query = queryMap.get("SelectAllQuery") + " where ItemTable.itemId = ?";
		return getInvoiceList(query, itemId);
	}

	public static List<Invoice> getContactInvoice(int contactId)
	{
		String query = queryMap.get("SelectAllQuery") + " where ContactTable.contactId = ?";
		return getInvoiceList(query, contactId);
	}

	public static boolean changeInvoiceStatus(int id, String status)
	{
		String query = queryMap.get("ChangeStatusQuery");
		try
		{
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setString(1, status);
			pst.setInt(2, id);
			return pst.executeUpdate() > 0;
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
