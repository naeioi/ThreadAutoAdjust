package com.onceas.timers;

import com.onceas.timers.internal.commonj.TimerManagerImpl;
import com.onceas.work.WorkManager;
import com.onceas.work.WorkManagerFactory;

public class TimerManagerFactory {

	private TimerManagerFactory() {
	}

	public static TimerManagerFactory getTimerManagerFactory() {
		if (timerManagerFactory != null) {
			return timerManagerFactory;
		} else {
			initialize();
			return timerManagerFactory;
		}
	}

	private static synchronized void initialize() {
		if (timerManagerFactory != null)
			return;
		timerManagerFactory = new TimerManagerFactory();
		/**
		 * if(!KernelStatus.isInitialized()) { if(KernelStatus.isServer()) throw
		 * new AssertionError("Attempt to access TimerManagerFactory before
		 * kernel is initialized on the server");
		 * KernelInitializer.initializeKernel(); }
		 */
	}

	public synchronized TimerManager getDefaultTimerManager() {
		if (defaultTimerManager == null)
			defaultTimerManager = com.onceas.timers.internal.TimerManagerImpl
					.getTimerManager("onceas.timers.DefaultTimerManager",
							WorkManagerFactory.getInstance().getDefault());
		return defaultTimerManager;
	}

	public TimerManager getTimerManager(String s, String s1) {
		WorkManager workmanager = WorkManagerFactory.getInstance().find(s1);
		if (workmanager == null)
			throw new IllegalArgumentException("No work manager for policy "
					+ s1);
		else
			return getTimerManager(s, workmanager);
	}

	public TimerManager getTimerManager(String s, WorkManager workmanager) {
		if ("onceas.timers.DefaultTimerManager".equals(s)) {
			if (workmanager == WorkManagerFactory.getInstance().getDefault())
				return getDefaultTimerManager();
			else
				throw new IllegalArgumentException(
						"Existing manager has different policy");
		} else {
			return com.onceas.timers.internal.TimerManagerImpl.getTimerManager(
					s, workmanager);
		}
	}

	public TimerManager getTimerManager(String s) {
		return getTimerManager(s, (WorkManager) null);
	}

	public commonj.timers.TimerManager getCommonjTimerManager(String s,
															  WorkManager workmanager) {
		return getCommonjTimerManager(getTimerManager(s, workmanager));
	}

	public commonj.timers.TimerManager getCommonjTimerManager(
			TimerManager timermanager) {
		return new TimerManagerImpl(timermanager);
	}

	private static final String DEFAULT_TIMER_MANAGER = "onceas.timers.DefaultTimerManager";

	private static TimerManagerFactory timerManagerFactory;

	private TimerManager defaultTimerManager;
}
