package com.onceas.work;

import com.onceas.work.constraint.ShutdownCallback;

public interface WorkManagerService extends WorkManager {
	public static interface WorkListener {

		public abstract void preScheduleWork();

		public abstract void postScheduleWork();
	}

	public abstract int getState();

	public abstract void start();

	public abstract boolean isShutdown();

	public abstract void shutdown(ShutdownCallback shutdowncallback);

	public abstract void forceShutdown();

	public abstract WorkManager getDelegate();

	public abstract void cleanup();

	public abstract void startRMIGracePeriod(WorkListener worklistener);

	public abstract void endRMIGracePeriod();

	public static final int RUNNING = 1;

	public static final int WAIT_FOR_PENDING_TX = 2;

	public static final int SHUTDOWN = 3;
}
