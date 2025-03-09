package com.exathreat.transformer;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HealthTransformer extends AbstractTransformer {

	@Transformer(inputChannel = "healthInChannel", outputChannel = "batchInChannel")
	public Message<Map<String, Object>> transform(Message<Map<String, Object>> msg) throws Exception {
		log.debug("Incoming msg: {}", msg);

		Map<String, Object> httpHeaders = headers();
		Map<String, Object> httpBody = body(msg);

		return new GenericMessage<Map<String,Object>>(httpBody, httpHeaders);
	}

	private Map<String, Object> body(Message<Map<String, Object>> msg) throws Exception {
		Map<String, Object> payload = msg.getPayload();

		Map<String, Object> httpBody = new HashMap<String, Object>();
		httpBody.put("gatewayChannel", "health");
		httpBody.put("event", buildEventMsg(payload));
		httpBody.putAll(payload);
		return httpBody;
	}

	private String buildEventMsg(Map<String, Object> payload) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("Gateway " + InetAddress.getLocalHost().getHostAddress() + " JVM metrics gathered. ");
		sb.append("memory heap (");
		sb.append("init: " + payload.get("gatewayJvmMemoryHeapInit") + ", ");
		sb.append("committed: " + payload.get("gatewayJvmMemoryHeapCommitted") + ", ");
		sb.append("max: " + payload.get("gatewayJvmMemoryHeapMax") + ", ");
		sb.append("used: " + payload.get("gatewayJvmMemoryHeapUsed") + "). ");
		sb.append("runtime (");
		sb.append("pID: " + payload.get("gatewayJvmRuntimePid") + ", ");
		sb.append("start time: " + payload.get("gatewayJvmRuntimeStartTime") + ", ");
		sb.append("up time: " + payload.get("gatewayJvmRuntimeUpTime") + "). ");
		sb.append("thread (");
		sb.append("cpu time: " + payload.get("gatewayJvmThreadCpuTime") + ", ");
		sb.append("count: " + payload.get("gatewayJvmThreadCount") + ").");
		return sb.toString();
	}
}