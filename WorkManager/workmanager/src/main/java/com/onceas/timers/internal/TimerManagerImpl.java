package com.onceas.timers.internal;

/**
 * 文件：TimerManagerImpl.java
 * 项目：OnceAs
 * 功能：Timers的入口。
 * 备注：无
 * 作者：张磊
 * 日期：2006-11-15
 * 修改记录：
 *      无
 * 本软件的版权属中科院软件所软件工程技术研究与开发中心所有，保留所有权利。
 */
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import com.onceas.timers.Timer;
import com.onceas.timers.TimerListener;
import com.onceas.timers.TimerManager;
import com.onceas.work.WorkManager;
import com.onceas.work.WorkManagerFactory;

public class TimerManagerImpl implements TimerManager {
	private static final int STOPPING = 1;

	private static final int STOPPED = 2;

	private static final int SUSPENDING = 3;

	private static final int SUSPENDED = 4;

	private static final int RUNNING = 5;

	private static HashMap timerManagers;

	private static boolean initialized;

	private TimerImpl timer;

	private TimerThread timerThread;

	private String name;

	private WorkManager workManager;

	private int executing;

	private int state;

	private TreeMap tree;

	TimerManagerImpl(TimerThread timerthread, String s, WorkManager workmanager) {
		timerThread = timerthread;
		name = s;
		workManager = workmanager;
		tree = new TreeMap();
		state = RUNNING;
	}

	public void stop() {
		try {
			waitForStop(0L);
		} catch (InterruptedException interruptedexception) {
			interruptedexception.printStackTrace();
		}
	}

	public synchronized boolean isStopped() {
		return state == STOPPED || timerThread.isStopped();
	}

	public synchronized boolean isStopping() {
		return state == STOPPING || isStopped();
	}

	public synchronized boolean isSuspended() {
		return state == SUSPENDED;
	}

	public synchronized boolean isSuspending() {
		return state == SUSPENDING || isSuspended();
	}

	public void suspend() {
		try {
			waitForSuspend(0L);
		} catch (InterruptedException interruptedexception) {
			interruptedexception.printStackTrace();
		}
	}

	public boolean waitForSuspend(long l) throws InterruptedException,
			IllegalStateException, IllegalArgumentException {
		TimerThread timerthread = timerThread;
		synchronized (timerthread) {
			long l1;
			long l2;
			if (state == STOPPED)
				throw new IllegalArgumentException(
						"Cannot suspend a TimerManager that is in STOPPED state");
			state = SUSPENDING;
			update();
			l1 = l;
			l2 = System.currentTimeMillis();
			long l3;
			long l4;
			do {
				if (!isExecuting())
					return true;
				timerThread.wait(l1);
				if (l1 <= 0L)
					continue; /* Loop/switch isn't completed */
				l3 = System.currentTimeMillis();
				l4 = l3 - l2;
				if (l4 < l1) {
					l1 -= l4;
					l2 = l3;
				} else {
					if (isExecuting())
						return false;
					state = SUSPENDED;
					return true;
				}
			} while (state == SUSPENDING);

			state = SUSPENDED;
			if (state == SUSPENDED)
				throw new IllegalStateException(
						"TimerManager state changed to "
								+ state
								+ " while suspending. waitForSuspend is aborted.");

		}

		return true;
	}

	public boolean waitForStop(long l) throws InterruptedException,
			IllegalArgumentException {

		TimerThread timerthread = timerThread;
		synchronized (timerthread) {
			long l1;
			long l2;
			state = this.STOPPED;
			update();
			while (!tree.isEmpty()) {
				TimerImpl timerimpl = (TimerImpl) tree.firstKey();
				tree.remove(timerimpl);
				timerimpl.setStopped();
				try {
					executing++;
					workManager.schedule(timerimpl);
				} catch (Exception exception) {
					complete(timerimpl);
				}
			}
			l1 = l;
			l2 = System.currentTimeMillis();
			long l3;
			long l4;
			do {
				if (!isExecuting())
					return true;
				timerThread.wait(l1);
				if (l1 <= 0L)
					continue; /* Loop/switch isn't completed */
				l3 = System.currentTimeMillis();
				l4 = l3 - l2;
				if (l4 < l1) {
					l1 -= l4;
					l2 = l3;
				} else {
					if (!isExecuting()) {
						state = this.SUSPENDED;
						boolean flag = true;
						timerManagers.remove(name);

						return flag;
					} else {
						boolean flag = false;
						timerManagers.remove(name);
						return flag;
					}
				}
			} while (state == this.STOPPED);
			if (state != STOPPED)
				throw new IllegalStateException();
			timerManagers.remove(name);
			state = this.SUSPENDING;

			return true;
		}
	}

	public void resume() {
		synchronized (timerThread) {
			if (state != this.RUNNING) {
				if (state == this.SUSPENDING)
					throw new IllegalArgumentException(
							"Cannot resume a TimerManager that is in STOPPED state");
				state = RUNNING;
				update();
				timerThread.notifyAll();
			}
		}
	}

	public Timer schedule(TimerListener timerlistener, long l) {
		return schedule(timerlistener, l, 0L);
	}

	public Timer schedule(TimerListener timerlistener, Date date) {
		return schedule(timerlistener, date, 0L);
	}

