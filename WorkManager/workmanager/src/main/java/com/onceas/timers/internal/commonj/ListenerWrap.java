package com.onceas.timers.internal.commonj;

import java.io.Serializable;

import com.onceas.timers.TimerListener;
import commonj.timers.CancelTimerListener;
import commonj.timers.StopTimerListener;
import commonj.timers.Timer;

final class ListenerWrap implements TimerListener, Serializable,
		com.onceas.timers.CancelTimerListener,
		com.onceas.timers.StopTimerListener {

	public ListenerWrap(commonj.timers.TimerListener timerlistener) {
		listener = timerlistener;
	}

	public void timerExpired(com.onceas.timers.Timer timer1) {
		initialize(timer1);
		listener.timerExpired(timer);
	}

	private void initialize(com.onceas.timers.Timer timer1) {
		if (timer == null)
			timer = new TimerWrap(listener, timer1);
	}

	public commonj.timers.TimerListener getTimerListener() {
		return listener;
	}

	public void timerCancelled(com.onceas.timers.Timer timer1) {
		if (listener instanceof CancelTimerListener)
			((CancelTimerListener) listener).timerCancel(timer);
	}

	public void timerStopped(com.onceas.timers.Timer timer1) {
		if (listener instanceof StopTimerListener)
			((StopTimerListener) listener).timerStop(timer);
	}

	public String toString() {
		return listener.toString();
	}

	private static final long serialVersionUID = 0xf5727fd4ef62eb62L;

	private final commonj.timers.TimerListener listener;

	private transient Timer timer;
}
