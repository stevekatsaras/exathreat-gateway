package com.exathreat.config;

import java.util.Map;
import java.util.concurrent.Executors;

import com.exathreat.config.factory.ApplicationSettings;
import com.exathreat.config.factory.SyslogSettings;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;
import org.springframework.integration.syslog.inbound.TcpSyslogReceivingChannelAdapter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@Import({ ApplicationConfig.class })
public class IntegrationConfig {

	@Autowired
	private ApplicationSettings applicationSettings;

	@Autowired
	private SyslogSettings syslogSettings;

	@Bean
	public MessageChannel discoverInChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel healthInChannel() {
		return new DirectChannel();
	}
	
	@Bean
	public MessageChannel httpInChannel() {
		return new DirectChannel();
	}

	@Bean
  public MessageChannel syslogInChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel batchInChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel batchDiscardChannel() {
		return new QueueChannel();
	}

	@Bean
  public MessageChannel apiInChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel apiOutChannel() {
		return new ExecutorChannel(Executors.newCachedThreadPool());
	}

	@Bean
	public MessageChannel errorChannel() {
		return new QueueChannel();
	}

	@Bean
	public HttpRequestHandlingMessagingGateway httpRequestHandlingMessagingGateway() {
		RequestMapping mapping = new RequestMapping();
		mapping.setMethods(HttpMethod.POST);
		mapping.setPathPatterns("/gateway/http");

		HttpRequestHandlingMessagingGateway gateway = new HttpRequestHandlingMessagingGateway(false);
		gateway.setRequestChannel(httpInChannel());
		gateway.setRequestMapping(mapping);
		gateway.setRequestPayloadType(ResolvableType.forClass(Map.class));
		return gateway;
	}

	@Bean
	public TcpSyslogReceivingChannelAdapter tcpSyslogReceivingChannelAdapter() {
		ByteArrayLfSerializer byteArrayLfSerializer = new ByteArrayLfSerializer();
		byteArrayLfSerializer.setMaxMessageSize(syslogSettings.getBuffer());

		TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(syslogSettings.getPort());
		connectionFactory.setDeserializer(byteArrayLfSerializer);

		if (StringUtils.isNotBlank(applicationSettings.getNetworkHost())) {
			connectionFactory.setLocalAddress(applicationSettings.getNetworkHost());
		}

		TcpSyslogReceivingChannelAdapter adapter = new TcpSyslogReceivingChannelAdapter();
		adapter.setConnectionFactory(connectionFactory);
		adapter.setOutputChannel(syslogInChannel());
		return adapter;
	}
	
}