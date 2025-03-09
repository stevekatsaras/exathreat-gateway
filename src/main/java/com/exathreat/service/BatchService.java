package com.exathreat.service;

import java.util.Map;

import com.exathreat.config.factory.ApplicationSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.aggregator.TimeoutCountSequenceSizeReleaseStrategy;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BatchService {

	@Autowired
	private ApplicationSettings applicationSettings;

	@Autowired
	private MessageChannel apiInChannel;

	@Autowired
	private MessageChannel batchDiscardChannel;

	@Bean
	@ServiceActivator(inputChannel = "batchInChannel")
	public MessageHandler aggregator() {
		Integer batchSize = Integer.parseInt(applicationSettings.getBatchSize());
		Long batchTimeout = Long.parseLong(applicationSettings.getBatchTimeout());
		
		batchSize = (batchSize < 100) ? 100 : (batchSize > 1000) ? 1000 : batchSize; // between 100 & 1000
		batchTimeout = (batchTimeout < 1000) ? 1000 : (batchTimeout > 60000) ? 60000 : batchTimeout; // between 1sec & 60sec

		AggregatingMessageHandler aggregator = new AggregatingMessageHandler(
			new DefaultAggregatingMessageGroupProcessor(), 
			new SimpleMessageStore(),
			new HeaderAttributeCorrelationStrategy("type"), 
			new TimeoutCountSequenceSizeReleaseStrategy(batchSize, batchTimeout));
			
		aggregator.setDiscardChannel(batchDiscardChannel);
		aggregator.setOutputChannel(apiInChannel);
		aggregator.setExpireGroupsUponCompletion(true);
		aggregator.setExpireGroupsUponTimeout(true);
		return aggregator;
	}

	/**
	 * Any message(s) discarded by the aggregator (for whatever reason) will be forwarded back in for retry.
	 * Discarded message(s) are polled every 5s; every poll fetches 10 message(s).
	 */
	@ServiceActivator(inputChannel = "batchDiscardChannel", outputChannel = "batchInChannel", poller = @Poller(fixedDelay = "5000", maxMessagesPerPoll = "10"))
	public void discard(Message<Map<String,Object>> msg) {
		log.error("Error occurred! Reason: message was discarded from batch aggregation ::: performing retry...");
		log.debug("Discarded msg: {}", msg);
	}
}