package com.onceas.timers.internal.commonj;

import java.util.Date;

import commonj.timers.Timer;
import commonj.timers.TimerListener;
import commonj.timers.TimerManager;

public final class TimerManagerImpl implements TimerManager {

	public TimerManagerImpl(com.onceas.timers.TimerManager timermanager) {
		tm = timermanager;
	}

	public void suspend() {
		tm.suspend();
	}

	public boolean isSuspending() {
		return tm.isSuspending();
	}

	public boolean isSuspended() {
		return tm.isSuspended();
	}

	public boolean waitForSuspend(long l) throws InterruptedException {
		return tm.waitForSuspend(l);
	}

	public void resume() {
		tm.resume();
	}

	public void stop() {
		tm.stop();
	}

	public boolean isStopped() {
		return tm.isStopped();
	}

	public boolean isStopping() {
		return tm.isStopping();
	}

	public boolean waitForStop(long l) throws InterruptedException {
		return tm.waitForStop(l);
	}

	public Timer schedule(TimerListener timerlistener, Date date) {
		return new TimerWrap(timerlistener, tm.schedule(new ListenerWrap(
				timerlistener), date));
	}

	public Timer schedule(TimerListener timerlistener, long l) {
		return new TimerWrap(timerlistener, tm.schedule(new ListenerWrap(
				timerlistener), l));
	}

	public Timer schedule(TimerListener timerlistener, Date date, long l) {
		return new TimerWrap(timerlistener, tm.schedule(new ListenerWrap(
				timerlistener), date, l));
	}

	public Timer schedule(TimerListener timerlistener, long l, long l1) {
		return new TimerWrap(timerlistener, tm.schedule(new ListenerWrap(
				timerlistener), l, l1));
	}

	public Timer scheduleAtFixedRate(TimerListener timerlistener, Date date,
			long l) {
		return new TimerWrap(timerlistener, tm.schedule(new ListenerWrap(
				timerlistener), date, l));
	}

	public Timer scheduleAtFixedRate(TimerListener timerlistener, long l,
			long l1) {
		return new TimerWrap(timerlistener, tm.schedule(new ListenerWrap(
				timerlistener), l, l1));
	}

	public String toString() {
		return tm.toString();
	}

	private com.onceas.timers.TimerManager tm;
}
