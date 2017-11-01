package com.onceas.timers;

public interface CancelTimerListener extends TimerListener {

	public abstract void timerCancelled(Timer timer);
}
