package com.util;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class ResponseUtil
{
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static Response errorResponse()
	{
		return generateResponse(500, "Something went wrong in our end.");
	}

	public static Response generateResponse(int code, String message)
	{
		return generateResponse(code, message, "", null);
	}

	public static Response generateResponse(int code, String message, String dataKey, Object responseObj)
	{
		String responseStr = null;
		try
		{
			Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
			responseMap.put("code", code);
			responseMap.put("message", message);
			if(responseObj != null && !dataKey.isEmpty())
			{
				responseMap.put(dataKey, responseObj);
			}
			responseStr = objectMapper.writeValueAsString(responseMap);
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(code).entity(responseStr).build();
	}
}
