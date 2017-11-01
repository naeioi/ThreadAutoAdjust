package com.onceas.work.threadpool;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.onceas.runtime.work.ExecuteThreadRuntime;
import com.onceas.runtime.work.ExecuteThreadRuntimeMBean;
import com.onceas.work.DebugWM;
import com.onceas.work.WorkAdapter;
import com.onceas.work.constraint.RequestManager;
import com.onceas.work.constraint.ServerWorkManagerImpl;

public final class ExecuteThread extends AuditableThread {
	private static final class RequestDeath extends Error {

		private RequestDeath() {
		}

	}

	public ExecuteThread(int i, String s) {
		super("ExecuteThread: '" + i + "' for queue: '" + s + "'");
		stuckThread = false;
		started = false;
		executeCount = 0;
		timeStamp = 0L;
		standby = false;
		init(i);
	}

	public ExecuteThread(int i, String s, ThreadGroup threadgroup) {
		super(threadgroup, "ExecuteThread: '" + i + "' for queue: '" + s + "'");
		stuckThread = false;
		started = false;
		executeCount = 0;
		timeStamp = 0L;
		standby = false;
		init(i);
	}

	private void init(int i) {
		id = i;
		date = new Date();
		date.setYear(date.getYear());
		calendar = new GregorianCalendar();
		defaultContextClassLoader = super.getContextClassLoader();
		setContextClassLoader(defaultContextClassLoader);
		hashcode = getName().hashCode();
		setDaemon(true);
	}

	public int hashCode() {
		return hashcode;
	}

	public boolean isStarted() {
		return started;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public WorkAdapter getCurrentWork() {
		return workEntry;
	}

	public ServerWorkManagerImpl getWorkManager() {
		return workManager;
	}

	public Date getDate() {
		return date;
	}

	public int getExecuteCount() {
		return executeCount;
	}

	void setTimeStamp(long l) {
		timeStamp = l;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setStuckThread(boolean flag) {
		stuckThread = flag;
	}

	public boolean isStuck() {
		return stuckThread;
	}

	public void setStandby(boolean flag) {
		standby = flag;
	}

	public boolean isStandby() {
		return standby;
	}

	public synchronized void notifyRequest(WorkAdapter workadapter) {
		workEntry = workadapter;
		workManager = workadapter.wm;
		notify();
	}

	public void setRequest(WorkAdapter workadapter, long l) {
		workEntry = workadapter;
		workManager = workadapter.wm;
		timeStamp = l;
		setThreadPriority();
	}

	private synchronized void waitForRequest() {
		while (workEntry == null)
			try {
				wait();
			} catch (InterruptedException interruptedexception) {
			}
	}

	public void run() {
		runtime++;
		synchronized (this) {
			started = true;
			readyToRun();
			notify();
		}
		do
			try {
				do {
					do {
						if (workEntry != null) {
							timeStamp = System.currentTimeMillis();
							workEntry.startedTimeStamp = timeStamp;
							execute(workEntry.getWork());
						}
						WorkAdapter workadapter = workEntry;
						reset();
						RequestManager.getInstance().registerIdle(this,
								workadapter);
						workadapter = null;
					} while (workEntry != null);
					waitForRequest();
				} while (true);
			} catch (Exception obj) {
				obj.printStackTrace();
				return;
			}
		while (true);
	}

	void execute(Runnable runnable) {
		try {
			executeCount++;
			runnable.run();
		} catch (ThreadDeath threaddeath) {
			throw threaddeath;
		} catch (RequestManager.ShutdownError shutdownerror) {
			throw shutdownerror;
		} catch (RequestDeath requestdeath) {
			requestdeath.printStackTrace();
		} catch (OutOfMemoryError outofmemoryerror) {
			outofmemoryerror.printStackTrace();
			workEntry.getWorkManager();
			ServerWorkManagerImpl.notifyOOME(outofmemoryerror);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public void setThreadPriority() {
		try {
			if (workEntry.requestClass != null) {
				int i = workEntry.requestClass.getThreadPriority();
				if (getPriority() != i)
					setPriority(i);
				if (DebugWM.debug_ThreadPriorty) {
					log(this.getName() + " 's priorty had set as"
							+ this.getPriority());
				}
			}
		} catch (SecurityException securityexception) {
		}
	}

	public ClassLoader getContextClassLoader() {
		return contextClassLoader;
	}

	public void setContextClassLoader(ClassLoader classloader) {
		contextClassLoader = classloader == null ? ClassLoader
				.getSystemClassLoader() : classloader;
	}

	public final void reset() {
		super.reset();
		if (!AppletStatus.isApplet())
			setContextClassLoader(defaultContextClassLoader);
		workEntry = null;
	}

	public ExecuteThreadRuntimeMBean getRuntime() {
		return new ExecuteThreadRuntime(this);
	}

	public void setHog(boolean flag) {
		isHogger = flag;
	}

	public boolean isHog() {
		return isHogger;
	}

	public boolean isExecutingInternalWork() {
		try {
			return workManager.isInternal();
		} catch (Exception exception) {
			return false;
		}
	}

	private static void log(String s) {

		System.out.println("<ExecuteThread>" + s);
	}

	private static final boolean ASSERT = true;

	private static final Throwable REQUEST_DEATH = new RequestDeath();

	private Calendar calendar;

	private ClassLoader defaultContextClassLoader;

	private Date date;

	private int hashcode;

	public int id;

	private ClassLoader contextClassLoader;

	private boolean stuckThread;

	private boolean started;

	private int executeCount;

	public long timeStamp;

	private WorkAdapter workEntry;

	private ServerWorkManagerImpl workManager;

	private boolean standby;

	private boolean isHogger;

	private static int nullwork = 0;

	private static int runtime = 0;

}
