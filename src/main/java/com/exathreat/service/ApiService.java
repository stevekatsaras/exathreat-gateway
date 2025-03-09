package com.exathreat.service;

import java.net.InetAddress;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exathreat.config.factory.ApplicationSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiService {
	
	@Autowired
	private ApplicationSettings applicationSettings;

  @ServiceActivator(inputChannel = "apiInChannel", outputChannel = "apiOutChannel")	
	public Message<Map<String, Object>> prepare(Message<List<Map<String, Object>>> msg) throws Exception {
		log.info("Outgoing msg with {} events!", msg.getPayload().size());
		
		List<Map<String, Object>> msgs = msg.getPayload();
		for (Map<String, Object> m : msgs) {
			m.put("gatewayTime", DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now(ZoneOffset.UTC)));
			m.put("gatewayIp", InetAddress.getLocalHost().getHostAddress());
			m.put("gatewayHost", InetAddress.getLocalHost().getHostName());
			m.put("gatewayName", "exathreat-gateway-springboot");
		}
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("apiKey", applicationSettings.getApiKey());
		requestBody.put("orgCode", applicationSettings.getOrgCode());
		requestBody.put("orgName", applicationSettings.getOrgName());
		requestBody.put("events", msgs);
		
		log.debug("Outgoing msg: {}", requestBody);

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		return new GenericMessage<Map<String, Object>>(requestBody, headers);
	}

	@ServiceActivator(inputChannel = "apiOutChannel")
	@Bean
	public HttpRequestExecutingMessageHandler httpRequestExecutingMessageHandler() throws Exception {
		String apiUrl = applicationSettings.getApiUrl();

		HttpRequestExecutingMessageHandler handler = 
		Http.outboundGateway(apiUrl + (apiUrl.endsWith("/") ? "ingest" : "/ingest"))
			.httpMethod(HttpMethod.POST)
			.mappedRequestHeaders("Content-Type")
			.messageConverters(new MappingJackson2HttpMessageConverter())
			.get();

		handler.setAsync(true);
		handler.setExpectReply(false);
		handler.setExtractPayload(true);
		return handler;
	}
}