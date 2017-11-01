package com.onceas.runtime.server;

import com.onceas.health.HealthFeedback;
import com.onceas.health.HealthState;
import com.onceas.runtime.RuntimeMBean;

public interface ServerHealthRuntimeMBean extends RuntimeMBean, HealthFeedback {
	public void setHealthState(int i, String s);

	public HealthState getHealthState();
}
