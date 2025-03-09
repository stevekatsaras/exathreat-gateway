package com.exathreat.config.factory;

import org.springframework.beans.factory.annotation.Value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ApplicationSettings {
	
	@Value("${api.url}")
	private String apiUrl;

	@Value("${api.key}")
	private String apiKey;

	@Value("${gw.health}")
	private String gwHealth;

	@Value("${network.host}")
	private String networkHost;

	@Value("${batch.size}")
	private String batchSize;

	@Value("${batch.timeout}")
	private String batchTimeout;

	@Value("${discover.subnets}")
	private String discoverSubnets;

	@Value("${orgCode}")
	private String orgCode;

	@Value("${orgName}") 
	private String orgName;
}