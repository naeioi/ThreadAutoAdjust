package com.onceas.runtime.timers;

import java.io.Serializable;

public interface Timer extends Serializable {

	public abstract String getTimerManagerName();

	public abstract long getTimeout();

	public abstract long getPeriod();

	public abstract boolean isStopped();

	public abstract boolean isCancelled();

	public abstract long[] getPastExpirationTimes();

	public abstract long getExpirationCount();
}
