package com.onceas.work;

import java.util.ArrayList;
import java.util.List;

import com.onceas.util.collection.Stack;
import com.onceas.util.collection.UnsyncCircularQueue;

public final class WorkManagerLite extends WorkManagerImpl {
	private static final ThreadGroup THREAD_GROUP = initThreadGroup();

	private static final int THREAD_POOL_SIZE = initThreadPoolSize();

	private final Stack idleThreads;

	private final List threadPool;

	private final UnsyncCircularQueue queue;

	private static final String THREAD_POOL_SIZE_PROP = "onceas.thinclient.kernel.ThreadPoolSize";

	private static final int DEFAULT_POOL_SIZE = 5;

	public String getName() {
		return wmName;
	}

	public String getApplicationName() {
		return null;
	}

	public String getModuleName() {
		return null;
	}

	public int getType() {
		return 2;
	}

	public int getConfiguredThreadCount() {
		return threadPool.size();
	}

	private static int initThreadPoolSize() {
		try {
			return Integer.getInteger(THREAD_POOL_SIZE_PROP, DEFAULT_POOL_SIZE)
					.intValue();
		} catch (SecurityException securityexception) {
			return 5;
		} catch (NumberFormatException numberformatexception) {
			return 5;
		}
	}

	private static ThreadGroup initThreadGroup() {
		try {
			return new ThreadGroup("Pooled Threads");
		} catch (SecurityException securityexception) {
			return null;
		}
	}

	private ExecuteThreadLite create(int i) {
		return new ExecuteThreadLite(i, this, THREAD_GROUP);
	}

	private void start(ExecuteThreadLite executethreadlite) {
		executethreadlite.start();
		synchronized (executethreadlite) {
			while (!executethreadlite.isStarted())
				try {
					executethreadlite.wait();
				} catch (InterruptedException interruptedexception) {
				}
		}
	}

	WorkManagerLite() {
		idleThreads = new Stack();
		threadPool = new ArrayList();
		queue = new UnsyncCircularQueue();
		wmName = WorkManagerConstant.ONCEAS_DIRECT;
	}

	WorkManagerLite(String s) {
		this(s, THREAD_POOL_SIZE);
	}

	WorkManagerLite(String s, int i) {
		idleThreads = new Stack();
		threadPool = new ArrayList();
		queue = new UnsyncCircularQueue();
		wmName = s;
		setThreadCount(i);
	}

	public void setThreadCount(int i) {
		if (i > threadPool.size()) {
			synchronized (this) {
				int j = threadPool.size();
				for (int k = j; k < i; k++) {
					ExecuteThreadLite executethreadlite = create(k);
					threadPool.add(executethreadlite);
					start(executethreadlite);
				}
			}
		}
		return;
	}

	public void schedule(Runnable runnable) {
		ExecuteThreadLite executethreadlite;
		if (WorkManagerConstant.ONCEAS_DIRECT == wmName) {
			runnable.run();
			return;
		}
		executethreadlite = null;
		synchronized (this) {
			if (idleThreads.size() > 0)
				executethreadlite = (ExecuteThreadLite) idleThreads.pop();
			if (executethreadlite == null)
				queue.put(runnable);
		}
		executethreadlite.notifyRequest(runnable);
		return;
	}

	public void registerIdle(ExecuteThreadLite executethreadlite) {
		Runnable runnable;

		runnable = null;
		synchronized (this) {
			runnable = (Runnable) queue.get();
			if (runnable == null)
				idleThreads.push(executethreadlite);
		}
		executethreadlite.setRequest(runnable);
		return;
	}

	public boolean executeIfIdle(Runnable runnable) {
		ExecuteThreadLite executethreadlite = null;
		synchronized (this) {
			if (idleThreads.size() == 0)
				return false;
			executethreadlite = (ExecuteThreadLite) idleThreads.pop();
		}
		if (executethreadlite != null) {
			executethreadlite.notifyRequest(runnable);
			return true;
		}
		return false;
	}

	public boolean scheduleIfBusy(Runnable runnable) {
		if (getQueueDepth() > 0) {
			schedule(runnable);
			return true;
		} else {
			return false;
		}
	}

	public int getQueueDepth() {
		return queue.size();
	}

	public boolean isThreadOwner(Thread thread) {
		if (!(thread instanceof ExecuteThreadLite)) {
			return false;
		} else {
			ExecuteThreadLite executethreadlite = (ExecuteThreadLite) thread;
			return this == executethreadlite.getWorkManager();
		}
	}

	public void setInternal() {
	}

	public boolean isInternal() {
		return false;
	}

}
