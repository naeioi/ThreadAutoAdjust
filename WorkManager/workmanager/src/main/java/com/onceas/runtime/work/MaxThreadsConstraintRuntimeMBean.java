package com.onceas.runtime.work;

import com.onceas.runtime.RuntimeMBean;

public interface MaxThreadsConstraintRuntimeMBean extends RuntimeMBean {

	public abstract int getExecutingRequests();

	public abstract int getDeferredRequests();
}
