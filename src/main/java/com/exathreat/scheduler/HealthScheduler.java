package com.exathreat.scheduler;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.exathreat.config.factory.ApplicationSettings;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HealthScheduler {

	@Autowired
	private ApplicationSettings applicationSettings;

	@Autowired
	private MessageChannel healthInChannel;

	@Scheduled(initialDelay = 15000, fixedDelay = 60000) // initial delay: 15s, fixed rate every: 1m
	public void run() {
		if (BooleanUtils.isTrue(Boolean.parseBoolean(applicationSettings.getGwHealth()))) {
			log.debug("[HealthScheduler.run] - started at: " + ZonedDateTime.now(ZoneOffset.UTC));

			MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

			Map<String, Object> payload = new HashMap<String, Object>();
			payload.put("gatewayJvmMemoryHeapInit", memoryMXBean.getHeapMemoryUsage().getInit());
			payload.put("gatewayJvmMemoryHeapCommitted", memoryMXBean.getHeapMemoryUsage().getCommitted());
			payload.put("gatewayJvmMemoryHeapMax", memoryMXBean.getHeapMemoryUsage().getMax());
			payload.put("gatewayJvmMemoryHeapUsed", memoryMXBean.getHeapMemoryUsage().getUsed());		
			payload.put("gatewayJvmRuntimePid", runtimeMXBean.getPid());
			payload.put("gatewayJvmRuntimeStartTime", runtimeMXBean.getStartTime());
			payload.put("gatewayJvmRuntimeUpTime", runtimeMXBean.getUptime());
			payload.put("gatewayJvmThreadCpuTime", threadMXBean.getCurrentThreadCpuTime());
			payload.put("gatewayJvmThreadCount", threadMXBean.getThreadCount());

			healthInChannel.send(MessageBuilder.withPayload(payload).build());
			log.debug("[HealthScheduler.run] - ended at: " + ZonedDateTime.now(ZoneOffset.UTC));
		}
	}

}