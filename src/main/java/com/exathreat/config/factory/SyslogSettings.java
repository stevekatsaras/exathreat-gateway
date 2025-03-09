package com.exathreat.config.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class SyslogSettings {
	private int port;
	private int buffer;
}