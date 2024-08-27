package com.util;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.entity.Error;

public class QueryUtil
{
;	public static String getCriteria(String criteria, String column, String value, String conjuctor, String operator)
	{
		StringBuilder criteriaBuilder = new StringBuilder(criteria);
		if (criteriaBuilder.length() != 0) {
			criteriaBuilder.append(" ").append(conjuctor);
		}
		if (operator.equals("=")) {
			criteriaBuilder.append(" ").append(column).append(" = \"").append(value).append("\"");
		}
		else if (operator.equals("like"))
		{
			criteriaBuilder.append(" ").append(column).append(" like").append(" \"%").append(value).append("%\"");
		}

		return criteriaBuilder.toString();
	}

	public static String groupCriteria(String criteria1, String criteria2, String conjuctor) {
		if (criteria1.isEmpty()) {
			return criteria2;
		}
		return " ( "
			+ criteria1
			+ " "
			+ conjuctor
			+ " "
			+ criteria2
			+ " ) ";
	}

	public static String handlePagination(MultivaluedMap<String, String> queryParamsMap, List<Error> errorList)
	{
		int page = 0, size = 0;
		String pageLimit = "";
		if (queryParamsMap.containsKey("page") && queryParamsMap.containsKey("size")) {
			try {
				page = Integer.parseInt(queryParamsMap.getFirst("page"));
				size = Integer.parseInt(queryParamsMap.getFirst("size"));
				int offset = (page - 1) * size;
				pageLimit = " limit " + offset +", " + size;
			}
			catch (Exception e) {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Invalid page/size format");
				errorList.add(error);
			}
		} else {
			pageLimit = " limit 0, 100";
		}
		return pageLimit;
	}

	public static String appendCriOrderLimit(String query, String criteria, String orderBy, String pageLimit) {
		if (!criteria.isEmpty()) {
			query += " where " + criteria;
		}
		if (!orderBy.isEmpty()) {
			query += orderBy;
		}
		query += pageLimit;
		return query;
	}

	public static String handleParamCriteria(MultivaluedMap<String, String> queryParamsMap, Map<String, String> allowedFilterMap, String tableName)
	{
		String criteria = "";
		for (Map.Entry<String, List<String>> entry : queryParamsMap.entrySet()) {
			String key = entry.getKey();
			System.out.println("Entry key: " + key + " Criteria: " + criteria);
			if (allowedFilterMap.containsKey(key)) {
				for (String value : entry.getValue()) {
					criteria = getCriteria(criteria,  tableName+"." + allowedFilterMap.get(key), value, "and", "=");
				}
			}
		}
		return criteria;
	}

	public static String handleParamSortOrder(MultivaluedMap<String, String> queryParamsMap, Map<String, String> allowedSortMap, String tableName, List<Error> errorList)
	{
		String orderBy = "";
		if (queryParamsMap.containsKey("sort") && queryParamsMap.containsKey("sort_order")) {
			if (!allowedSortMap.containsKey(queryParamsMap.get("sort").get(0))) {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Column " + queryParamsMap.get("sort").get(0) + " was not sortable or unknown");
				errorList.add(error);
			} else {
				String sortOrder = queryParamsMap.get("sort_order").get(0);
				String sortCol = allowedSortMap.get(queryParamsMap.get("sort").get(0));
				sortOrder = sortOrder.toUpperCase();
				if (!sortCol.isEmpty()) {
					orderBy = " order by "+ tableName +"." + sortCol + " " + (sortOrder.equals("D") ? "desc" : "");
				}
			}
		}
		return orderBy;
	}
}
