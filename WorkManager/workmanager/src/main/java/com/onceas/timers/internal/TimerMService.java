package com.onceas.timers.internal;

import com.onceas.service.lifecycle.AbstractLifeCycleService;

public class TimerMService extends AbstractLifeCycleService implements
		TimerMServiceMBean {
	public TimerMService() {
		state = 1;
		synchronized (TimerMService.class) {
			if (singleton != null)
				throw new IllegalStateException();
			singleton = this;
		}
	}

	public synchronized void initialize() {
		if (!initialized) {
			timerThread = TimerThread.getTimerThread();
			initialized = true;
		}
	}

	public String getVersion() {
		return "Commonj TimerManager v1.1";
	}

	public synchronized void start() {
		initialize();
		timerThread.start();
		if (state != 0)
			notifyAll();
		state = 0;
	}

	public synchronized void stop() {
		checkInitialized();
		timerThread.halt();
		state = 2;
	}

	public synchronized void halt() {
		stop();
	}

	private void checkInitialized() {
		if (initialized)
			return;
		else
			throw new IllegalStateException("Service is not initialized");
	}

	private static final int STARTED = 0;

	private static final int STOPPED = 1;

	private static final int HALTED = 2;

	private static TimerMServiceMBean singleton;

	private TimerThread timerThread;

	private boolean initialized;

	private int state;
}
