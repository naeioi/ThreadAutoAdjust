package com.onceas.health;

import com.onceas.runtime.RuntimeMBean;

public final class MonitoredSystemTableEntry {

	MonitoredSystemTableEntry(String s, RuntimeMBean runtimembean, boolean flag) {
		key = s;
		MBeanRef = runtimembean;
		isCritical = flag;
	}

	String getKey() {
		return key;
	}

	RuntimeMBean getMBeanRef() {
		return MBeanRef;
	}

	boolean getIsCritical() {
		return isCritical;
	}

	private final String key;

	private final RuntimeMBean MBeanRef;

	private final boolean isCritical;
}
