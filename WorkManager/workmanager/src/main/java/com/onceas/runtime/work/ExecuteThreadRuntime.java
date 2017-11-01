package com.onceas.runtime.work;

import com.onceas.work.WorkAdapter;
import com.onceas.work.constraint.ServerWorkManagerImpl;
import com.onceas.work.threadpool.ExecuteThread;

public class ExecuteThreadRuntime implements
		com.onceas.runtime.work.ExecuteThreadRuntimeMBean {
	public String getName() {
		return name;
	}

	public String getCurrentRequest() {
		return currentWork;
	}

	public String getLastRequest() {
		return lastRequest;
	}

	public int getServicedRequestTotalCount() {
		return servicedRequests;
	}

	/**
	 * public JTATransaction getTransaction() { return transaction; }
	 * 
	 * public String getUser() { return user; }
	 */
	public boolean isIdle() {
		return currentWork == null;
	}

	public boolean isStuck() {
		return stuck;
	}

	public boolean isHogger() {
		return hogger;
	}

	public boolean isStandby() {
		return standby;
	}

	public long getCurrentRequestStartTime() {
		return startTime;
	}

	public Thread getExecuteThread() {
		return executeThread;
	}

	public String getWorkManagerName() {
		return wmName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getModuleName() {
		return moduleName;
	}

	/**
	 * public String toString() { return name + (stuck || !hogger ? "" :
	 * "[HOG]") + "\n\tcurrent request : " + (isIdle() ? "Idle Thread" :
	 * currentWork) + "\n\tuser : " + user + "\n\tstart time : " + (isIdle() ?
	 * "N/A" : (new Date(startTime)).toString()) + "\n\tWorkManager : " +
	 * (isIdle() ? "N/A" : wmName) + (applicationName == null ? "" : "@" +
	 * applicationName) + "\n"; }
	 */
	// private static final AuthenticatedSubject kernelID =
	// (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
	private final String name;

	private final String currentWork;

	private final String lastRequest;

	private final int servicedRequests;

	// private final JTATransaction transaction;
	// private final String user;
	private final long startTime;

	private final transient Thread executeThread;

	private String wmName;

	private String applicationName;

	private String moduleName;

	private final boolean standby;

	private final boolean stuck;

	private final boolean hogger;

	public ExecuteThreadRuntime(ExecuteThread executethread) {
		executeThread = executethread;
		WorkAdapter workadapter = executethread.getCurrentWork();
		currentWork = workadapter == null ? null : workadapter.toString();
		lastRequest = null;
		servicedRequests = executethread.getExecuteCount();
		name = executethread.getName();
		standby = executethread.isStandby();
		hogger = executethread.isHog();
		stuck = executethread.isStuck();
		startTime = executethread.getTimeStamp();
		try {
			ServerWorkManagerImpl serverworkmanagerimpl = executethread
					.getWorkManager();
			wmName = serverworkmanagerimpl.getName();
			applicationName = serverworkmanagerimpl.getApplicationName();
			moduleName = serverworkmanagerimpl.getModuleName();
		} catch (Exception exception1) {
			wmName = null;
			applicationName = null;
			moduleName = null;
		}
	}

}
