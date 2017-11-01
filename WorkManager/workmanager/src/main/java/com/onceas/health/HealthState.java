// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   HealthState.java

package com.onceas.health;

import java.io.Serializable;

public final class HealthState implements Serializable {

	public HealthState(int i) {
		this(i, NULL_REASONS);
	}

	public HealthState(int i, String s) {
		state = i;
		if (s == null)
			reasonCode = NULL_REASONS;
		else
			reasonCode = (new String[] { s });
	}

	public HealthState(int i, String as[]) {
		state = i;
		if (as == null)
			reasonCode = NULL_REASONS;
		else
			reasonCode = as;
	}

	public String getSubsystemName() {
		return subsystemName;
	}

	public int getState() {
		return state;
	}

	public String[] getReasonCode() {
		return reasonCode;
	}

	public String toString() {
		StringBuffer stringbuffer = new StringBuffer("State:"
				+ mapToString(state) + ",");
		stringbuffer.append("ReasonCode:[");
		if (reasonCode != null && reasonCode.length > 0) {
			for (int i = 0; i < reasonCode.length - 1; i++)
				stringbuffer.append(reasonCode[i] + ",");

			stringbuffer.append(reasonCode[reasonCode.length - 1]);
		}
		stringbuffer.append("]");
		return stringbuffer.toString();
	}

	public static String mapToString(int i) {
		switch (i) {
		case 0: // '\0'
			return "HEALTH_OK";

		case 1: // '\001'
			return "HEALTH_WARN";

		case 2: // '\002'
			return "HEALTH_CRITICAL";

		case 3: // '\003'
			return "HEALTH_FAILED";

		case 4: // '\004'
			return "HEALTH_OVERLOADED";
		}
		return "UNKNOWN";
	}

	public void setSubsystemName(String s) {
		subsystemName = s;
	}

	public void setCritical(boolean flag) {
		isCritical = flag;
	}

	public boolean isCritical() {
		return isCritical;
	}

	public void setMBeanName(String s) {
		mbeanName = s;
	}

	public String getMBeanName() {
		return mbeanName;
	}

	public void setMBeanType(String s) {
		mbeanType = s;
	}

	public String getMBeanType() {
		return mbeanType;
	}

	private static final long serialVersionUID = 0x83bcc9644ca81d99L;

	public static final int HEALTH_OK = 0;

	public static final int HEALTH_WARN = 1;

	public static final int HEALTH_CRITICAL = 2;

	public static final int HEALTH_FAILED = 3;

	public static final int HEALTH_OVERLOADED = 4;

	public static final String LOW_MEMORY_REASON = "server is low on memory";

	private static final String NULL_REASONS[] = new String[0];

	private final int state;

	private final String reasonCode[];

	private String subsystemName;

	private boolean isCritical;

	private String mbeanName;

	private String mbeanType;

}
