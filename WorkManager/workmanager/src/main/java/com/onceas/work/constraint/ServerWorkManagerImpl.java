package com.onceas.work.constraint;

import java.io.PrintWriter;

import com.onceas.health.HealthMonitorMService;
import com.onceas.health.LowMemoryNotification;
import com.onceas.health.MemoryEvent;
import com.onceas.health.MemoryListener;
import com.onceas.work.DebugWM;
import com.onceas.work.Work;
import com.onceas.work.WorkAdapter;
import com.onceas.work.WorkManagerConstant;
import com.onceas.work.WorkManagerFactory;
import com.onceas.work.WorkManagerImageSource;
import com.onceas.work.WorkManagerImpl;
import com.onceas.work.WorkManagerLogger;
import com.onceas.work.WorkManagerService;
import com.onceas.work.WorkManagerServiceImpl;
import com.onceas.work.threadpool.ExecuteThread;

public final class ServerWorkManagerImpl extends WorkManagerImpl {
	private static final class WorkAdapterImpl extends WorkAdapter {

		public void run() {
			runnable.run();
		}

		public Runnable overloadAction(String s) {
			if (runnable instanceof Work)
				return ((Work) runnable).overloadAction(s);
			else
				return null;
		}

		public Runnable cancel(String s) {
			if (runnable instanceof Work)
				return ((Work) runnable).cancel(s);
			else
				return null;
		}

		/**
		 * protected final AuthenticatedSubject getAuthenticatedSubject() {
		 * if(runnable instanceof WorkAdapter) return
		 * ((WorkAdapter)runnable).getAuthenticatedSubject(); else return null; }
		 */
		private final Runnable runnable;

		private WorkAdapterImpl(Runnable runnable1) {
			runnable = runnable1;
		}

	}

	public static final class LowMemoryListener implements MemoryListener {

		public boolean lowMemory() {
			return lowMemory;
		}

		private static boolean lowMemory;

		private LowMemoryListener() {
			LowMemoryNotification.addMemoryListener(this);
		}

		public void memoryChanged(MemoryEvent memoryevent) {
			if (memoryevent == null) {
				return;
			}
			if (memoryevent.getEventType() == 1) {
				lowMemory = true;
				return;
			}
			if (memoryevent.getEventType() == 0) {
				lowMemory = false;
			}
		}

	}

	public static OverloadManager SHARED_OVERLOAD_MANAGER;

	public static LowMemoryListener LOW_MEMORY_LISTENER;

	// private final ServiceClassSupport requestClass;
	// protected final MaxThreadsConstraint max;
	// protected final MinThreadsConstraint min;
	// private final OverloadManager overload;
	// private final StuckThreadManager stuckThreadManager;
	// --- modified by syk in order to produce setters to add workmanager
	// component in runtime
	private ServiceClassSupport requestClass;

	protected MaxThreadsConstraint max;

	protected MinThreadsConstraint min;

	private OverloadManager overload;

	private StuckThreadManager stuckThreadManager;

	// private static final AuthenticatedSubject kernelId =
	// (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
	private WorkManagerServiceImpl workManagerService;

	private long acceptedCount;

	private long startedCount;

	private long completedCount;

	private int queueLength;

	public static void initialize() {
		SHARED_OVERLOAD_MANAGER = new OverloadManager("global overload manager");
		SHARED_OVERLOAD_MANAGER.setCapacity(65536);
		// SHARED_OVERLOAD_MANAGER.setCapacity(ManagementService.getRuntimeAccess(kernelId).getServer().getOverloadProtection().getSharedCapacityForWorkManagers());
		LOW_MEMORY_LISTENER = new LowMemoryListener();
	}

	public static void initialize(int i) {
		SHARED_OVERLOAD_MANAGER = new OverloadManager("global overload manager");
		SHARED_OVERLOAD_MANAGER.setCapacity(i);
		LOW_MEMORY_LISTENER = new LowMemoryListener();
	}

