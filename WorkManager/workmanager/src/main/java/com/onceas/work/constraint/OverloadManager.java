package com.onceas.work.constraint;

import com.onceas.work.management.notification.WMUpdateEvent;
import com.onceas.work.management.notification.WMUpdateListener;


public class OverloadManager implements WMUpdateListener {
	OverloadManager(String s) {
		workCount = 0;
		queueDepth = 0;
		name = s;
	}

	public OverloadManager(String s, int i) {
		workCount = 0;
		queueDepth = 0;
		name = s;
		capacity = i;
	}

	public final int getCapacity() {
		return capacity;
	}

	public final synchronized void setCapacity(int i) {
		capacity = i;
	}

	public boolean accept() {
		int i = getCapacity();
		if (i < 0)
			return true;
		return workCount < i;
	}

	public String getName() {
		return name;
	}

	public int getLength() {
		return workCount;
	}

	public int getQueueDepth() {
		return queueDepth;
	}

	// by syk
	public int getCount() {
		return capacity;
	}

	public void updatePerformed(WMUpdateEvent e) {
		Object proposed = e.getProposed();
		if (proposed instanceof OverloadManager) {
			capacity = (((OverloadManager) proposed).getCount());
			// return;
		}
		// return;
	}

	// end

	static final String SHARED_OVERLOAD_MANAGER_NAME = "global overload manager";

	protected int workCount;

	protected int queueDepth;

	private String name;

	private int capacity;
}
