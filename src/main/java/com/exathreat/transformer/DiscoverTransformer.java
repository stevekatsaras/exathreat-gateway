package com.exathreat.transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DiscoverTransformer extends AbstractTransformer {

	@Transformer(inputChannel = "discoverInChannel", outputChannel = "batchInChannel")
	public Message<Map<String, Object>> transform(Message<Map<String, Object>> msg) throws Exception {
		log.debug("Incoming msg: {}", msg);

		Map<String, Object> httpHeaders = headers();
		Map<String, Object> httpBody = body(msg);

		return new GenericMessage<Map<String,Object>>(httpBody, httpHeaders);
	}

	private Map<String, Object> body(Message<Map<String, Object>> msg) throws Exception {
		Map<String, Object> payload = msg.getPayload();

		Map<String, Object> httpBody = new HashMap<String, Object>();
		httpBody.put("gatewayChannel", "discover");
		httpBody.put("event", buildEventMsg(payload));
		httpBody.putAll(payload);
		return httpBody;
	}

	@SuppressWarnings("unchecked")
	private String buildEventMsg(Map<String, Object> payload) throws Exception {
		List<Integer> assetOpenPorts = (List<Integer>) payload.get("assetOpenPorts");

		StringBuffer sb = new StringBuffer();
		sb.append("Asset " + payload.get("assetHostIp") + " discovered. ");
		sb.append("Scan revealed " + assetOpenPorts.size() + " open ports. ");
		sb.append("Ports are: " + assetOpenPorts + ".");
		return sb.toString();
	}
}