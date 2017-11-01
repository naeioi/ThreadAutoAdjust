package com.onceas.runtime;

import javax.management.ObjectName;

public interface RuntimeMBean {
	public abstract String getName();

	public abstract ObjectName getObjectName();

	public abstract RuntimeMBean getParent();

	public abstract String getType();

	public abstract RuntimeMBean getRuntimeMBean();

	public abstract void setParent(RuntimeMBean rtmbean);
}
