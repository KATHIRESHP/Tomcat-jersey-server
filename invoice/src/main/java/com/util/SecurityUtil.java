package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.entity.Error;

public class SecurityUtil
{
	private static String alphaNumericPattern = "^[a-zA-Z0-9_ ]*$";
	
	public static boolean isValidParamValue(String value) {
		return (value != null) && (!value.isEmpty()) && (value.matches(alphaNumericPattern));
	}
	public static List<Error> validateRequestParams(UriInfo uriInfo, List<String> allowedParameters, Map<String, String> allowedFilterMap)
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
