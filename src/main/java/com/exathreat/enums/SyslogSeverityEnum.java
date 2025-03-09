package com.exathreat.enums;

public enum SyslogSeverityEnum {
	EMERGENCY(0, "Emergency"),
	ALERT(1, "Alert"),
	CRITICAL(2, "Critical"),
	ERROR(3, "Error"),
	WARNING(4, "Warning"),
	NOTICE(5, "Notice"),
	INFORMATIONAL(6, "Informational"),
	DEBUG(7, "Debug"),
	UNKNOWN(-1, "Unknown");
	
	private final int code;
	private final String desc;

	SyslogSeverityEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static SyslogSeverityEnum get(int code) {
		SyslogSeverityEnum syslogSeverityEnum = null;
		if (code == EMERGENCY.getCode()) {
			syslogSeverityEnum = EMERGENCY;
		}
		else if (code == ALERT.getCode()) {
			syslogSeverityEnum = ALERT;
		}
		else if (code == CRITICAL.getCode()) {
			syslogSeverityEnum = CRITICAL;
		}
		else if (code == ERROR.getCode()) {
			syslogSeverityEnum = ERROR;
		}
		else if (code == WARNING.getCode()) {
			syslogSeverityEnum = WARNING;
		}
		else if (code == NOTICE.getCode()) {
			syslogSeverityEnum = NOTICE;
		}
		else if (code == INFORMATIONAL.getCode()) {
			syslogSeverityEnum = INFORMATIONAL;
		}
		else if (code == DEBUG.getCode()) {
			syslogSeverityEnum = DEBUG;
		}
		else {
			syslogSeverityEnum = UNKNOWN;
		}
		return syslogSeverityEnum;
	}
}