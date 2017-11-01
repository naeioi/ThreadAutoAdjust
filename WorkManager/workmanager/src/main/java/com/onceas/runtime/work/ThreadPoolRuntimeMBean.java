package com.onceas.runtime.work;

import com.onceas.health.HealthFeedback;
import com.onceas.runtime.RuntimeMBean;

public interface ThreadPoolRuntimeMBean extends RuntimeMBean, HealthFeedback {

	public abstract ExecuteThreadRuntimeMBean[] getExecuteThreads();

	public abstract ExecuteThreadRuntimeMBean[] getStuckExecuteThreads();

	public abstract int getExecuteThreadTotalCount();

	public abstract int getExecuteThreadIdleCount();

	public abstract int getQueueLength();

	public abstract int getPendingUserRequestCount();

	public abstract int getSharedCapacityForWorkManagers();

	public abstract long getCompletedRequestCount();

	public abstract int getHoggingThreadCount();

	public abstract int getStandbyThreadCount();

	public abstract double getThroughput();

	public abstract int getMinThreadsConstraintsPending();

	public abstract long getMinThreadsConstraintsCompleted();

	public abstract boolean isSuspended();

	public abstract long getRejectedRequestCount();

	public abstract long getCanceledRequestCount();

	public abstract void resetCompletedRequestCount();

	public abstract int getHealthThreadCount();

	public abstract int getExecuteThreadCount();
}
