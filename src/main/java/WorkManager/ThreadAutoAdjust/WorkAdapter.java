package WorkManager.ThreadAutoAdjust;

import java.util.Random;

public class WorkAdapter implements Work {
	public WorkAdapter() {
		creationTimeStamp = System.currentTimeMillis();
		this.requestClass=RequestManager.requestClass;
	}
	
	public WorkAdapter(Runnable task){
		this();
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

	// to check whether effective work exists: add by syk
	public boolean hasEffective() {
		return true;
	}

	// end

	public long creationTimeStamp;

	public long startedTimeStamp;

	public boolean started;

	private boolean scheduled;
	
	public FairShareRequestClass requestClass;

	@Override
	public void run() {
		Random Random=new Random();
		long l=System.currentTimeMillis();
		int index=(int)(Random.nextFloat()*1000000);
		for(int i=0;i<index;i++){
			Math.pow(Random.nextDouble(),Random.nextDouble());
		}
		System.out.println(System.currentTimeMillis()-l);
		
	}
}
