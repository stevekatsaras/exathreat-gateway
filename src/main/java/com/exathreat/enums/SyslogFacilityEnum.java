package com.exathreat.enums;

public enum SyslogFacilityEnum {
	KERNEL(0, "Kernel"),
	USERLEVEL(1, "User-level"),
	MAIL(2, "Mail system"),
	SYSTEM(3, "System daemon"),
	SECURITY(4, "Security/Authorization"),
	SYSLOGD(5, "Syslogd"),
	LINEPRINTER(6, "Line printer subsystem"),
	NETWORKNEWS(7, "Network news subsystem"),
	UUCP(8, "UUCP subsystem"),
	CLOCK1(9, "Clock daemon"),
	AUTHORIZATION(10, "Security/Authorization"),
	FTP(11, "FTP daemon"),
	NTP(12, "NTP subsystem"),
	LOGAUDIT(13, "Log audit"),
	LOGALERT(14, "Log alert"),
	CLOCK2(15, "Clock daemon"),
	LOCAL0(16, "Local use 0"),
	LOCAL1(17, "Local use 1"),
	LOCAL2(18, "Local use 2"),
	LOCAL3(19, "Local use 3"),
	LOCAL4(20, "Local use 4"),
	LOCAL5(21, "Local use 5"),
	LOCAL6(22, "Local use 6"),
	LOCAL7(23, "Local use 7"),
	UNKNOWN(-1, "Unknown");
	
	private final int num;
	private final String desc;

	SyslogFacilityEnum(int num, String desc) {
		this.num = num;
		this.desc = desc;
	}

	public int getNum() {
		return num;
	}

	public String getDesc() {
		return desc;
	}

	public static SyslogFacilityEnum get(int num) {
		SyslogFacilityEnum syslogFacilityEnum = null;
		if (num == KERNEL.getNum()) {
			syslogFacilityEnum = KERNEL;
		}
		else if (num == USERLEVEL.getNum()) {
			syslogFacilityEnum = USERLEVEL;
		}
		else if (num == MAIL.getNum()) {
			syslogFacilityEnum = MAIL;
		}
		else if (num == SYSTEM.getNum()) {
			syslogFacilityEnum = SYSTEM;
		}
		else if (num == SECURITY.getNum()) {
			syslogFacilityEnum = SECURITY;
		}
		else if (num == SYSLOGD.getNum()) {
			syslogFacilityEnum = SYSLOGD;
		}
		else if (num == LINEPRINTER.getNum()) {
			syslogFacilityEnum = LINEPRINTER;
		}
		else if (num == NETWORKNEWS.getNum()) {
			syslogFacilityEnum = NETWORKNEWS;
		}
		else if (num == UUCP.getNum()) {
			syslogFacilityEnum = UUCP;
		}
		else if (num == CLOCK1.getNum()) {
			syslogFacilityEnum = CLOCK1;
		}
		else if (num == AUTHORIZATION.getNum()) {
			syslogFacilityEnum = AUTHORIZATION;
		}
		else if (num == FTP.getNum()) {
			syslogFacilityEnum = FTP;
		}
		else if (num == NTP.getNum()) {
			syslogFacilityEnum = NTP;
		}
		else if (num == LOGAUDIT.getNum()) {
			syslogFacilityEnum = LOGAUDIT;
		}
		else if (num == LOGALERT.getNum()) {
			syslogFacilityEnum = LOGALERT;
		}
		else if (num == CLOCK2.getNum()) {
			syslogFacilityEnum = CLOCK2;
		}
		else if (num == LOCAL0.getNum()) {
			syslogFacilityEnum = LOCAL0;
		}
		else if (num == LOCAL1.getNum()) {
			syslogFacilityEnum = LOCAL1;
		}
		else if (num == LOCAL2.getNum()) {
			syslogFacilityEnum = LOCAL2;
		}
		else if (num == LOCAL3.getNum()) {
			syslogFacilityEnum = LOCAL3;
		}
		else if (num == LOCAL4.getNum()) {
			syslogFacilityEnum = LOCAL4;
		}
		else if (num == LOCAL5.getNum()) {
			syslogFacilityEnum = LOCAL5;
		}
		else if (num == LOCAL6.getNum()) {
			syslogFacilityEnum = LOCAL6;
		}
		else if (num == LOCAL7.getNum()) {
			syslogFacilityEnum = LOCAL7;
		}
		else {
			syslogFacilityEnum = UNKNOWN;
		}
		return syslogFacilityEnum;
	}
}