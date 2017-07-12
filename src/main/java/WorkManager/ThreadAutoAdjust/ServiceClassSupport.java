package WorkManager.ThreadAutoAdjust;

public abstract class ServiceClassSupport extends ServiceClassStatsSupport
		implements RequestClass, Comparable {

	public ServiceClassSupport(String s) {
		super(s);
		threadPriority = 5;
	}

//	public void cleanup() {
//		RequestManager.getInstance().deregister(this);
//	}

	public ServiceClassSupport(String s, long l, long l1) {
		this(s);
		vtDeltaFirst = l;
		vtDeltaRepeat = l1;
	}

	public final long getVirtualTimeIncrement(long l) {
		long l1 = myLast - l;
		if (l1 < 0L) {
			myLast = l + vtDeltaFirst;
			return vtDeltaFirst;
		} else {
			myLast += vtDeltaRepeat;
			vtIncrement = l1 + vtDeltaRepeat;
			return vtIncrement;
		}
	}

	public final void queueEmptied(long l, float f) {
		myLast = 0L;
	}

	protected final synchronized void setIncrements(long l, long l1) {
		vtDeltaFirst = l;
		vtDeltaRepeat = l1;
	}

	public final long getDeltaFirst() {
		return vtDeltaFirst;
	}

	public final long getDelta() {
		return vtDeltaRepeat;
	}

	protected long getIncrementForThreadPriorityCalculation() {
		return vtDeltaRepeat;
	}

	public final int compareTo(Object obj) {
		ServiceClassSupport serviceclasssupport = (ServiceClassSupport) obj;
		return (int) (vtDeltaRepeat - serviceclasssupport.vtDeltaRepeat);
	}

	public final int getPendingRequestsCount() {
		return pendingRequestCount;
	}

	public final long getMyLast() {
		return myLast;
	}

	public final long getVirtualTimeIncrement() {
		return vtIncrement;
	}

	void setThreadPriority(int i) {
		if (threadPriority == i) {
			return;
		} else {
			threadPriority = i;
			return;
		}
	}

	public int getThreadPriority() {
		return threadPriority;
	}

	public boolean isInternal() {
		return internal;
	}

	void setInternal(boolean flag) {
		internal = flag;
	}

	void setShared(boolean flag) {
		isShared = flag;
	}

	boolean isShared() {
		return isShared;
	}

	private long vtDeltaFirst;

	private long vtDeltaRepeat;

	private long myLast;

	int pendingRequestCount;

	private long vtIncrement;

	private int threadPriority;

	private boolean internal;

	private boolean isShared;
}