	public ServerWorkManagerImpl(String s, String s1, String s2,
			RequestClass requestclass,
			MaxThreadsConstraint maxthreadsconstraint,
			MinThreadsConstraint minthreadsconstraint,
			OverloadManager overloadmanager,
			StuckThreadManager stuckthreadmanager) {
		wmName = s == null ? null : s.intern();
		applicationName = s1;
		moduleName = s2;
		if (wmName != WorkManagerConstant.ONCEAS_KERNEL_DEFAULT
				&& applicationName == null)
			setInternal();
		if (requestclass == null)
			requestClass = new FairShareRequestClass(s, s1, s2);
		else
			requestClass = (ServiceClassSupport) requestclass;
		if (isInternal())
			requestClass.setInternal(true);
		max = maxthreadsconstraint;
		min = minthreadsconstraint;
		if (minthreadsconstraint != null)
			register(minthreadsconstraint);
		overload = overloadmanager;
		stuckThreadManager = stuckthreadmanager;
		WorkManagerImageSource.getInstance().register(this);
	}

	public void register(MinThreadsConstraint minthreadsconstraint) {
		RequestManager.getInstance().register(minthreadsconstraint);
	}

	public int getType() {
		return 1;
	}

	public int getConfiguredThreadCount() {
		return -1;
	}

	public void schedule(Runnable runnable) {
		try {
			if (WorkManagerConstant.ONCEAS_DIRECT == wmName) {
				runnable.run();
				return;
			}
		} catch (RuntimeException runtimeexception) {
			WorkManagerLogger.logScheduleFailed(wmName, runtimeexception);
			throw runtimeexception;
		} catch (OutOfMemoryError outofmemoryerror) {
			WorkManagerLogger.logScheduleFailed(wmName, outofmemoryerror);
			notifyOOME(outofmemoryerror);
			throw outofmemoryerror;
		} catch (Error error) {
			WorkManagerLogger.logScheduleFailed(wmName, error);
			throw error;
		}
		if (accept(runnable)) {
			WorkAdapter workadapter = getWorkAdapter(runnable);
			// RequestManager.getInstance().executeIt(workadapter);
			RequestManager rm = RequestManager.getInstance();
			rm.executeIt(workadapter);
		}
	}

	public boolean executeIfIdle(Runnable runnable) {
		if (WorkManagerConstant.ONCEAS_DIRECT == wmName) {
			runnable.run();
			return true;
		}
		if (accept(runnable))
			return RequestManager.getInstance().executeIfIdle(
					getWorkAdapter(runnable));
		else
			return false;
	}

	public boolean scheduleIfBusy(Runnable runnable) {
		if (RequestManager.getInstance().getIdleThreadCount() == 0
				&& RequestManager.getInstance().getQueueDepth() > 0) {
			schedule(runnable);
			return true;
		}
		if (Thread.currentThread() instanceof ExecuteThread) {
			ExecuteThread executethread = (ExecuteThread) Thread
					.currentThread();
			if (executethread.getWorkManager() instanceof ServerWorkManagerImpl) {
				ServerWorkManagerImpl serverworkmanagerimpl = executethread
						.getWorkManager();
				long l = RequestManager.updateRequestClass(
						(ServiceClassStatsSupport) serverworkmanagerimpl
								.getRequestClass(), executethread);
				executethread.timeStamp = l;
			}
		}
		return false;
	}

	private WorkAdapter getWorkAdapter(Runnable runnable) {
		// Object obj;
		// if(runnable instanceof WorkAdapter)
		// {// ??
		// obj = (WorkAdapter)runnable;
		// if(!((WorkAdapter) (obj)).setScheduled())
		// obj = new WorkAdapterImpl(((Runnable) (obj)));
		// } else
		// {
		// obj = new WorkAdapterImpl(runnable);
		// }
		// ((WorkAdapter) (obj)).setWorkManager(this);
		// return ((WorkAdapter) (obj));

		// by syk 20071205
		WorkAdapter wa;
		if (runnable instanceof WorkAdapter) {
			wa = (WorkAdapter) runnable;
			if (!wa.setScheduled())
				wa = new WorkAdapterImpl(runnable);
		} else {
			wa = new WorkAdapterImpl(runnable);
		}
		wa.setWorkManager(this);
		// debug by syk
		// printWMInfo(runnable);
		// debug end
		return wa;
	}

