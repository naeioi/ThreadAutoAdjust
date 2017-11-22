package workmanager;

public interface RequestClass {

	String getName();

	int getThreadPriority();

	boolean isInternal();

	void queueEmptied(long l, float f);

	void workCompleted(long l, long l1);

	void timeElapsed(long l, ServiceClassesStats serviceclassesstats);

	long getVirtualTimeIncrement(long l);
}
