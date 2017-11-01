package com.onceas.work;

import com.onceas.work.threadpool.AuditableThread;

public abstract class WorkManagerImpl implements WorkManager {

	public WorkManagerImpl() {
	}

	public String getName() {
		return wmName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setInternal() {
		isInternal = true;
	}

	public void setInternal(boolean flag) {
		isInternal = flag;
	}

	public boolean isInternal() {
		return isInternal;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public void setThreadCount(int i) throws IllegalStateException,
			SecurityException {
		throw new IllegalStateException("WorkManager [" + toString()
				+ "] does not support setting thread count");
	}

	public static void executeDaemonTask(String s, int i, Runnable runnable) {
		AuditableThread auditablethread = new AuditableThread(
				ONCEAS_DAEMON_GROUP, runnable, s);
		auditablethread.setDaemon(true);
		auditablethread.setPriority(i);
		auditablethread.start();
	}

	public String getRuntimeType() {
		return runtimeType;
	}

	public void setRuntimeType(String runtimeType) {
		this.runtimeType = runtimeType;
	}

	public String toString() {
		return applicationName + "@" + moduleName + "@" + wmName;
	}

	private static final ThreadGroup ONCEAS_DAEMON_GROUP = new ThreadGroup(
			"Non-Pooled Threads");

	protected String wmName;

	protected String applicationName;

	protected String moduleName;

	protected boolean isInternal;

	// runtimeType标志与wm“一一”对应的WorkManagerRuntime的类型，在创建WorkManagerRuntime时设置；用来与
	// 上面的3个xxName一起构造WorkManagerRuntime的ObjectName : by syk
	protected String runtimeType;
}
