package com.database;

import com.entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InvoiceDb {
	private static HashMap<String, String> queryMap = new HashMap<String, String>();
	static {
		intializeQueryMap();
	}
	private static void intializeQueryMap() {
		String selectQuery = "Select InvoiceTable.contactId, InvoiceTable.invoiceId,  InvoiceTable.total,  InvoiceTable.status, ItemTable.itemId, ItemTable.itemName, LineItemTable.rate, LineItemTable.quantity, LineItemTable.amount, LineItemTable.lineItemId from InvoiceTable "
				+ "join Contacttable on InvoiceTable.contactId = Contacttable.contactId "
				+ "join LineItemTable on Invoicetable.invoiceId = LineItemTable.invoiceId "
				+ "join ItemTable on LineItemTable.itemId = ItemTable.itemId "
				+ "where InvoiceTable.invoiceId = ?";
		
		String selectAllQuery = "Select InvoiceTable.contactId, InvoiceTable.invoiceId,  InvoiceTable.total,  InvoiceTable.status, ItemTable.itemId, ItemTable.itemName, LineItemTable.rate, LineItemTable.quantity, LineItemTable.amount, LineItemTable.lineItemId from InvoiceTable "
				+ "join Contacttable on InvoiceTable.contactId = Contacttable.contactId "
				+ "join LineItemTable on Invoicetable.invoiceId = LineItemTable.invoiceId "
				+ "join ItemTable on LineItemTable.itemId = ItemTable.itemId ";
		
		String invoiceEditQuery = "update InvoiceTable set contactId = ?, total = ? "
				+ "where InvoiceTable.invoiceId = ? ";
		
		String invoiceDeleteQuery = "delete from InvoiceTable "
				+ "where InvoiceTable.invoiceId = ?";
		
		String updateInvoiceStatus = "update InvoiceTable set status = ? where invoiceId = ?";
		
		queryMap.put("SelectAllQuery", selectAllQuery);
		queryMap.put("SelectQuery", selectQuery);
		queryMap.put("EditQuery", invoiceEditQuery);
		queryMap.put("DeleteQuery", invoiceDeleteQuery);
		queryMap.put("ChangeStatusQuery", updateInvoiceStatus);
	}
	
	public static List<Invoice> getInvoices(String criteria, String orderBy) {
		System.out.println("getInvoices called");
		String query = queryMap.get("SelectAllQuery");
		if (!criteria.isEmpty()) {
			query += " where " + criteria;
		}
		if (!orderBy.isEmpty()) {
			query += orderBy;
		}
		List<Invoice> invoiceList = new ArrayList<Invoice>();
		try {
			System.out.println(query);
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			invoiceList = getInvoiceList(rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return invoiceList;
	}

	public static List<Invoice> getInvoiceList(ResultSet rs) {
		LinkedHashMap<Integer, Invoice> invoicesMap = new LinkedHashMap<Integer, Invoice>();
		System.out.println("getInvoiceList rs called");
		try {
			while(rs.next()) {
				Invoice invoice;
				if (invoicesMap.containsKey(rs.getInt("invoiceId"))) {
					invoice = invoicesMap.get(rs.getObject("invoiceId"));
				} else {
					invoice = new Invoice();
					invoicesMap.put(rs.getInt("invoiceId"), invoice);
					invoice.setInvoiceId(rs.getInt("invoiceId"));
					System.out.println("invoice id " + invoice.getInvoiceId());
					invoice.setContactId(rs.getInt("contactId"));
					invoice.setTotalAmount(rs.getInt("total"));
					invoice.setStatus(rs.getString("status"));
				}
				
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
		return new ArrayList<Invoice>(invoicesMap.values());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Invoice getInvoice(int id) {
		System.out.println("getInvoices id called");
		String query = queryMap.get("SelectQuery");
		try {
			System.out.println(query);
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			Invoice invoice = null;
			while(rs.next()) {
				if (invoice == null) {
					invoice = new Invoice();
					invoice.setInvoiceId(rs.getInt("invoiceId"));
					invoice.setContactId(rs.getInt("contactId"));
					invoice.setTotalAmount(rs.getInt("total"));
					invoice.setStatus(rs.getString("status"));
				}
				
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

			return invoice;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean createInvoice(Invoice invoice) {
		String query = "insert into InvoiceTable (invoiceId, contactId, total) values (?, ?, ?)";
		Connection con = SqlConnection.getConnection();
		try {
			con.setAutoCommit(false);
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, invoice.getInvoiceId());
			pst.setInt(2, invoice.getContactId());
			pst.setInt(3, invoice.getTotalAmount());
			if (pst.executeUpdate() > 0) {
				 if (LineItemDb.addLineItems(invoice.getLineItems(), invoice.getInvoiceId())) {
					 con.commit();
					 return true;
				 }
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean deleteInvoice(int id) {
		String query = queryMap.get("DeleteQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateInvoice(int id, Invoice invoice) {
		Connection con = null;
        PreparedStatement pst = null;

        System.out.println("Enter updateInvoice()");
        try {
            con = SqlConnection.getConnection();
            con.setAutoCommit(false);

            String updateInvoiceQuery = "update InvoiceTable set total = ? where invoiceId = ?";
            pst = con.prepareStatement(updateInvoiceQuery);
            pst.setInt(1, invoice.getTotalAmount());
            pst.setInt(2, id);
            pst.executeUpdate();
            pst.close();
            System.out.println("Invoice updated");

            String selectLineItemsQuery = "select lineItemId from LineItemTable where invoiceId = ?";
            pst = con.prepareStatement(selectLineItemsQuery);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            Map<Integer, Boolean> existingLineItems = new HashMap<Integer, Boolean>();
            while (rs.next()) {
				System.out.println("Existing line-item: "+ rs.getInt("lineItemId"));
                existingLineItems.put(rs.getInt("lineItemId"), false);
            }
            System.out.println("Existing line items mapped successfully");
            rs.close();
            pst.close();
            
            List<InvoiceLineItem> lineItemCreationList = new ArrayList<InvoiceLineItem>();
            List<InvoiceLineItem> lineItemUpdationList = new ArrayList<InvoiceLineItem>();
            for (InvoiceLineItem lineItem : invoice.getLineItems()) {
            	System.out.println("Incoming line-item: " + lineItem.getLineItemId());
                if (lineItem.getLineItemId() != 0 && existingLineItems.containsKey(lineItem.getLineItemId())) {
                	lineItemUpdationList.add(lineItem);
                	System.out.println("Line item added to update");
                } else {
                	lineItemCreationList.add(lineItem);
                	System.out.println("line item added to create");
                }
                existingLineItems.put(lineItem.getLineItemId(), true);
                System.out.println("existing line item map setted to true");
            }
			LineItemDb.addLineItems(lineItemCreationList, id);
            LineItemDb.updateLineItem(lineItemUpdationList, id);

            List<Integer> lineItemDeletionList = new ArrayList<Integer>();
            for (Map.Entry<Integer, Boolean> entry : existingLineItems.entrySet()) {
                if (!entry.getValue()) {
                	lineItemDeletionList.add(entry.getKey());
                    System.out.println("Lineitem deleted");
                }
            }
            LineItemDb.deleteLineItems(lineItemDeletionList);
            con.commit();
            return true;
        } catch(Exception e) {
        	e.printStackTrace();
        }
        return false;
	}
	
	public static List<Invoice> getItemInvoice(int itemId) {
		String query = "Select * from InvoiceTable "
				+ "join Contacttable on InvoiceTable.contactId = Contacttable.contactId "
				+ "join LineItemTable on Invoicetable.invoiceId = LineItemTable.invoiceId "
				+ "join ItemTable on LineItemTable.itemId = ItemTable.itemId "
				+ "where LineItemTable.itemId = ?";
		List<Invoice> invoiceList = new ArrayList<Invoice>();
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, itemId);
			ResultSet rs = pst.executeQuery();
			invoiceList = getInvoiceList(rs);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invoiceList;
	}

	public static List<Invoice> getContactInvoice(int contactId) {
		String query = "Select * from InvoiceTable "
				+ "join Contacttable on InvoiceTable.contactId = Contacttable.contactId "
				+ "join LineItemTable on Invoicetable.invoiceId = LineItemTable.invoiceId "
				+ "join ItemTable on LineItemTable.itemId = ItemTable.itemId "
				+ "where InvoiceTable.contactId = ?";
		List<Invoice> invoiceList = new ArrayList<Invoice>();
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, contactId);
			ResultSet rs = pst.executeQuery();
			invoiceList = getInvoiceList(rs);
			System.out.println("Contact associated invoice count " + invoiceList.size());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return invoiceList;
	}


	public static boolean changeInvoiceStatus(int id, String status) {
		String query = queryMap.get("ChangeStatusQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setString(1, status);
			pst.setInt(2, id);
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
