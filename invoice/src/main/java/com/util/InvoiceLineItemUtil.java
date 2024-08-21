package com.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.database.ItemDb;
import com.entity.Invoice;
import com.entity.InvoiceLineItem;
import com.entity.Item;

public class InvoiceLineItemUtil {

	public static void calculateAmount(InvoiceLineItem lineItem) {
		if (lineItem.getRate() < 0) {
			Item item = ItemDb.getItem(lineItem.getItemId());
			if (item != null) {
				lineItem.setRate(item.getPrice());
			}
		}
		if (lineItem.getQuantity() <= 0) {
			lineItem.setQuantity(1);
		}
		lineItem.setAmount(lineItem.getQuantity() * lineItem.getRate());
	}

	public static void printLineItems(List<InvoiceLineItem> lineItemList) {
		System.out.println("Printing lineitems list");
		for (InvoiceLineItem lineItem: lineItemList) {
			System.out.println("Line item id: " + lineItem.getLineItemId() + " " + lineItem.getInvoiceid());
		}
	}
}
