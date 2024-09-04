package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.entity.Error;

public class SecurityUtil
{
	private static final String ALPHA_NUMERIC_PATTERN = "^[a-zA-Z0-9_ ]*$";
	private static final String NUMERIC_PATTERN = "\\d+";
	
	public static boolean isValidParamValue(String value) {
		return (value != null) && (!value.isEmpty()) && (value.matches(ALPHA_NUMERIC_PATTERN));
	}

	public static List<Error> validateRequestParams(UriInfo uriInfo, List<String> allowedParameters, Map<String, String> allowedFilterMap, Map<String, String> allowedSortMap)
	{
		MultivaluedMap<String, String> queryParamsMap = uriInfo.getQueryParameters();
		List<Error> errorList = new ArrayList<Error>();
		for (Map.Entry<String, List<String>> entry : queryParamsMap.entrySet()) {
			String key = entry.getKey();
			if (allowedParameters.contains(key) || allowedFilterMap.containsKey(key)) {
				for (String value: queryParamsMap.get(key)) {
					if (!SecurityUtil.isValidParamValue(value)) {
						Error error = new Error();
						error.setCode(400);
						error.setMessage("Parameter "  + key + "'s value [" + value + "] is not valid");
						errorList.add(error);
					} else {
						if (key.equals("sort") && !allowedSortMap.containsKey(value)) {
							Error error = new Error();
							error.setCode(400);
							error.setMessage("Sort column ["  + value + "] is unknown");
							errorList.add(error);
						} else if (key.equals("page") && !value.matches(NUMERIC_PATTERN)) {
							Error error = new Error();
							error.setCode(400);
							error.setMessage("Page value ["  + value + "] should be number");
							errorList.add(error);
						} else if (key.equals("size") && !value.matches(NUMERIC_PATTERN)) {
							Error error = new Error();
							error.setCode(400);
							error.setMessage("Size value ["  + value + "] should be a number");
							errorList.add(error);
						}
					}
				}
			}
			else {
				Error error = new Error();
				error.setCode(400);
				error.setMessage("Parameter "  + key + " was unknown");
				errorList.add(error);
			}
		}
		return errorList;
	}
}
