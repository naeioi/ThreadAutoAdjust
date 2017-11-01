package com.onceas.timers;

public interface StopTimerListener extends TimerListener {

	public abstract void timerStopped(Timer timer);
}
