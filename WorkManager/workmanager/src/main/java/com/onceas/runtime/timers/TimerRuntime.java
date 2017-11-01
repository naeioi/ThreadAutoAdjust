package com.onceas.runtime.timers;

import com.onceas.runtime.Runtime;
import com.onceas.timers.internal.TimerImpl;

public final class TimerRuntime extends Runtime implements TimerRuntimeMBean {

	public TimerRuntime() {
		super("TimerRuntime");
	}

	public Timer[] getTimers() {
		com.onceas.timers.Timer atimer[] = TimerImpl.getTimers();
		if (atimer == null || atimer.length == 0)
			return null;
		Timer atimer1[] = new Timer[atimer.length];
		for (int i = 0; i < atimer.length; i++)
			atimer1[i] = ((TimerImpl) atimer[i]).getRuntime();

		return atimer1;
	}
}
