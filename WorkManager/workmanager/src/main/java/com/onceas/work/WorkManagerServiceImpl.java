package com.onceas.work;

import com.onceas.util.collection.StackTraceUtils;
import com.onceas.work.constraint.MaxThreadsConstraint;
import com.onceas.work.constraint.MinThreadsConstraint;
import com.onceas.work.constraint.OverloadManager;
import com.onceas.work.constraint.RequestClass;
import com.onceas.work.constraint.ServerWorkManagerImpl;
import com.onceas.work.constraint.ShutdownCallback;
import com.onceas.work.constraint.StuckThreadManager;

public final class WorkManagerServiceImpl implements WorkManagerService {
	public String getName() {
		return _flddelegate.getName();
	}

	public String getApplicationName() {
		return _flddelegate.getApplicationName();
	}

	public String getModuleName() {
		return _flddelegate.getModuleName();
	}

	public int getType() {
		return _flddelegate.getType();
	}

	public int getConfiguredThreadCount() {
		return _flddelegate.getConfiguredThreadCount();
	}

	public static WorkManagerService createService(String s, String s1,
			String s2) {
		return createService(s, s1, s2, null);
	}

	public static WorkManagerService createService(String s, String s1,
			String s2, StuckThreadManager stuckthreadmanager) {
		WorkManager workmanager = ServerWorkManagerFactory.create(s, s1, s2,
				stuckthreadmanager);
		WorkManagerServiceImpl workmanagerserviceimpl = new WorkManagerServiceImpl(
				workmanager);
		((ServerWorkManagerImpl) workmanager)
				.setWorkManagerService(workmanagerserviceimpl);
		return workmanagerserviceimpl;
	}

	/**
	 * public static WorkManagerService createService(String s, String s1,
	 * WorkManagerMBean workmanagermbean, StuckThreadManager stuckthreadmanager) {
	 * WorkManagerFactory workmanagerfactory = WorkManagerFactory
	 * .getInstance();
	 * 
	 * ServerWorkManagerImpl serverworkmanagerimpl = (ServerWorkManagerImpl)
	 * ServerWorkManagerFactory .create(s, s1, workmanagermbean,
	 * stuckthreadmanager); WorkManagerServiceImpl workmanagerserviceimpl = new
	 * WorkManagerServiceImpl( serverworkmanagerimpl);
	 * serverworkmanagerimpl.setWorkManagerService(workmanagerserviceimpl);
	 * return workmanagerserviceimpl; }
	 */
	public static WorkManagerService createService(String s, String s1,
			String s2, RequestClass requestclass,
			MaxThreadsConstraint maxthreadsconstraint,
			MinThreadsConstraint minthreadsconstraint,
			OverloadManager overloadmanager,
			StuckThreadManager stuckthreadmanager) {
		ServerWorkManagerImpl serverworkmanagerimpl = (ServerWorkManagerImpl) ServerWorkManagerFactory
				.create(s, s1, s2, requestclass, maxthreadsconstraint,
						minthreadsconstraint, overloadmanager,
						stuckthreadmanager);
		WorkManagerServiceImpl workmanagerserviceimpl = new WorkManagerServiceImpl(
				serverworkmanagerimpl);
		serverworkmanagerimpl.setWorkManagerService(workmanagerserviceimpl);
		return workmanagerserviceimpl;
	}

	private WorkManagerServiceImpl(WorkManager workmanager) {
		// ??
		// /state = SHUTDOWN;
		state = RUNNING;
		allowRMIWork = false;
		_flddelegate = workmanager;
		if (debugEnabled())
			debug("-- wmservice created - " + this);
	}

	public synchronized int getState() {
		return state;
	}

	public synchronized void start() {
		state = RUNNING;
		if (debugEnabled())
			debug("-- wmservice - " + this + " started");
	}

	public boolean isShutdown() {
		if (debugEnabled())
			debug("-- wmservice - " + this + " is shutdown : "
					+ (state == SHUTDOWN));
		return state == SHUTDOWN;
	}

	public synchronized void shutdown(ShutdownCallback shutdowncallback) {
		if (internal || state == SHUTDOWN || state == WAIT_FOR_PENDING_TX) {
			if (shutdowncallback != null)
				shutdowncallback.completed();
			return;
		}
		if (debugEnabled())
			debug("-- wmservice - " + this + " shutdown with callback "
					+ shutdowncallback + "\nstack trace:\n"
					+ StackTraceUtils.throwable2StackTrace(null));
		callback = shutdowncallback;
		if (waitForPendingTransactions()) {
			if (debugEnabled())
				debug("-- wmservice - "
						+ this
						+ " is waiting for pending txn to complete before shutdown");
			state = WAIT_FOR_PENDING_TX;
			return;
		}
		if (allowRMIWork)
			return;
		if (debugEnabled())
			debug("-- wmservice - " + this
					+ " has no pending txn. commencing shutdown ...");
		state = SHUTDOWN;
		if (workPending())
			return;
		if (debugEnabled())
			debug("-- wmservice - "
					+ this
					+ " has no pending work and no pending txn. Invoking callback");
		if (shutdowncallback != null)
			shutdowncallback.completed();
		shutdowncallback = null;
	}

