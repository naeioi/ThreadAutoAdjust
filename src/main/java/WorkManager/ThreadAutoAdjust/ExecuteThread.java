package WorkManager.ThreadAutoAdjust;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class ExecuteThread extends Thread {
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
		return workAdapter;
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

	public synchronized void notifyRequest(WorkAdapter workAdapter) {
		this.workAdapter = workAdapter;
		notify();
	}

	public void setRequest(WorkAdapter workAdapter, long l) {
		this.workAdapter = workAdapter;
		timeStamp = l;
	}

	private synchronized void waitForRequest() {
		while (workAdapter == null)
			try {
				wait();
			} catch (InterruptedException interruptedexception) {
			}
	}

	public void run() {
		runtime++;
		synchronized (this) {
			started = true;
			notify();
		}
		do
			try {
				do {
					do {
						if (workAdapter != null) {
							timeStamp = System.currentTimeMillis();
							workAdapter.startedTimeStamp = timeStamp;
							execute(workAdapter);
						}
						WorkAdapter memoryAdapter = workAdapter;
						reset();
						RequestManager.getInstance().registerIdle(this,
								memoryAdapter);
						memoryAdapter = null;
					} while (workAdapter != null);
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
			synchronized (runnable) {
				runnable.run();
				runnable.notify();
			}
		} catch (ThreadDeath threaddeath) {
			throw threaddeath;
		} catch (RequestManager.ShutdownError shutdownerror) {
			throw shutdownerror;
		} catch (RequestDeath requestdeath) {
			requestdeath.printStackTrace();
		} catch (OutOfMemoryError outofmemoryerror) {
			outofmemoryerror.printStackTrace();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
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
		workAdapter = null;
	}


	public void setHog(boolean flag) {
		isHogger = flag;
	}

	public boolean isHog() {
		return isHogger;
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

	private WorkAdapter workAdapter;

	private boolean standby;

	private boolean isHogger;

	private static int nullwork = 0;

	private static int runtime = 0;

}
