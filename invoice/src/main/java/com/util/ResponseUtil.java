package com.util;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.Response;


import com.google.gson.Gson;

public class ResponseUtil {
	private static Gson gson = new Gson();
	
	public static Response generateResponse(int code, String message) {
		return generateResponse(code, message, "",  null);
	}

	public static Response generateResponse(int code, String message, String dataKey, Object responseObj) {
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		responseMap.put("code", code);
		responseMap.put("message", message);
		if (responseObj != null && !dataKey.isEmpty()) {			
			responseMap.put(dataKey, responseObj);
		}
		String responseStr = gson.toJson(responseMap);
		return Response.status(code).entity(responseStr).build();
	}
}
