package com.util;

public class QueryUtil
{
;	public static String getCriteria(String criteria, String column, String value, String conjuctor, String matcher)
	{
		StringBuilder criteriaBuilder = new StringBuilder(criteria);
		if (criteriaBuilder.length() != 0) {
			criteriaBuilder.append(" ").append(conjuctor);
		}
		if (matcher.equals("=")) {
			criteriaBuilder.append(" ").append(column).append(" = \"").append(value).append("\"");
		}
		else if (matcher.equals("like"))
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
}
