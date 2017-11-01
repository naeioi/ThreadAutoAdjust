package com.onceas.runtime.work;

import java.io.Serializable;

public interface ExecuteThreadRuntimeMBean extends Serializable {
	public abstract String getCurrentRequest();

	public abstract long getCurrentRequestStartTime();

	/**
	 * @deprecated Method getLastRequest is deprecated
	 */
	public abstract String getLastRequest();

	public abstract int getServicedRequestTotalCount();

	public abstract boolean isIdle();

	public abstract boolean isStuck();

	public abstract boolean isHogger();

	public abstract boolean isStandby();

	/**
	 * public abstract JTATransaction getTransaction();
	 * 
	 * public abstract String getUser();
	 */
	public abstract String getName();

	public abstract String getWorkManagerName();

	public abstract String getApplicationName();

	public abstract String getModuleName();

	public abstract Thread getExecuteThread();
}
