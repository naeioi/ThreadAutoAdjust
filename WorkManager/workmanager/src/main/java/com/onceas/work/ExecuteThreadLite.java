package com.onceas.work;

import com.onceas.work.threadpool.AuditableThread;
import com.onceas.work.threadpool.KernelStatus;

public class ExecuteThreadLite extends AuditableThread {
	/**
	 * ExecuteThreadLite
	 * 
	 * @param i
	 *            int： 序号
	 * @param workmanagerlite
	 *            WorkManagerLite：隶属于的workmanagerLite
	 * @param threadgroup
	 *            ThreadGroup：隶属于的线程池
	 */
	ExecuteThreadLite(int i, WorkManagerLite workmanagerlite,
			ThreadGroup threadgroup) {
		super(threadgroup, "ExecuteThread: '" + i + "' for queue: '"
				+ workmanagerlite.getName() + "'");
		init(workmanagerlite);
	}

	protected void init(WorkManagerLite workmanagerlite) {
		if (DebugWM.debug_Thread == 1) {
			System.out
					.println("ExcuteThreadLite:   void init(WorkManagerLite workmanagerlite)");
		}
		wm = workmanagerlite;
		hashcode = getName().hashCode();
		setDaemon(true);
	}

	public int hashCode() {
		return hashcode;
	}

	synchronized void notifyRequest(Runnable runnable1) {
		if (DebugWM.debug_Thread == 1) {
			System.out
					.println("ExcuteThreadLite:   synchronized void notifyRequest");
		}
		runnable = runnable1;
		notify();
	}

	void setRequest(Runnable runnable1) {
		runnable = runnable1;
	}

	private synchronized void waitForRequest() {
		if (DebugWM.debug_Thread == 1) {
			System.out
					.println("ExcuteThreadLite:   synchronized void waitForRequest()");
		}
		while (runnable == null)
			try {
				wait();
			} catch (InterruptedException interruptedexception) {
			}
	}

	public void run() {
		if (DebugWM.debug_Thread == 1) {
			System.out.println("ExcuteThreadLite:    void run()");
		}
		synchronized (this) {
			started = true;
			readyToRun();
			notify();
		}
		do
			try {
				do {
					do {
						if (runnable != null)
							execute(runnable);
						reset();
						wm.registerIdle(this);
					} while (runnable != null);
					waitForRequest();
				} while (true);
			} catch (ThreadDeath threaddeath) {
				if (KernelStatus.isServer())
					throw threaddeath;
			}
		while (true);
	}

	void execute(Runnable runnable1) {
		if (DebugWM.debug_Thread == 1) {
			System.out
					.println("ExcuteThreadLite:    void execute(Runnable runnable1)");
		}
		try {
			runnable1.run();
		} catch (ThreadDeath threaddeath) {
			throw threaddeath;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	protected final void reset() {
		super.reset();
		runnable = null;
	}

	public boolean isStarted() {
		return started;
	}

	public WorkManagerLite getWorkManager() {
		if (DebugWM.debug_Thread == 1) {
			System.out
					.println("ExcuteThreadLite:   WorkManagerLite getWorkManager()");
		}
		return wm;
	}

	private WorkManagerLite wm;

	private int hashcode;

	private Runnable runnable;

	private boolean started;
}