	public void forceShutdown() {
		if (debugEnabled())
			debug("-- wmservice - " + this + " force shutdown with callback "
					+ callback + "\nstack trace:\n"
					+ StackTraceUtils.throwable2StackTrace(null));
		state = SHUTDOWN;
	}

	private boolean waitForPendingTransactions() {
		return false;
		/**
		 * ( int i =
		 * ManagementService.getRuntimeAccess(kernelId).getServerRuntime().getStateVal();
		 * if(i != 2 || ClientInitiatedTxShutdownService.isTxMapEmpty()) return
		 * false; if(debugEnabled()) debug("-- wmservice - " + this + " has
		 * pending txn and will timeout in " +
		 * ClientInitiatedTxShutdownService.getTxTimeoutMillis() + "ms");
		 * TimerManagerFactory.getTimerManagerFactory().getDefaultTimerManager().schedule(new
		 * TxEmptyChecker(ClientInitiatedTxShutdownService.getTxTimeoutMillis(),
		 * 2000), 0L, 2000L); return true;
		 */
	}

	public WorkManager getDelegate() {
		return _flddelegate;
	}

	public void cleanup() {
		if (_flddelegate instanceof ServerWorkManagerImpl)
			((ServerWorkManagerImpl) _flddelegate).cleanup();
	}

	public void schedule(Runnable runnable) {
		if (permitSchedule(runnable)) {
			_flddelegate.schedule(runnable);
		} else {
			Debug
					.assertion(runnable instanceof Work,
							"Only work instances can be submitted to WorkManagerService");
			Runnable runnable1 = ((Work) runnable).cancel(getCancelMessage());
			Debug.assertion(runnable1 != null, "cancel task cannot be null");
			WorkManagerFactory.getInstance().getRejector().schedule(runnable1);
		}
	}

	String getCancelMessage() {
		return getName() + " in " + getApplicationName() + "Canceled";
		// return WorkManagerLogger.logCancelBeforeEnqueueLoggable(getName(),
		// getApplicationName()).getMessage();
	}

	public boolean executeIfIdle(Runnable runnable) {
		if (permitSchedule(runnable))
			return _flddelegate.executeIfIdle(runnable);
		else
			return false;
	}

	public boolean scheduleIfBusy(Runnable runnable) {
		return false;
	}

	public int getQueueDepth() {
		return workCount;
	}

	public void setInternal() {
		internal = true;
		_flddelegate.setInternal();
		if (debugEnabled())
			debug("-- wmservice - " + this + " marked internal");
	}

	public boolean isInternal() {
		return internal;
	}

	public boolean isThreadOwner(Thread thread) {
		return false;
	}

	private boolean permitSchedule(Runnable runnable) {
		if (internal)
			return true;
		boolean flag = false;
		synchronized (this) {
			if (state == RUNNING || !(runnable instanceof Work)
					|| allowTransactionalWork(runnable)) {
				workCount++;
				flag = true;
			}
		}
		if (allowRMIWork) {
			rmiManager.preScheduleWork();
			return true;
		}
		if (flag)
			return true;
		if (debugEnabled())
			debug("-- wmservice - " + this + " is shutdown");
		if (!isAdminUser(runnable) && !isAdminChannelRequest(runnable))
			return false;
		synchronized (this) {
			workCount++;
			return true;
		}
	}

	private boolean allowTransactionalWork(Runnable runnable) {
		if (state == WAIT_FOR_PENDING_TX && (runnable instanceof WorkAdapter)) {
			boolean flag = ((WorkAdapter) runnable).isTransactional();
			if (debugEnabled())
				debug("-- wmservice - "
						+ this
						+ " is waiting for pending txn and current work has txn:"
						+ flag);
			return flag;
		} else {
			return false;
		}
	}

	private boolean isAdminChannelRequest(Runnable runnable) {
		if (!(runnable instanceof WorkAdapter))
			return false;
		if (((WorkAdapter) runnable).isAdminChannelRequest()) {
			if (debugEnabled())
				debug("-- wmservice - " + this
						+ " is shutdown but accepted work from admin channel");
			return true;
		} else {
			return false;
		}
	}

	private boolean isAdminUser(Runnable runnable) {
		if (!(runnable instanceof WorkAdapter))
			return false;
		return true;
		// TODO
		/**
		 * AuthenticatedSubject authenticatedsubject =
		 * ((WorkAdapter)runnable).getAuthenticatedSubject();
		 * if(authenticatedsubject != null &&
		 * SubjectUtils.doesUserHaveAnyAdminRoles(authenticatedsubject)) {
		 * if(debugEnabled()) debug("-- wmservice - " + this + " is shutdown but
		 * accepted work from " + authenticatedsubject); return true; } else {
		 * return false; }
		 */
	}

