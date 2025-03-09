package com.exathreat.transformer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpTransformer extends AbstractTransformer {

	@Transformer(inputChannel = "httpInChannel", outputChannel = "batchInChannel")
	public Message<Map<String, Object>> transform(Message<Map<String, Object>> msg) throws Exception {
		log.debug("Incoming msg: {}", msg);

		Map<String, Object> httpHeaders = headers();
		Map<String, Object> httpBody = body(msg);

		return new GenericMessage<Map<String,Object>>(httpBody, httpHeaders);
	}

	private Map<String, Object> body(Message<Map<String, Object>> msg) throws Exception {
		Map<String, Object> payload = msg.getPayload();

		Map<String, Object> httpBody = new HashMap<String, Object>();
		httpBody.put("gatewayChannel", "http");
		if (!payload.containsKey("event")) {
			httpBody.put("event", buildEventMsg(payload));
		}
		httpBody.putAll(payload);
		return httpBody;
	}

	private String buildEventMsg(Map<String, Object> payload) throws Exception {
		return payload.keySet()
			.stream()
      .map(key -> key + "=" + payload.get(key))
      .collect(Collectors.joining(", ", "{", "}"));
	}
}