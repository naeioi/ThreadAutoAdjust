package com.onceas.work;

import com.onceas.work.constraint.ContextRequestClass;
import com.onceas.work.constraint.MaxThreadsConstraint;
import com.onceas.work.constraint.MinThreadsConstraint;
import com.onceas.work.constraint.RequestClass;
import com.onceas.work.constraint.ServerWorkManagerImpl;

public abstract class WorkAdapter implements Work {
	public WorkAdapter() {
		creationTimeStamp = System.currentTimeMillis();
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

	public final void setWorkManager(ServerWorkManagerImpl serverworkmanagerimpl) {
		if (serverworkmanagerimpl == null)
			return;
		wm = serverworkmanagerimpl;
		requestClass = serverworkmanagerimpl.getRequestClass();

		if (serverworkmanagerimpl.getRequestClass() instanceof ContextRequestClass)
			requestClass = ((ContextRequestClass) serverworkmanagerimpl
					.getRequestClass()).getEffective();

	}

	public final ServerWorkManagerImpl getWorkManager() {
		return wm;
	}

	public final MinThreadsConstraint getMinThreadsConstraint() {
		return wm.getMinThreadsConstraint();
	}

	public final MaxThreadsConstraint getMaxThreadsConstraint() {
		return wm.getMaxThreadsConstraint();
	}

	final boolean isStarted() {
		return started;
	}

	public final Runnable getWork() {
		if (wm.isShutdown()) {
			Runnable runnable = cancel(wm.getCancelMessage());
			if (runnable != null)
				return runnable;
		}
		return this;
	}

	public boolean readyToRun() {
		MaxThreadsConstraint maxthreadsconstraint = wm
				.getMaxThreadsConstraint();
		return maxthreadsconstraint == null
				|| maxthreadsconstraint.readyToRun(this);
	}

	public WorkAdapter getEffective() {
		return this;
	}

	// to check whether effective work exists: add by syk
	public boolean hasEffective() {
		return true;
	}

	// end
	/**
	 * protected AuthenticatedSubject getAuthenticatedSubject() { return
	 * subject; }
	 */
	public final String dump() {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("work: " + this + "\n");
		stringbuffer.append("workmanager: " + getWorkManager() + "\n");
		stringbuffer.append("scheduled=" + scheduled + ", started=" + started
				+ "\n");
		stringbuffer.append("wait time: "
				+ (System.currentTimeMillis() - creationTimeStamp) + "\n");
		if (wm != null)
			stringbuffer.append("min constraint: " + getMinThreadsConstraint());
		return stringbuffer.toString();
	}

	public ServerWorkManagerImpl wm;

	public RequestClass requestClass;

	public long creationTimeStamp;

	public long startedTimeStamp;

	public boolean started;

	private boolean scheduled;
}
