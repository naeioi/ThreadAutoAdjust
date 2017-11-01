package com.onceas.work.threadpool;

public class AuditableThread extends Thread {
	private ResettableThreadLocal.ThreadStorage threadStorage;

	final FinalThreadLocal.FinalThreadStorage finalThreadStorage;

	protected AuditableThread() {
		finalThreadStorage = new FinalThreadLocal.FinalThreadStorage();
	}

	protected AuditableThread(Runnable runnable) {
		super(runnable);
		finalThreadStorage = new FinalThreadLocal.FinalThreadStorage();
	}

	protected AuditableThread(String s) {
		super(s);
		finalThreadStorage = new FinalThreadLocal.FinalThreadStorage();
	}

	protected AuditableThread(ThreadGroup threadgroup, Runnable runnable) {
		super(threadgroup, runnable);
		finalThreadStorage = new FinalThreadLocal.FinalThreadStorage();
	}

	protected AuditableThread(Runnable runnable, String s) {
		super(runnable, s);
		finalThreadStorage = new FinalThreadLocal.FinalThreadStorage();
	}

	protected AuditableThread(ThreadGroup threadgroup, String s) {
		super(threadgroup, s);
		finalThreadStorage = new FinalThreadLocal.FinalThreadStorage();
	}

	public AuditableThread(ThreadGroup threadgroup, Runnable runnable, String s) {
		super(threadgroup, runnable, s);
		finalThreadStorage = new FinalThreadLocal.FinalThreadStorage();
	}

	protected void reset() {
		if (threadStorage != null)
			threadStorage.reset();
		finalThreadStorage.reset();
	}

	protected void readyToRun() {
		FinalThreadLocal.resetJavaThreadStorage();
	}

	final ResettableThreadLocal.ThreadStorage getThreadStorage() {
		return threadStorage;
	}

	final void setThreadStorage(
			ResettableThreadLocal.ThreadStorage threadstorage) {
		threadStorage = threadstorage;
	}

}
