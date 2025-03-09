package com.exathreat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ErrorService {

	@Autowired
	private MessageChannel apiOutChannel;
	
	/**
	 * Any message(s) failed to be sent to the API will be retried.
	 * Failed message(s) are polled every 3s; every poll fetches 1 message(s).
	 */
	@ServiceActivator(inputChannel = "errorChannel", poller = @Poller(fixedDelay = "3000", maxMessagesPerPoll = "1"))
	public void handle(Message<Throwable> msg) throws Exception {
		if (msg.getPayload() instanceof MessageHandlingException) {
			MessageHandlingException exception = (MessageHandlingException) msg.getPayload();
			log.error("Error occurred! Reason: " + exception.getMessage() + " ::: performing retry...");
			log.debug("Failed msg: {}", exception.getFailedMessage());
			apiOutChannel.send(exception.getFailedMessage());
		}
	}
}