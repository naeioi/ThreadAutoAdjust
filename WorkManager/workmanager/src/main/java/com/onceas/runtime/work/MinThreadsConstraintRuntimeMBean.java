package com.onceas.runtime.work;

import com.onceas.runtime.RuntimeMBean;

public interface MinThreadsConstraintRuntimeMBean extends RuntimeMBean {

	public abstract long getCompletedRequests();

	public abstract int getPendingRequests();

	public abstract int getExecutingRequests();

	public abstract long getOutOfOrderExecutionCount();

	public abstract int getMustRunCount();

	public abstract long getMaxWaitTime();

	public abstract long getCurrentWaitTime();
}
