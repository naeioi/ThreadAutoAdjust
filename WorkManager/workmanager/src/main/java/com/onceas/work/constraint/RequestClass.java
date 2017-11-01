package com.onceas.work.constraint;

public interface RequestClass {

	public abstract String getName();

	public abstract int getThreadPriority();

	public abstract boolean isInternal();

	public abstract void queueEmptied(long l, float f);

	public abstract void workCompleted(long l, long l1);

	public abstract void timeElapsed(long l,
                                     ServiceClassesStats serviceclassesstats);

	public abstract long getVirtualTimeIncrement(long l);

	public abstract int getPendingRequestsCount();

	public abstract void cleanup();
}
