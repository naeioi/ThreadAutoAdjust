package com.onceas.health;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.onceas.systemplatform.GCListener;
import com.onceas.systemplatform.GarbageCollectionEvent;
import com.onceas.systemplatform.VM;

public final class LowMemoryNotification implements GCListener {

	private static final MemoryEvent LOW_MEMORY_EVENT = new MemoryEvent(1);

	private static final MemoryEvent OK_MEMORY_EVENT = new MemoryEvent(0);

	private static final Queue<MemoryListener> queue = new ConcurrentLinkedQueue<MemoryListener>();

	private static boolean initialized;

	private static int lowThreshold;

	private static int highThreshold;

	private boolean lowMemoryThresholdReached;

	public LowMemoryNotification(int i, int j) {
		lowThreshold = i;
		highThreshold = j;
	}

	public static synchronized void initialize(int i, int j) {
		if (initialized) {
			return;
		} else {
			VM.getVM().addGCListener(new LowMemoryNotification(i, j));
			initialized = true;
			return;
		}
	}

	public static void addMemoryListener(MemoryListener memorylistener) {
		queue.add(memorylistener);
	}

	public static void removeMemoryListener(MemoryListener memorylistener) {
		queue.remove(memorylistener);
	}

	private static final synchronized void sendMemoryEvent(
			MemoryEvent memoryevent) {
		for (MemoryListener memorylistener : queue) {
			memorylistener.memoryChanged(memoryevent);
		}
	}

	public void onGarbageCollection(
			GarbageCollectionEvent garbagecollectionevent) {
		int i;
		if (garbagecollectionevent == null)
			return;
		long l = Runtime.getRuntime().freeMemory();
		long l1 = Runtime.getRuntime().totalMemory();
		i = (int) ((l * 100L) / l1);
		synchronized (this) {
			if (i >= lowThreshold || garbagecollectionevent.getEventType() != 0) {
				lowMemoryThresholdReached = false;
				if (i > highThreshold) {
					sendMemoryEvent(OK_MEMORY_EVENT);
				}
				return;
			} else {
				if (!lowMemoryThresholdReached) {
					lowMemoryThresholdReached = true;
					sendMemoryEvent(LOW_MEMORY_EVENT);
					return;
				}
			}
		}
	}

	public static void setHighThreshold(int highThreshold) {
		LowMemoryNotification.highThreshold = highThreshold;
	}

	public static void setLowThreshold(int lowThreshold) {
		LowMemoryNotification.lowThreshold = lowThreshold;
	}
}
