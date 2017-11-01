package com.onceas.runtime.work;

import com.onceas.runtime.RuntimeMBean;

public interface WorkManagerRuntimeMBean extends RuntimeMBean {
	public abstract String getApplicationName();

	public abstract String getModuleName();

	public abstract int getPendingRequests();

	public abstract long getCompletedRequests();

	public abstract int getStuckThreadCount();

	public abstract MinThreadsConstraintRuntimeMBean getMinThreadsConstraintRuntime();

	public abstract MaxThreadsConstraintRuntimeMBean getMaxThreadsConstraintRuntime();

	public abstract void setMinThreadsConstraintRuntime(
            MinThreadsConstraintRuntimeMBean minthreadsconstraintruntimembean);

	public abstract void setMaxThreadsConstraintRuntime(
            MaxThreadsConstraintRuntimeMBean maxthreadsconstraintruntimembean);

	public abstract void setRequestClassRuntime(
            RequestClassRuntimeMBean requestclassruntimembean);

	public abstract RequestClassRuntimeMBean getRequestClassRuntime();
}
