package com.exathreat.config;

import javax.annotation.PostConstruct;

import com.exathreat.config.factory.ApplicationSettings;
import com.exathreat.config.factory.SyslogSettings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationConfig {

	@PostConstruct
  public void init() {
		log.info("applicationSettings: " + applicationSettings());
		log.info("syslogSettings: " + syslogSettings());
	}

	@Bean
	public ApplicationSettings applicationSettings() {
		return new ApplicationSettings();
	}

	@Bean
	@ConfigurationProperties(prefix = "syslog")
	public SyslogSettings syslogSettings() {
		return new SyslogSettings();
	}

}