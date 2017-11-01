package com.onceas.timers;

import java.util.Date;

public interface TimerManager {

	public abstract Timer schedule(TimerListener timerlistener, long l);

	public abstract Timer schedule(TimerListener timerlistener, Date date);

	public abstract Timer schedule(TimerListener timerlistener, long l, long l1);

	public abstract Timer schedule(TimerListener timerlistener, Date date,
                                   long l);

	public abstract Timer scheduleAtFixedRate(TimerListener timerlistener,
                                              Date date, long l);

	public abstract Timer scheduleAtFixedRate(TimerListener timerlistener,
                                              long l, long l1);

	public abstract void resume();

	public abstract void suspend();

	public abstract void stop();

	public abstract boolean waitForStop(long l) throws InterruptedException;

	public abstract boolean isStopping();

	public abstract boolean isStopped();

	public abstract boolean waitForSuspend(long l) throws InterruptedException;

	public abstract boolean isSuspending();

	public abstract boolean isSuspended();
}
