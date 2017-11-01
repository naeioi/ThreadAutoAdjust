package com.onceas.timers.internal;

import java.util.TreeMap;

import com.onceas.timers.Timer;

class TimerThread {
	class Thread extends java.lang.Thread {

		public void run() {
			synchronized (timerThread) {
				do {
					while (timerThread.isStopped())
						try {
							timerThread.wait();
						} catch (InterruptedException interruptedexception) {
						}
					if (timerTree.isEmpty()) {
						if (timerThread.isHalted())
							break;
						try {
							timerThread.wait();
						} catch (InterruptedException interruptedexception1) {
						}
						continue;
					}
					TimerImpl timerimpl = (TimerImpl) tree.firstKey();
					if (timerThread.isHalted()) {
						timerTree.remove(timerimpl);
					} else {
						long l = timerimpl.getTimeout()
								- System.currentTimeMillis();
						if (l <= 0L) {
							timerTree.remove(timerimpl);
						} else {
							try {
								timerThread.wait(l);
							} catch (InterruptedException interruptedexception2) {
							}
							continue;
						}
					}
					timerimpl.getTimerManager().execute();
				} while (true);
			}
		}

		private TimerThread timerThread;

		private TreeMap timerTree;

		public Thread(TimerThread timerthread1, TreeMap treemap) {
			super();
			timerThread = timerthread1;
			timerTree = treemap;
			setName("onceas.timers.TimerThread");
			setPriority(9);
			setDaemon(true);
		}
	}

	public TimerThread() {
		synchronized (TimerThread.class) {
			if (singleton != null)
				throw new IllegalStateException();
			/**
			 * if(!KernelStatus.isInitialized()) { String s = "kernel.Kernel";
			 * try { Class class2 = Class.forName(s, true,
			 * ClassLoader.getSystemClassLoader()); throw new
			 * AssertionError("Kernel needs to be initialized before starting
			 * TimerThread."); } catch(ClassNotFoundException
			 * classnotfoundexception) { } } if(KernelStatus.isServer()) try {
			 * new TimerRuntime(); } catch (ManagementException e) {
			 * e.printStackTrace(); }
			 */
			// new TimerRuntime();
			thread = new Thread(this, tree = new TreeMap());
			thread.start();
			singleton = this;
		}
	}

	synchronized void stop() {
		state = 1;
	}

	synchronized void start() {
		state = 0;
		notifyAll();
	}

	synchronized void halt() {
		state = 2;
		notifyAll();
	}

	void add(Timer timer) {
		tree.put(timer, timer);
		if (state != 0)
			return;
		if (tree.firstKey() == timer)
			notifyAll();
	}

	void remove(Timer timer) {
		tree.remove(timer);
	}

	int getNextCounter() {
		return ++counter;
	}

	boolean isStarted() {
		return state == 0;
	}

	boolean isStopped() {
		return state == 1;
	}

	boolean isHalted() {
		return state == 2;
	}

	public static TimerThread getTimerThread() {
		synchronized (TimerThread.class) {
			if (singleton != null)
				return singleton;
			return new TimerThread();
		}
	}

	static final int STARTED = 0;

	static final int STOPPED = 1;

	static final int HALTED = 2;

	private static TimerThread singleton;

	private Thread thread;

	private int counter;

	private int state;

	private TreeMap tree;

}