	// for debug by syk
	private void printWMInfo(Runnable runnable) {
		String appName = "/WorkManager1";
		if (this.applicationName != null
				&& this.applicationName.equals(appName)) {
			String name = null;
			int count = 0;

			System.out.println();
			System.out
					.println("**********************************************************************");
			System.out.println("����������[ " + this + " ]����work����ʱ�� ��ϸ������Ϣ: ");
			System.out.println("��1��������Ϣ�� ");
			System.out.println("=======���ȵġ�����[runnable]�ǣ� " + runnable);
			System.out.println("=======��������������[wmName]���ǣ� " + this.wmName);
			System.out.println("=======�����ġ�Ӧ�����ơ�[applicationName]�ǣ� "
					+ this.applicationName);
			System.out.println("=======�����ġ�module����[moduleName]���ǣ� "
					+ this.moduleName);

			System.out.println();

			System.out.println("��2�����������Ϣ�� ");
			System.out
					.println("2.1����߳���Լ��[maxthreadsconstraint]�ǣ� " + this.max);
			if (this.max != null) {
				name = this.max.getName();
				count = this.max.getCount();
				System.out
						.println("===========����߳���Լ��[maxthreadsconstraint]�������ǣ� "
								+ name);
				System.out
						.println("===========����߳���Լ��[maxthreadsconstraint]������߳����ǣ� "
								+ count);
			}
			System.out
					.println("2.2��С�߳���Լ��[minthreadsconstraint]�ǣ� " + this.min);
			if (this.min != null) {
				name = this.min.getName();
				count = this.min.getCount();
				System.out
						.println("===========��С�߳���Լ��[minthreadsconstraint]�������ǣ� "
								+ name);
				System.out
						.println("===========��С�߳���Լ��[minthreadsconstraint]����С�߳����ǣ� "
								+ count);
			}

			System.out.println("2.3���������Լ��[capacity]�ǣ� " + this.overload);
			if (this.overload != null) {
				name = this.overload.getName();
				count = this.overload.getCount();
				System.out.println("===========���������Լ��[capacity]�������ǣ� " + name);
				System.out.println("===========���������Լ��[capacity]������������ǣ� "
						+ count);
			}
			if (this.getRequestClass() instanceof FairShareRequestClass) {
				System.out.println("2.4[Fair Share Request Class]�ǣ� "
						+ this.requestClass);
				if (this.requestClass != null) {
					name = this.requestClass.getName();
					count = ((FairShareRequestClass) this.requestClass)
							.getFairShare();
					System.out
							.println("===========[Fair Share Request Class]�������ǣ� "
									+ name);
					System.out
							.println("===========[Fair Share Request Class]��ӵ�е�share�ǣ� "
									+ count);
				}
			}
			System.out.println();
			System.out.println("��3������������ͳ����Ϣ�� ");
			System.out.println("=======����������[startedCount]�ǣ� "
					+ this.startedCount);
			System.out.println("=======���ܵ�����[acceptedCount]�ǣ� "
					+ this.acceptedCount);
			System.out.println("=======��ɵ�����[completedCount]�ǣ� "
					+ this.completedCount);
			System.out.println("=======�ȴ�������[queueLength]�ǣ� "
					+ this.queueLength);

			System.out
					.println("**********************************************************************");
			System.out.println();
		}
	}

	// end debug
	public int getQueueDepth() {
		return queueLength;
	}

