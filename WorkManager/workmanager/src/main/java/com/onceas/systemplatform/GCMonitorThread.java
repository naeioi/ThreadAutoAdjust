package com.onceas.systemplatform;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

public final class GCMonitorThread extends Thread {

	public static synchronized void init() {
		if (started || VM.getVM().isJRockit()) {
			return;
		} else {
			GCMonitorThread gcmonitorthread = new GCMonitorThread();
			gcmonitorthread.setDaemon(true);
			gcmonitorthread.start();
			started = true;
			return;
		}
	}

	private GCMonitorThread() {
		super("onceas.GCMonitor");
		continueMonitoring = true;
		rq = new ReferenceQueue();
	}

	public synchronized void stopMonitoring() {
		continueMonitoring = false;
	}

	public void run() {
		try {
			while (continueMonitoring) {
				register();
				waitForNotification();
			}
		} catch (InterruptedException interruptedexception) {
			interruptedexception.printStackTrace();
		}
	}

	private void register() throws InterruptedException {
		long l = System.currentTimeMillis();
		if (l - lastRegisterTime < MINIMUM_PERIOD_BETWEEN_REGISTERS)
			Thread.sleep(MINIMUM_PERIOD_BETWEEN_REGISTERS);
		sref = new SoftReference(new Object(), rq);
		lastRegisterTime = l;
	}

	private void waitForNotification() throws InterruptedException {
		while (continueMonitoring) {
			java.lang.ref.Reference reference = rq.remove(TIMEOUT);
			if (reference != null) {
				VM.getVM().sendMajorGCEvent();
				return;
			}
			VM.getVM().sendMinorGCEvent();
		}
	}

	private static final long MINIMUM_PERIOD_BETWEEN_REGISTERS = 2000L;

	private static final long TIMEOUT = 60000L;

	private SoftReference sref;

	private ReferenceQueue rq;

	private boolean continueMonitoring;

	private long lastRegisterTime;

	private static boolean started;

}
