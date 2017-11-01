package com.onceas.work.constraint;

import java.util.ArrayList;

import com.onceas.util.collection.UnsyncCircularQueue;
import com.onceas.work.DebugWM;
import com.onceas.work.WorkAdapter;
import com.onceas.work.management.notification.WMUpdateEvent;
import com.onceas.work.management.notification.WMUpdateListener;

public class MaxThreadsConstraint implements WMUpdateListener {
	private final class ProxyEntry extends WorkAdapter {

		public final WorkAdapter getNextEntry() {
			return null;
		}

		public final boolean readyToRun() {
			int i = getCount();
			return (i <= 0 || inProgress < i) && queue.size() > 0;
		}

		public final WorkAdapter getEffective() {
			return getNext();
		}

		// public final boolean hasEffective(){
		// return queue.size() > 0;
		// }
		public final void run() {
		}

		public ProxyEntry() {
			super();
			requestClass = new FairShareRequestClass(name + "_proxy");
		}
	}

	protected MaxThreadsConstraint(String s) {
		this(s, -1);
	}

	public MaxThreadsConstraint(String s, int i) {
		queue = new UnsyncCircularQueue();
		name = s;
		count = i;
		proxy = new ProxyEntry();
	}

	public final int getCount() {
		return count;
	}

	public final void setCount(int i) {
		count = i;
	}

	public final String getName() {
		return name;
	}

	public final boolean readyToRun(WorkAdapter workadapter) {
		int i = getCount();
		if (DebugWM.debug_MaxThread == 1) {
			log(this.getName() + "********inProgress = " + inProgress
					+ "********MaxCount = " + i);
		}

		if (i <= 0 || inProgress < i) {
			return true;
		} else {
			if (DebugWM.debug_MaxThread == 1) {
				System.out.println("******** put to queue");
			}
			queue.put(workadapter);
			return false;
		}
	}

	void add(WorkAdapter workadapter) {
		queue.put(workadapter);
	}

	boolean isConstraintReached() {
		int i = getCount();
		return i > 0 && inProgress >= i;
	}

	final WorkAdapter getProxy() {
		return queue.empty() ? null : proxy;
	}

	// set wm in proxy by syk
	final WorkAdapter getProxy(WorkAdapter wa) {
		if (queue.empty())
			return null;
		if (proxy.wm == null)
			proxy.setWorkManager(wa.getWorkManager());
		return proxy;
	}

	// end
	private WorkAdapter getNext() {
		while (!queue.empty()) {
			WorkAdapter workadapter = (WorkAdapter) queue.get();
			// maxqueue中，会存放min.proxy;只有当minqueu非空，才返回存放的min.proxy;否则移除存放的min.proxy
			// if (!workadapter.started) by syk
			if (!workadapter.started && workadapter.hasEffective())
				return workadapter;
		}
		return null;
	}

	public final int getQueueSize() {
		return queue.size();
	}

	public final int getExecutingCount() {
		return inProgress;
	}

	private static void log(String s) {
		System.out.println("<MaxThreadConstraint>" + s);
	}

	// by syk
	public void updatePerformed(WMUpdateEvent e) {
		Object proposed = e.getProposed();
		int i = count;
		if (proposed instanceof MaxThreadsConstraint) {
			count = ((MaxThreadsConstraint) proposed).getCount();
		}
		if (i < count) {
			RequestManager.getInstance().executeImmediately(getCanRunList());
		}
	}

	// end

	WorkAdapter[] getCanRunList() {
		RequestManager requestmanager;
		requestmanager = RequestManager.getInstance();
		long l = System.currentTimeMillis();
		ArrayList arraylist = new ArrayList();

		synchronized (requestmanager) {
			int i;
			i = getCanRunCount();
			if (i == 0)
				return null;
			int j = 0;
			do {
				if (j >= i)
					break;
				WorkAdapter workadapter = getNext();
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

	private int getCanRunCount() {
		if (queue.empty() || inProgress >= count)
			return 0;
		else
			return Math.min(queue.size(), count - inProgress);
	}

	private void increaseCount(int i) {
		int j = count;
		count = i;
		if (count > j)
			RequestManager.getInstance().executeImmediately(getCanRunList());
	}

	// private static final AuthenticatedSubject kernelId =
	// (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
	private final String name;

	private int count;

	protected int inProgress;

	private final UnsyncCircularQueue queue;

	private final WorkAdapter proxy;
}