	public boolean isThreadOwner(Thread thread) {
		if (!(thread instanceof ExecuteThread)) {
			return false;
		} else {
			ExecuteThread executethread = (ExecuteThread) thread;
			return this == executethread.getWorkManager();
		}
	}

	public static void notifyOOME(OutOfMemoryError outofmemoryerror) {
		HealthMonitorMService.panic(outofmemoryerror);
	}

	private boolean accept(Runnable runnable) {
		if (isInternal())
			return true;
		if (!(runnable instanceof Work))
			return true;
		String s = null;
		if (LOW_MEMORY_LISTENER.lowMemory()) {
			s = getLowMemoryMessage();
			log("Low Memory now, so it will be reject! ");
		} else {
			OverloadManager overloadmanager = getRejectingOverloadManager();
			if (overloadmanager != null)
				s = getOverloadMessage(overloadmanager);
		}
		if (s == null)
			return true;
		Runnable runnable1 = ((Work) runnable).overloadAction(s);
		if (runnable1 == null || isAdminWork(runnable)) {
			return true;
		} else {
			if (DebugWM.debug_Overload) {
				log("the current overloadmanager is " + s + " in WorkManager "
						+ this + "  ,so it will be rejected!");
			}
			if (DebugWM.debug_LowMemory) {
				log("the current WorkManager  " + s + "  ,so "
						+ runnable.toString() + " will be rejected!");
			}
			RequestManager.getInstance().rejectedCount++;
			WorkManagerFactory.getInstance().getRejector().schedule(runnable1);
			return false;
		}
	}

	private OverloadManager getRejectingOverloadManager() {
		if (overload != null && !overload.accept())
			return overload;
		if (SHARED_OVERLOAD_MANAGER.accept())
			return null;
		if (min != null && !min.isConstraintSatisfied())
			return null;
		if (requestClass.getPendingRequestsCount() < SHARED_OVERLOAD_MANAGER
				.getCapacity()
				&& RequestManager.getInstance()
						.acceptRequestClass(requestClass))
			return null;
		else
			return SHARED_OVERLOAD_MANAGER;
	}

	private static final boolean isAdminWork(Runnable runnable) {
		if (!(runnable instanceof WorkAdapter))
			return false;
		return false;
		// TODO
		/**
		 * AuthenticatedSubject authenticatedsubject =
		 * ((WorkAdapter)runnable).getAuthenticatedSubject();
		 * if(authenticatedsubject == null) return false; else return
		 * SubjectUtils.doesUserHaveAnyAdminRoles(authenticatedsubject);
		 */
	}

	public static String getOverloadMessage(OverloadManager overloadmanager) {
		return "OverloadManager: " + overloadmanager.getName() + ", length: "
				+ overloadmanager.getLength() + "; Capacity: "
				+ overloadmanager.getCapacity();
		// return
		// WorkManagerLogger.logOverloadActionLoggable(overloadmanager.getName(),
		// overloadmanager.getLength(),
		// overloadmanager.getCapacity()).getMessage();
	}

	public String getCancelMessage() {
		return wmName + " in " + applicationName + "Canceled After Enqueue";
		// return WorkManagerLogger.logCancelAfterEnqueueLoggable(wmName,
		// applicationName).getMessage();
	}

	public final String getLowMemoryMessage() {
		return wmName + "LowMemory";
		// return WorkManagerLogger.logLowMemoryLoggable(wmName).getMessage();
	}

	public void setWorkManagerService(
			WorkManagerServiceImpl workmanagerserviceimpl) {
		workManagerService = workmanagerserviceimpl;
	}

	public WorkManagerService getWorkManagerService() {
		return workManagerService;
	}

	public boolean isShutdown() {
		return workManagerService == null ? false : workManagerService
				.isShutdown();
	}

	final void accepted() {
		acceptedCount++;
		queueLength++;
		if (!isInternal()) {
			SHARED_OVERLOAD_MANAGER.workCount++;
			SHARED_OVERLOAD_MANAGER.queueDepth++;
		}
		if (overload != null) {
			overload.workCount++;
			overload.queueDepth++;
		}
		requestClass.pendingRequestCount++;
		if (workManagerService != null)
			workManagerService.workAccepted();
	}