	public void workAccepted() {
	}

	public void workStarted() {
	}

	public void workStuck() {
		if (internal)
			return;
		synchronized (this) {
			stuckThreadCount++;
			if (state != SHUTDOWN)
				return;
		}
		boolean flag = workPending();
		if (!flag)
			invokeCallback();

		/**
		 * label0: { if(internal) return; synchronized(this) {
		 * stuckThreadCount++; if(state == 3) break label0; } return; } boolean
		 * flag = workPending(); workmanagerserviceimpl; JVM INSTR monitorexit ;
		 * goto _L1 exception; throw exception; _L1: if(!flag) invokeCallback();
		 * return;
		 */
	}

	private void invokeCallback() {
		if (callback == null)
			return;
		ShutdownCallback shutdowncallback;
		synchronized (this) {
			if (callback == null)
				return;
			shutdowncallback = callback;
		}
		if (debugEnabled())
			debug("-- wmservice - " + this
					+ " has no pending work. Invoking callback");
		shutdowncallback.completed();

		/**
		 * if(callback == null) return; ShutdownCallback shutdowncallback; try {
		 * label0: { synchronized(this) { if(callback != null) break label0; }
		 * return; } } catch(Throwable throwable) {
		 * WorkManagerLogger.logShutdownCallbackFailed(throwable); } goto _L1
		 * shutdowncallback = callback; callback = null; workmanagerserviceimpl;
		 * JVM INSTR monitorexit ; goto _L2 exception; throw exception; _L2:
		 * if(debugEnabled()) debug("-- wmservice - " + this + " has no pending
		 * work. Invoking callback"); shutdowncallback.completed(); break
		 * MISSING_BLOCK_LABEL_90; _L1:
		 */
	}

	// TODO RMI?
	private boolean workPending() {
		return workCount - stuckThreadCount > 0 || allowRMIWork;
	}

	public void workCompleted() {

		if (internal)
			return;
		synchronized (this) {
			workCount--;
			if (state != SHUTDOWN)
				return;
		}
		boolean flag = workPending();
		if (flag) {
			if (debugEnabled())
				debug("-- wmservice - " + this
						+ " is shutdown and waiting for " + workCount
						+ " to finish");
		} else {
			invokeCallback();
		}
		/**
		 * label0: { if(internal) return; synchronized(this) { workCount--;
		 * if(state == 3) break label0; } return; } boolean flag =
		 * workPending(); workmanagerserviceimpl; JVM INSTR monitorexit ; goto
		 * _L1 exception; throw exception; _L1: if(flag) { if(debugEnabled())
		 * debug("-- wmservice - " + this + " is shutdown and waiting for " +
		 * workCount + " to finish"); } else { invokeCallback(); } return;
		 */
	}

	public void startRMIGracePeriod(WorkListener worklistener) {
		rmiManager = worklistener;
		if (worklistener != null)
			allowRMIWork = true;
	}

	public void endRMIGracePeriod() {
		boolean flag = false;
		synchronized (this) {
			allowRMIWork = false;
			if (state != WAIT_FOR_PENDING_TX)
				state = SHUTDOWN;
			flag = workPending();
		}
		if (!flag)
			invokeCallback();
		rmiManager = null;
	}

	private void notifyTransactionCompletion() {
		boolean flag = false;
		synchronized (this) {
			if (!allowRMIWork)
				state = SHUTDOWN;
			flag = workPending();
		}
		if (!flag)
			invokeCallback();
	}

	public void setThreadCount(int i) throws IllegalStateException,
			SecurityException {
		throw new IllegalStateException("WorkManager [" + toString()
				+ "] does not support setting thread count");
	}

	public void setModuleName(String moduleName) {
		_flddelegate.setModuleName(moduleName);
		
	}
	public String toString() {
		return super.toString() + "[" + getName() + ", " + getApplicationName()
				+ ", " + getModuleName() + "]";
	}

	private static boolean debugEnabled() {
		// return debugWMService.isEnabled();
		return DebugWM.debug_WMService;
	}

	private static void debug(String s) {
		WorkManagerLogger.logDebug(s);
	}

	private static void log(String s) {
		System.out.println("<WorkManagerServiceImpl>" + s);
	}

	private static final int PENDING_TX_TIMER_INTERVAL = 2000;

	private int state;

	private int workCount;

	private final WorkManager _flddelegate;

	private ShutdownCallback callback;

	private boolean internal;

	private int stuckThreadCount;

	private WorkListener rmiManager;

	private boolean allowRMIWork;



}
