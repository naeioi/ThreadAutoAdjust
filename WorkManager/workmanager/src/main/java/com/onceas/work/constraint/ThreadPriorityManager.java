package com.onceas.work.constraint;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.onceas.work.DebugWM;

public final class ThreadPriorityManager {
	private static final class Factory {

		static final ThreadPriorityManager THE_ONE = new ThreadPriorityManager();

		private Factory() {
		}
	}

	public static boolean DISABLE_THREAD_PRIORITY = false;

	private static final long PERIOD = 6000L;

	private static final double SHORT_OUTLIER_MULTIPLE = 1.5D;

	private static final double LONG_OUTLIER_MULTIPLE = 2D;

	private static final int MIN_PRIORITY = 1;

	private static final int LOW_PRIORITY = 2;

	private static final int NORMAL_PRIORITY = 5;

	private static final int HIGH_PRIORITY = 9;

	private long lastExecutionTime;

	private long sum;

	private long square;

	private int count;

	private static Logger log = Logger.getLogger(ThreadPriorityManager.class.toString());

	private ThreadPriorityManager() {
	}

	public static ThreadPriorityManager getInstance() {
		return Factory.THE_ONE;
	}

	public void computeThreadPriorities(List list) {
		if (isDisabled() || notDueForExecution())
			return;
		if (list == null || list.size() == 0)
			return;
		count = 0;
		sum = 0L;
		square = 0L;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ServiceClassSupport serviceclasssupport = (ServiceClassSupport) iterator
					.next();
			long l = serviceclasssupport
					.getIncrementForThreadPriorityCalculation();
			count++;
			if (debugEnabled())
				log(serviceclasssupport.getName() + " has incr " + l);
			sum += l;
			square += l * l;
		}

		if (count < 2)
			return;
		List list1;
		double d = ServiceClassStatsSupport.div(sum, count);
		double d2 = ServiceClassStatsSupport.stdev(sum, square, count);
		int i = count;
		count = 0;
		sum = 0L;
		square = 0L;
		list1 = assignThreadPriorityToRequestClasses(list, d, d2);
		if (list1 == null || list1.size() == i || list1.size() < 2)
			return;
		try {
			double d1 = ServiceClassStatsSupport.div(sum, count);
			double d3 = ServiceClassStatsSupport.stdev(sum, square, count);
			assignThreadPriorityToRequestClasses(list1, d1, d3);
		} catch (ConcurrentModificationException concurrentmodificationexception) {
		}
		return;
	}

	private static boolean isDisabled() {
		return DISABLE_THREAD_PRIORITY;
	}

	/**
	 * excute compute priority every 6 secs
	 * 
	 * @return
	 */
	private boolean notDueForExecution() {
		long l = System.currentTimeMillis();
		if (l - lastExecutionTime < PERIOD) {
			return true;
		} else {
			lastExecutionTime = l;
			return false;
		}
	}

	private List assignThreadPriorityToRequestClasses(List list, double d,
			double d1) {
		if (d1 == 0.0D)
			return null;
		if (debugEnabled())
			log("[mean] " + d);
		if (debugEnabled())
			log("[stdev] " + d1);
		ArrayList arraylist = new ArrayList();
		Iterator iterator = list.iterator();
		do {
			if (!iterator.hasNext())
				break;
			ServiceClassSupport serviceclasssupport = (ServiceClassSupport) iterator
					.next();
			long l = serviceclasssupport
					.getIncrementForThreadPriorityCalculation();
			if (lowerThreadPriority(l, d, d1)) {
				if (!serviceclasssupport.isInternal()) {
					if (debugEnabled())
						log(serviceclasssupport.getName() + " with incr " + l
								+ " is set to low thread priority");
					serviceclasssupport.setThreadPriority(LOW_PRIORITY);
				}
			} else {
				if (increaseThreadPriority(l, d, d1)) {
					if (debugEnabled())
						log(serviceclasssupport.getName() + " with incr " + l
								+ " is set to high thread priority");
					serviceclasssupport.setThreadPriority(HIGH_PRIORITY);
				} else if (serviceclasssupport.getThreadPriority() > MIN_PRIORITY)
					serviceclasssupport.setThreadPriority(NORMAL_PRIORITY);
				arraylist.add(serviceclasssupport);
				count++;
				sum += l;
				square += l * l;
				if (debugEnabled())
					log(serviceclasssupport.getName() + " thread priority is "
							+ serviceclasssupport.getThreadPriority());
			}
		} while (true);
		return arraylist;
	}

	private static boolean increaseThreadPriority(long l, double d, double d1) {
		boolean flag = LONG_OUTLIER_MULTIPLE * d1 > d;
		double d2 = LONG_OUTLIER_MULTIPLE;
		if (flag)
			d2 = 1.0D;
		return (double) l < d - d2 * d1;
	}

	private static boolean lowerThreadPriority(long l, double d, double d1) {
		boolean flag = LONG_OUTLIER_MULTIPLE * d1 > d;
		double d2 = LONG_OUTLIER_MULTIPLE;
		if (flag)
			d2 = SHORT_OUTLIER_MULTIPLE;
		return (double) l > d + d2 * d1;
	}

	private static void setThreadPriority(Thread thread, int i) {
		try {
			if (thread.getPriority() != i)
				thread.setPriority(i);
			log("Thread" + thread.getName() + "'s priority is set to " + i);
		} catch (SecurityException securityexception) {
		}
	}

	/**
	 * 将Hogger的线程优先级降低 这一动作在周期性的检查Stuck线程时执行
	 * 
	 * @param thread
	 * @param flag
	 */
	public static void handleHogger(Thread thread, boolean flag) {
		if (DebugWM.debug_StuckThread)
			log("now change the thread's priorty");

		if (isDisabled())
			return;
		if (flag)
			setThreadPriority(thread, NORMAL_PRIORITY);
		else
			setThreadPriority(thread, MIN_PRIORITY);
	}

	private static boolean debugEnabled() {
		return DebugWM.debug_ThreadPriortyManager;
	}

	private static void log(String s) {
		// log.info("<ThreadPriorityManager>" + s);
		System.out.println("<ThreadPriorityManager>" + s);
	}
}
