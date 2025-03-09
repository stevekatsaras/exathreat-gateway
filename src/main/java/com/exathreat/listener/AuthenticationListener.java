package com.exathreat.listener;

import java.time.Duration;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
public class AuthenticationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
	
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		ConfigurableEnvironment env = event.getEnvironment();
		verifyStartupArguments(env);
		authenticateCredentials(env);
	}

	private void verifyStartupArguments(ConfigurableEnvironment env) {
		String apiUrl = env.getProperty("api.url");
		String apiKey = env.getProperty("api.key");
		
		if (StringUtils.isBlank(apiUrl) || StringUtils.isBlank(apiKey)) {
			log.error("The 'api.url' or 'api.key' startup arguments are missing.");
			log.error("The application cannot startup without these arguments.");
			log.error("Aborting...");
			System.exit(-1);
		}

		System.setProperty("gw.health", env.getProperty("gw.health", "false"));
		System.setProperty("network.host", env.getProperty("network.host", ""));
		System.setProperty("batch.size", env.getProperty("batch.size", "100"));
		System.setProperty("batch.timeout", env.getProperty("batch.timeout", "1000"));
		System.setProperty("discover.subnets", env.getProperty("discover.subnets", ""));		

		log.info("The startup arguments are present. Initiating credential authentication...");
		log.info("'api.url': {}", apiUrl);
		log.info("'api.key': {}", apiKey);
	}

	private void authenticateCredentials(ConfigurableEnvironment env) {
		log.info("Communicating to the API gateway...");
		
		Map<String, Object> requestBody = Map.of("apiKey", env.getProperty("api.key"));		
		Map<String, Object> responseBody = call(env.getProperty("api.url"), requestBody);

		if (!(Boolean) responseBody.get("authenticated")) {
			log.error("Credential authentication failed. " + (String) responseBody.get("errorMsg"));
			log.error("Aborting...");
			System.exit(-1);
		}

		log.info("Credential authentication is successful. Initialising application...");

		System.setProperty("orgCode", (String) responseBody.get("orgCode"));
		System.setProperty("orgName", (String) responseBody.get("orgName"));
	}

	private Map<String, Object> call(String apiUrl, Map<String, Object> requestBody) {
		Map<String, Object> responseBody = null;
		try {
			responseBody = WebClient.create().post()
				.uri(apiUrl + (apiUrl.endsWith("/") ? "auth" : "/auth"))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(Mono.just(requestBody), Map.class)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
				.doOnSuccess(response -> {
					log.info("Communication established!");
				})
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(10))
					.doBeforeRetry(retrySignal -> {
						log.error("Communication failed. Reason: " + retrySignal.failure().getMessage() + ". Attempting retry " + (retrySignal.totalRetries() + 1) + "...");
					})
				)
				.doOnError(exception -> {
					log.error("Communication error. Reason: " + exception.getMessage());
				})
				.block();
		}
		catch (Exception exception) {
			log.error("Communication error. Reason: " + exception.getMessage());
			log.error("Aborting...");
			System.exit(0);
		}
		return responseBody;
	}
}