	public final void started() {
		startedCount++;
		queueLength--;
		if (min != null)
			min.inProgress++;
		if (max != null)
			max.inProgress++;
		if (!isInternal())
			SHARED_OVERLOAD_MANAGER.queueDepth--;
		if (overload != null)
			overload.queueDepth--;
		if (workManagerService != null)
			workManagerService.workStarted();
	}

	final void stuck() {
		if (workManagerService != null)
			workManagerService.workStuck();
	}

	final void completed() {
		completedCount++;
		if (!isInternal())
			SHARED_OVERLOAD_MANAGER.workCount--;
		if (overload != null)
			overload.workCount--;
		requestClass.pendingRequestCount--;
		if (min != null) {
			min.inProgress--;
			min.totalCompletedCount++;
		}
		if (max != null)
			max.inProgress--;
		if (workManagerService != null)
			workManagerService.workCompleted();
	}

	final void canceled() {
		RequestManager.getInstance().canceledCount++;
		if (!isInternal())
			SHARED_OVERLOAD_MANAGER.workCount--;
		if (overload != null)
			overload.workCount--;
		requestClass.pendingRequestCount--;
		if (min != null) {
			min.inProgress--;
			// min.totalCompletedCount++;
		}
		if (max != null)
			max.inProgress--;
		if (workManagerService != null)
			workManagerService.workCompleted();
	}

	public final long getAcceptedCount() {
		return acceptedCount;
	}

	public final long getStartedCount() {
		return startedCount;
	}

	public final long getCompletedCount() {
		return completedCount;
	}

	public final OverloadManager getOverloadManager() {
		return overload;
	}

	public final RequestClass getRequestClass() {
		return requestClass;
	}

	public final MaxThreadsConstraint getMaxThreadsConstraint() {
		return max;
	}

	public final MinThreadsConstraint getMinThreadsConstraint() {
		return min;
	}

	public final StuckThreadManager getStuckThreadManager() {
		return stuckThreadManager;
	}

	public void cleanup() {
		if (!requestClass.isShared())
			requestClass.cleanup();
		WorkManagerImageSource.getInstance().deregister(this);
	}

	public void dumpInformation(PrintWriter printwriter) {
		if (printwriter == null) {
			return;
		} else {
			printwriter.println("--- WorkManager " + wmName + " for app "
					+ applicationName + ", module " + moduleName + " ---");
			printwriter.println("Requests accepted       : " + acceptedCount);
			printwriter.println("Requests started        : " + startedCount);
			printwriter.println("Requests Completed      : " + completedCount);
			return;
		}
	}

	public static void log(String s) {
		System.out.println("<ServerWorkManagerImpl>" + s);
	}

	// ---------------------------------------------------
	// add setters for creating workmanager components through WorkManagerConfig
	// :by syk

	/**
	 * @param max
	 *            the max to set
	 */
	public void setMaxThreadsConstraint(MaxThreadsConstraint max) {
		this.max = max;
	}

	/**
	 * @param min
	 *            the min to set
	 */
	public void setMinThreadsConstraint(MinThreadsConstraint min) {
		this.min = min;
	}

	/**
	 * @param overload
	 *            the overload to set
	 */
	public void setOverloadManager(OverloadManager overload) {
		this.overload = overload;
	}

	/**
	 * @param requestClass
	 *            the requestClass to set
	 */
	public void setRequestClass(ServiceClassSupport requestClass) {
		this.requestClass = requestClass;
	}

	/**
	 * @param stuckThreadManager
	 *            the stuckThreadManager to set
	 */
	public void setStuckThreadManager(StuckThreadManager stuckThreadManager) {
		this.stuckThreadManager = stuckThreadManager;
	}

	// syk

}
