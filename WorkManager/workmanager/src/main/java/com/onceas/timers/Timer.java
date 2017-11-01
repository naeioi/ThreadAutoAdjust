package com.onceas.timers;

public interface Timer {

	public abstract long getTimeout();

	public abstract long getPeriod();

	public abstract TimerListener getListener();

	public abstract boolean cancel();

	public abstract boolean isStopped();

	public abstract boolean isCancelled();
}
