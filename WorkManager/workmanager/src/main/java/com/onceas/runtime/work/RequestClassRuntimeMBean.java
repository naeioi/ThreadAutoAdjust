package com.onceas.runtime.work;

import com.onceas.runtime.RuntimeMBean;

public interface RequestClassRuntimeMBean extends RuntimeMBean {

	public abstract String getRequestClassType();

	public abstract long getCompletedCount();

	public abstract long getTotalThreadUse();

	public abstract int getPendingRequestCount();

	public abstract long getVirtualTimeIncrement();

	public abstract long getThreadUseSquares();

	public abstract long getDeltaFirst();

	public abstract long getDeltaRepeat();

	public abstract long getMyLast();

	public abstract double getInterval();

	public static final String FAIR_SHARE = "fairshare";

	public static final String RESPONSE_TIME = "responsetime";
}
