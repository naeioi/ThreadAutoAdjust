package com.onceas.timers.internal.commonj;

import commonj.timers.Timer;
import commonj.timers.TimerListener;

final class TimerWrap implements Timer {

	TimerWrap(TimerListener timerlistener, com.onceas.timers.Timer timer1) {
		listener = timerlistener;
		timer = timer1;
	}

	public boolean cancel() {
		return timer.cancel();
	}

	public TimerListener getTimerListener() {
		return listener;
	}

	public long getScheduledExecutionTime() {
		return timer.getTimeout();
	}

	public long getPeriod() {
		return Math.abs(timer.getPeriod());
	}

	public int hashCode() {
		return timer.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof TimerWrap) {
			TimerWrap timerwrap = (TimerWrap) obj;
			return timer.equals(timerwrap.timer);
		} else {
			return false;
		}
	}

	public String toString() {
		return timer.toString();
	}

	private final com.onceas.timers.Timer timer;

	private final TimerListener listener;
}
