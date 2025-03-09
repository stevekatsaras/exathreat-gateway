package com.exathreat.transformer;

import java.util.HashMap;
import java.util.Map;

public class AbstractTransformer {
	
	public Map<String, Object> headers() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("type", "all");
		return headers;
	}
}