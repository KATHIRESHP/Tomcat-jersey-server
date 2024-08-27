package com.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.entity.InvoiceLineItem;
import com.util.InvoiceLineItemUtil;

public class LineItemDb {

	private static final HashMap<String, String> queryMap = new HashMap<String, String>();
	static {
		initializeQueryMap();
	}
	private static void initializeQueryMap() {
		String selectInvoiceLineItemQuery = "select lineItemId, invoiceId, rate, quantity, amount from LineItemTable where lineItemId = ? and invoiceId = ?";
		String selectQuery = "select lineItemId, invoiceId, rate, quantity, amount from LineItemTable where lineItemId = ?";
		String updateQuery = "update LineItemTable set itemId = ?, rate = ?, quantity = ?, amount = ? where lineItemId = ?";
		String deleteQuery = "delete from LineItemTable where lineItemId = ?";

		queryMap.put("SelectInvoiceLineItemQuery", selectInvoiceLineItemQuery);
		queryMap.put("SelectQuery", selectQuery);
		queryMap.put("UpdateQuery", updateQuery);
		queryMap.put("DeleteQuery", deleteQuery);
	}

	public static InvoiceLineItem getInvoiceLineItem(int lineItemId, int invoiceId) {
		String query = queryMap.get("SelectInvoiceLineItemQuery");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			pst.setInt(1, lineItemId);
			pst.setInt(2, invoiceId);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				InvoiceLineItem lineItem = new InvoiceLineItem();
				lineItem.setAmount(rs.getInt("amount"));
				lineItem.setInvoiceid(rs.getInt("invoiceId"));
				lineItem.setLineItemId(rs.getInt("lineItemId"));
				lineItem.setItemId(rs.getInt("itemId"));
				lineItem.setRate(rs.getInt("rate"));
				lineItem.setQuantity(rs.getInt("quantity"));
				return lineItem;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	public static boolean addLineItems(List<InvoiceLineItem> lineItemList, int invoiceId) {
		if (lineItemList.isEmpty()) {
			return true;
		}
		 StringBuilder query = new StringBuilder("insert into LineItemTable (lineItemId, invoiceId, itemId, rate, quantity, amount) values ");
		 System.out.println(lineItemList);
         for (int i = 0; i < lineItemList.size(); i++) {
        	 query.append("(?, ?, ?, ?, ?, ?),");
         }
         query.setLength(query.length() - 1);
         PreparedStatement pst;
		try {
			pst = SqlConnection.getConnection().prepareStatement(query.toString());
			int index = 1;
			InvoiceLineItemUtil.printLineItems(lineItemList);
			for (InvoiceLineItem lineItem : lineItemList) {
				pst.setInt(index++, lineItem.getLineItemId());
				pst.setInt(index++, invoiceId);
				pst.setInt(index++, lineItem.getItemId());
				pst.setInt(index++, lineItem.getRate());
				pst.setInt(index++, lineItem.getQuantity());
				pst.setInt(index++, lineItem.getAmount());
			}
			System.out.println("Item added to query " + query);
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Line addition failed");
		return false;
	}

	public static boolean updateLineItem(List<InvoiceLineItem> lineItemUpdationList, int id) {
		if (lineItemUpdationList.size() <= 0) {
			return true;
		}
		
		String query = "update LineItemTable set itemId = ?, rate = ?, quantity = ?, amount = ? where lineItemId = ?";
        try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query);
			for (InvoiceLineItem lineItem: lineItemUpdationList) {
				pst.setInt(1, lineItem.getItemId());
		        pst.setInt(2, lineItem.getRate());
		        pst.setInt(3, lineItem.getQuantity());
		        pst.setInt(4, lineItem.getAmount());
		        pst.setInt(5, lineItem.getLineItemId());
		        pst.addBatch();
			}
			return pst.executeBatch()[0] > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
	}


	public static boolean deleteLineItems(List<Integer> lineItemDeletionList) {
		if (lineItemDeletionList.size() <= 0) {
			return true;
		}
		StringBuilder query = new StringBuilder("delete from LineItemTable where lineItemId in (");
		for (int i = 0; i < lineItemDeletionList.size(); i++) {
			query.append(lineItemDeletionList.get(i));
			if (i < lineItemDeletionList.size() - 1) {
				query.append(",");
			}
		}
		query.append(")");
		try {
			PreparedStatement pst = SqlConnection.getConnection().prepareStatement(query.toString());
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
