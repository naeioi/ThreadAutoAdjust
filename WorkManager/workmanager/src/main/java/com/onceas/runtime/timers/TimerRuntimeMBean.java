package com.onceas.runtime.timers;

import com.onceas.runtime.RuntimeMBean;

public interface TimerRuntimeMBean extends RuntimeMBean {

	public abstract Timer[] getTimers();
}
