package workmanager;

public class WorkAdapter implements Work {
	public long creationTimeStamp;
	public long startedTimeStamp;
	public boolean started;
	private boolean scheduled;
	public FairShareRequestClass requestClass;
	private Runnable task;

	public WorkAdapter() {
		creationTimeStamp = System.currentTimeMillis();
		this.requestClass=RequestManager.requestClass;
	}
	
	public WorkAdapter(Runnable task_){
		this();
		task = task_;
	}

	public void prepareForReuse() {
		started = false;
		scheduled = false;
		creationTimeStamp = -1L;
		startedTimeStamp = -1L;
	}

	public Runnable overloadAction(String s) {
		return null;
	}

	public Runnable cancel(String s) {
		return null;
	}

	public boolean isAdminChannelRequest() {
		return false;
	}

	public boolean isTransactional() {
		return false;
	}

	public final boolean setScheduled() {
		if (scheduled)
			return false;
		synchronized (this) {
			if (scheduled)
				return false;
			scheduled = true;
			if (creationTimeStamp <= 0L)
				creationTimeStamp = System.currentTimeMillis();
			return true;
		}
	}

	final boolean isStarted() {
		return started;
	}

	public WorkAdapter getEffective() {
		return this;
	}

	// to check whether effective workmanager exists: add by syk
	public boolean hasEffective() {
		return true;
	}

	public void run() {
		if(task != null) {
			startedTimeStamp = System.currentTimeMillis();
			task.run();
		}
	}
}
