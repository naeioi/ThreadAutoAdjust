package com.onceas.work.constraint;

import java.util.ArrayList;

import com.onceas.util.collection.UnsyncCircularQueue;
import com.onceas.work.DebugWM;
import com.onceas.work.WorkAdapter;
import com.onceas.work.management.notification.WMUpdateEvent;
import com.onceas.work.management.notification.WMUpdateListener;

public final class MinThreadsConstraint implements WMUpdateListener {
	private final class ProxyEntry extends WorkAdapter {
		// this attribute of proxy cannot be change: add by syk
		public final boolean started = false;

		// end

		public WorkAdapter getNextEntry() {
			return null;
		}

		public boolean readyToRun() {
			// return queue.size() > 0;
			// add max constraint check and proxy need to put back: by syk
			return queue.size() > 0
					&& (wm.getMaxThreadsConstraint() == null || wm
							.getMaxThreadsConstraint().readyToRun(this));
		}

		public WorkAdapter getEffective() {
			WorkAdapter workadapter = getNext();
			if (MinThreadsConstraint.DEBUG)
				MinThreadsConstraint.log("entry [" + workadapter + "] from '"
						+ name + "' is picked up from queue. Queue length="
						+ queue.size() + ", inProgress=" + inProgress);
			return workadapter;
		}

		public final boolean hasEffective() {
			return queue.size() > 0;
		}

		public void run() {
		}

		public String toString() {
			return name + ", count=" + count + ", queueSize=" + getQueueSize()
					+ ", executing=" + getExecutingCount() + ", mustRun="
					+ getMustRunCount() + ", outOfOrder="
					+ getOutOfOrderExecutionCount();
		}

		private ProxyEntry() {
			super();
		}

	}

	public MinThreadsConstraint(String s, int i) {
		name = s;
		count = i;
		if (DEBUG)
			log("created '" + s + "' with count " + i);
	}

	public int getCount() {
		return count;
	}

	public String getName() {
		return name;
	}

	WorkAdapter add(WorkAdapter workadapter) {
		queue.put(workadapter);
		if (DEBUG)
			log("added [" + workadapter + "] to '" + name + "'. Queue length="
					+ queue.size() + ", inProgress=" + inProgress);
		// begin :by syk
		// need to set wm for proxy, or you'll meet NullPointerException when
		// operating wm througth proxy.
		// precondition: workadapter != null
		if (PROXY.wm == null)
			PROXY.setWorkManager(workadapter.getWorkManager());
		// end
		return PROXY;
	}

	boolean isConstraintSatisfied() {
		return count <= 0 || inProgress >= count;
	}

	public int getMustRunCount() {
		if (queue.empty() || inProgress >= count)
			return 0;
		else
			return Math.min(queue.size(), count - inProgress);
	}

	final WorkAdapter getMustRun(long l) {
		if (inProgress >= count)
			return null;
		WorkAdapter workadapter = getNext();
		if (workadapter != null) {
			outOfOrderExecutionCount++;
			currentWaitTime = l - workadapter.creationTimeStamp;
			maxWaitTime = Math.max(maxWaitTime, currentWaitTime);
			if (DEBUG)
				log("must run [" + workadapter + "] from '" + name
						+ "'. Queue length=" + queue.size() + ", inProgress="
						+ inProgress);
		}
		return workadapter;
	}

	private WorkAdapter getNext() {// return null or the next work that does
									// not yet start
		do {
			if (queue.empty())
				break;
			WorkAdapter workadapter = (WorkAdapter) queue.get();
			if (!workadapter.started)
				return workadapter;
			if (DEBUG && workadapter != null)
				log("[ALERT] entry [" + workadapter + "] from '" + name
						+ "' is already started. Queue length=" + queue.size()
						+ ", inProgress=" + inProgress);
		} while (true);
		// while(!queue.empty()){
		// WorkAdapter workadapter = (WorkAdapter) queue.get();
		// if(!workadapter.started)
		// return workadapter;
		// }
		return null;
	}

	public int getQueueSize() {
		return queue.size();
	}

	public int getExecutingCount() {
		return inProgress;
	}

	public long getCompletedCount() {
		return totalCompletedCount;
	}

	public long getOutOfOrderExecutionCount() {
		return outOfOrderExecutionCount;
	}

	public long getMaxWaitTime() {
		return maxWaitTime;
	}

	public long getCurrentWaitTime() {
		return currentWaitTime;
	}

	public void updatePerformed(WMUpdateEvent e) {
		Object proposed = e.getProposed();
		int i = count;
		if (proposed instanceof MinThreadsConstraint) {
			count = ((MinThreadsConstraint) proposed).getCount();
		}
		if (count > i) {
			RequestManager.getInstance().executeImmediately(getMustRunList());
		}
	}

	WorkAdapter[] getMustRunList() {
		RequestManager requestmanager;
		long l;
		requestmanager = RequestManager.getInstance();
		l = System.currentTimeMillis();
		ArrayList arraylist = new ArrayList();
		RequestManager requestmanager1 = requestmanager;
		synchronized (requestmanager) {
			int i;
			i = getMustRunCount();
			if (i == 0)
				return null;
			int j = 0;
			do {
				if (j >= i)
					break;
				WorkAdapter workadapter = getMustRun(l);
				if (workadapter == null)
					break;
				arraylist.add(workadapter);
				j++;
			} while (true);
		}
		if (arraylist.size() == 0) {
			return null;
		} else {
			WorkAdapter aworkadapter[] = new WorkAdapter[arraylist.size()];
			arraylist.toArray(aworkadapter);
			return aworkadapter;
		}
	}

	private static void log(String s) {
		/*
		 * if(DEBUG) WorkManagerLogger.logDebug("<MinConstraint>" + s);
		 */
		if (DEBUG)
			System.out.println("<MinConstraint>" + s);

	}

	public String toString() {
		return PROXY.toString();
	}

	public final void dumpAndDestroy() {
		for (int i = 0; i < queue.size(); i++) {
			WorkAdapter workadapter = (WorkAdapter) queue.get();
			System.out.println("--- count " + i + " --- ");
			System.out.println(workadapter.dump() + "\n");
		}

	}

	private void increaseCount(int i) {
		int j = count;
		count = i;
		if (count > j)
			RequestManager.getInstance().executeImmediately(getMustRunList());
	}

	private static final boolean DEBUG = DebugWM.debug_MinThread;

	private final String name;

	private final UnsyncCircularQueue queue = new UnsyncCircularQueue();

	private final WorkAdapter PROXY = new ProxyEntry();

	protected int inProgress;

	protected long totalCompletedCount;

	private int count;

	private long outOfOrderExecutionCount;

	private long maxWaitTime;

	private long currentWaitTime;
}
