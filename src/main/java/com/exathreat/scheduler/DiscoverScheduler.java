package com.exathreat.scheduler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.exathreat.config.factory.ApplicationSettings;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DiscoverScheduler {

	@Autowired
	private ApplicationSettings applicationSettings;

	@Autowired
	private MessageChannel discoverInChannel;

	@Scheduled(initialDelay = 60000, fixedDelay = 7200000) // initial delay: 1m, fixed rate every: 2h
	public void run() {
		if (StringUtils.isNotBlank(applicationSettings.getDiscoverSubnets())) {
			log.debug("[DiscoverScheduler.run] - started at: " + ZonedDateTime.now(ZoneOffset.UTC));

			List<Map<String, Object>> assets = discoverByIp();
			for (Map<String, Object> asset : assets) {
				scanPorts(asset);
				discoverInChannel.send(MessageBuilder.withPayload(asset).build());
			}
			log.debug("[DiscoverScheduler.run] - discovered " + assets.size() + " assets.");
			log.debug("[DiscoverScheduler.run] - ended at: " + ZonedDateTime.now(ZoneOffset.UTC));
		}
	}

	private List<Map<String, Object>> discoverByIp() {
		String[] subnetsList = applicationSettings.getDiscoverSubnets().split(",");

		List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<CompletableFuture<Map<String, Object>>>();
		for (String subnet : subnetsList) {
			for (int i = 1; i <= 255; i++) {
				final String ipAddress = subnet + "." + i;
				futures.add(CompletableFuture.supplyAsync(() -> {
					InetAddress inetAddress = null;
					boolean reachable = false;
					try {
						inetAddress = InetAddress.getByName(ipAddress);
						reachable = inetAddress.isReachable(200);
					}
					catch (Exception exception) {
						throw new CompletionException(exception);
					}
					return Map.of("inetAddress", inetAddress, "reachable", reachable);
				}));
			}
		}

		List<Map<String, Object>> assets = new ArrayList<Map<String, Object>>();
		for (final CompletableFuture<Map<String, Object>> future : futures) {
			try {
				Map<String, Object> result = future.join();
				if ((boolean) result.get("reachable")) {
					InetAddress inetAddress = (InetAddress) result.get("inetAddress");

					Map<String, Object> payload = new HashMap<String, Object>();
					payload.put("assetHostIp", inetAddress.getHostAddress());
					payload.put("assetHostname", inetAddress.getCanonicalHostName());
					assets.add(payload);
				}
			}
			catch (Exception exception) {}
		}
		return assets;
	}

	private void scanPorts(Map<String, Object> asset) {
		List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<CompletableFuture<Map<String, Object>>>();
		
		String ipAddress = (String) asset.get("assetHostIp");
		for (int p = 1; p <= 65535; p++) {
			final int port = p;
			futures.add(CompletableFuture.supplyAsync(() -> {
				boolean open = false;
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(ipAddress, port), 200);
					socket.close();
					open = true;
				}
				catch (Exception exception) {
					throw new CompletionException(exception);
				}
				return Map.of("port", port, "open", open);
			}));
		}

		List<Object> openPorts = new ArrayList<Object>();
		for (final CompletableFuture<Map<String, Object>> future : futures) {
			try {
				Map<String, Object> result = future.join();
				if ((boolean) result.get("open")) {
					openPorts.add(result.get("port"));
				}
			}
			catch (Exception exception) {}
		}
		asset.put("assetOpenPorts", openPorts);
	}
}