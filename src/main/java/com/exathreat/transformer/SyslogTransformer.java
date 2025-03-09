package com.exathreat.transformer;

import java.util.HashMap;
import java.util.Map;

import com.exathreat.enums.SyslogFacilityEnum;
import com.exathreat.enums.SyslogSeverityEnum;

import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SyslogTransformer extends AbstractTransformer {
	
	@Transformer(inputChannel = "syslogInChannel", outputChannel = "batchInChannel")
	public Message<Map<String, Object>> transform(Message<Map<String, Object>> msg) throws Exception {
		log.debug("Incoming msg: {}", msg);

		Map<String, Object> syslogHeaders = headers();
		Map<String, Object> syslogBody = body(msg);
		
		return new GenericMessage<Map<String,Object>>(syslogBody, syslogHeaders);
	}

	private Map<String, Object> body(Message<Map<String, Object>> msg) throws Exception {
		Map<String, Object> payload = msg.getPayload();
		MessageHeaders headers = msg.getHeaders();
		
		Map<String, Object> syslogBody = new HashMap<String, Object>();
		syslogBody.put("gatewayChannel", "syslog");

		if (payload.containsKey("FACILITY")) {
			SyslogFacilityEnum syslogFacilityEnum = SyslogFacilityEnum.get((Integer) payload.get("FACILITY"));
			syslogBody.put("syslogFacilityNum", syslogFacilityEnum.getNum());
			syslogBody.put("syslogFacilityDesc", syslogFacilityEnum.getDesc());
		}
		if (payload.containsKey("SEVERITY")) {
			SyslogSeverityEnum syslogSeverityEnum = SyslogSeverityEnum.get((Integer) payload.get("SEVERITY"));
			syslogBody.put("syslogSeverityCode", syslogSeverityEnum.getCode());
			syslogBody.put("syslogSeverityDesc", syslogSeverityEnum.getDesc());
		}
		if (payload.containsKey("TIMESTAMP")) {
			syslogBody.put("syslogTimestamp", payload.get("TIMESTAMP"));
		}
		if (payload.containsKey("HOST")) {
			syslogBody.put("syslogHostname", payload.get("HOST"));
		}
		if (headers.containsKey("ip_address")) {
			syslogBody.put("syslogHostIp", headers.get("ip_address"));
		}
		if (payload.containsKey("TAG")) {
			syslogBody.put("syslogTag", payload.get("TAG"));
		}
		if (payload.containsKey("MESSAGE")) {
			syslogBody.put("event", payload.get("MESSAGE"));
		}
		return syslogBody;
	}
}