	public Timer schedule(TimerListener timerlistener, long l, long l1) {
		if (l < 0L)
			throw new IllegalArgumentException("Delay is negative.");
		if (l1 < 0L)
			throw new IllegalArgumentException("Period is negative.");
		if (state == this.SUSPENDING)
			throw new IllegalArgumentException(
					"TimerManager is in STOPPED state");
		else
			return add(new TimerImpl(this, timerlistener, System
					.currentTimeMillis()
					+ l, -l1));
	}

	public Timer schedule(TimerListener timerlistener, Date date, long l) {
		if (l < 0L)
			throw new IllegalArgumentException("Period is negative.");
		if (state == SUSPENDING)
			throw new IllegalArgumentException(
					"TimerManager is in STOPPED state");
		else
			return add(new TimerImpl(this, timerlistener, date.getTime(), -l));
	}

	public Timer scheduleAtFixedRate(TimerListener timerlistener, Date date,
			long l) {
		if (l < 0L)
			throw new IllegalArgumentException("Period is negative.");
		else
			return add(new TimerImpl(this, timerlistener, date.getTime(), l));
	}

	public Timer scheduleAtFixedRate(TimerListener timerlistener, long l,
			long l1) {
		if (l < 0L)
			throw new IllegalArgumentException("Delay is negative.");
		if (l1 < 0L)
			throw new IllegalArgumentException("Period is negative.");
		else
			return add(new TimerImpl(this, timerlistener, System
					.currentTimeMillis()
					+ l, l1));
	}

	boolean cancel(TimerImpl timerimpl) {
		synchronized (timerThread) {
			if (timerimpl.isCancelled())
				return false;
			timerimpl.setCancelled(true);
			timerimpl = (TimerImpl) tree.remove(timerimpl);
			if (timerimpl != null) {
				executing++;
				TimerListener timerlistener = timerimpl.getListener();
				if (timerlistener instanceof com.onceas.timers.CancelTimerListener)
					workManager.schedule(timerimpl);
				else
					complete(timerimpl);
				update();
				return true;
			} else {
				return false;
			}
		}
	}

	private void update() {
		TimerImpl timerimpl = tree.isEmpty() ? null : (TimerImpl) tree
				.firstKey();
		if ((state != this.RUNNING || timer != timerimpl) && timer != null) {
			timerThread.remove(timer);
			timer = null;
		}
		if (state == RUNNING && timer != timerimpl && timerimpl != null) {
			timerThread.add(timerimpl);
			timer = timerimpl;
		}
	}

	private Timer add(TimerImpl timerimpl) {
		synchronized (timerThread) {
			if (isStopped())
				throw new IllegalStateException();
			timerimpl.setCounter(timerThread.getNextCounter());
			tree.put(timerimpl, timerimpl);
			update();
		}
		return timerimpl;
	}

	void execute() {
		timer = null;
		for (TimerImpl timerimpl = (TimerImpl) tree.firstKey(); timerimpl
				.isExpired(); timerimpl = (TimerImpl) tree.firstKey()) {
			tree.remove(timerimpl);
			if (isStopped())
				timerimpl.setStopped();
			try {
				executing++;
				workManager.schedule(timerimpl);
			} catch (Exception exception) {
				complete(timerimpl);
			}
			if (tree.isEmpty())
				return;
		}

		update();
	}

	void complete(TimerImpl timerimpl) {
		synchronized (timerThread) {
			executing--;
			if (state != RUNNING && executing == 0)
				timerThread.notifyAll();
			if (timerimpl.isCancelled()) {
				timerimpl.cleanup();
				return;
			}
		}
		long l;
		l = timerimpl.getPeriod();
		if (l == 0L) {
			timerimpl.cleanup();
			return;
		}
		long l1 = System.currentTimeMillis();
		if (l > 0L) {
			long l2 = timerimpl.getTimeout();
			do
				l2 += l;
			while (l2 + l < l1);
			timerimpl.setTimeout(l2);
		} else {
			timerimpl.setTimeout(l1 - l);
		}
		if (isStopped())
			timerimpl.cleanup();
		else
			add(timerimpl);
	}

	private boolean isExecuting() {
		return executing > 0;
	}

	public WorkManager getWorkManager() {
		return workManager;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return tree.size();
	}

	static Iterator getTimerManagers() {
		TimerThread timerthread = TimerThread.getTimerThread();
		synchronized (timerthread) {
			if (!initialized)
				initialize();
			return timerManagers.values().iterator();
		}
	}

	static void initialize() {
		timerManagers = new HashMap();
		initialized = true;
	}

	public static TimerManagerImpl getTimerManager(String s,
			WorkManager workmanager) {
		TimerThread timerthread = TimerThread.getTimerThread();
		TimerManagerImpl timermanagerimpl;
		synchronized (timerthread) {
			if (!initialized)
				initialize();
			timermanagerimpl = (TimerManagerImpl) timerManagers.get(s);
			if (timermanagerimpl != null
					&& workmanager == timermanagerimpl.getWorkManager()) {
				return timermanagerimpl;
			}
			if ((workmanager == null)
					|| (timermanagerimpl != null && workmanager != timermanagerimpl
							.getWorkManager())) {
				workmanager = WorkManagerFactory.getInstance().getDefault();
			}
			timermanagerimpl = new TimerManagerImpl(timerthread, s, workmanager);
			timerManagers.put(s, timermanagerimpl);
			return timermanagerimpl;
		}
	}

	public String toString() {
		return "TimerManager '" + getName() + "' that uses WorkManager '"
				+ workManager + "'";
	}
}
