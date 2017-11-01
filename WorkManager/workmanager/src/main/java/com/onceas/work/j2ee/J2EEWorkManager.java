package com.onceas.work.j2ee;

/**
 * <p>Title: Work Manager的实现</p>
 *
 * <p>Description: 基于OnceAS平台</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 中国科学院软件研究所</p>
 *
 * @author 张磊
 * @version 1.0
 *
 * 说明：真正实现commonj中WorkManager中四个接口。其中J2EEWorkManager实现了WorkManager接口，WorkStatus
 * 实现了WorkEvent和WorkItem接口，WorkWithListener通过扩展WorkAdapter实现了work接口，callback类中实现了waitforall（）
 * 和waitforany（）方法，具体为什么这样搞，不大清楚
 *
 */
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.onceas.util.application.ApplicationAccess;
import com.onceas.util.application.ApplicationContextInternal;
import com.onceas.work.DebugWM;
import com.onceas.work.InheritableThreadContext;
import com.onceas.work.WorkAdapter;
import com.onceas.work.WorkManagerConstant;
import com.onceas.work.WorkManagerFactory;
import com.onceas.work.j2ee.remote.RemoteMsgDispacher;
import com.onceas.work.j2ee.remote.RemoteWorkAdapter;
import com.onceas.work.j2ee.remote.RemoteWorkManagerConstant;
import com.onceas.work.util.DefaultWorkManagerUtil;

import commonj.work.RemoteWorkItem;
import commonj.work.Work;
import commonj.work.WorkCompletedException;
import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;
import commonj.work.WorkRejectedException;

public final class J2EEWorkManager implements WorkManager

{
	protected static class ScheduleWorkRejectedException extends Exception {

		public long getWorkId() {
			return m_lWorkId;
		}

		private long m_lWorkId;

		protected ScheduleWorkRejectedException(long lWorkId, Throwable eReason) {
			super(eReason);
			m_lWorkId = lWorkId;
		}

		protected ScheduleWorkRejectedException(long lWorkId, String sMsg) {
			super(sMsg);
			m_lWorkId = lWorkId;
		}
	}

	protected static class RemoteWorkStatusInfo implements Serializable {

		public long getWorkId() {
			return m_lWorkId;
		}

		public int getStatus() {
			return m_nStatus;
		}

		public Object getData() {
			return m_oFeedback;
		}

		public String toString() {
			return "WorkStatus{Id=" + m_lWorkId + ", Status="
					+ formatStatus(m_nStatus) + ", Data=" + m_oFeedback + "}";
		}

		public static String formatStatus(int nStatus) {
			switch (nStatus) {
			case 1: // '\001'
				return "WORK_ACCEPTED";

			case 2: // '\002'
				return "WORK_REJECTED";

			case 3: // '\003'
				return "WORK_STARTED";

			case 4: // '\004'
				return "WORK_COMPLETED";
			}
			return "Unknown status: " + nStatus;
		}

		protected static final int WORK_UNKNOWN = 0;

		public static final int WORK_ACCEPTED = 1;

		public static final int WORK_REJECTED = 2;

		public static final int WORK_STARTED = 3;

		public static final int WORK_COMPLETED = 4;

		private long m_lWorkId;

		private int m_nStatus;

		private Object m_oFeedback;

		public RemoteWorkStatusInfo() {
		}

		public RemoteWorkStatusInfo(long lWorkId, int nStatus, Object oFeedback) {
			m_lWorkId = lWorkId;
			m_nStatus = nStatus;
			m_oFeedback = oFeedback;
		}
	}

	protected static class SendFeedback extends RemoteWorkAdapter {
		private RemoteWorkStatusInfo m_statusInfo;

		public SendFeedback() {
		}

		public SendFeedback(RemoteWorkStatusInfo statusInfo) {
			// super(status.getWorkId(), 0);
			m_statusInfo = statusInfo;
		}

		public SendFeedback(RemoteWorkStatusInfo statusInfo, String appName) {
			// super(status.getWorkId(), 0);
			m_statusInfo = statusInfo;
			this.applicationName = appName;
		}

		public void run() {
			J2EEWorkManager j2eeWorkManager = (J2EEWorkManager) J2EEWorkManager
					.get(null, null, getWorkManager());
			j2eeWorkManager.processFeedback(this);
		}

		public RemoteWorkStatusInfo getWorkStatusInfo() {
			return m_statusInfo;
		}
	}

	protected static class Response extends RemoteWorkAdapter {
		private RemoteWorkStatusInfo m_statusInfo;

		public Response() {
		}

		public Response(String appName) {
			this.applicationName = appName;
		}

		public void run() {
			J2EEWorkManager j2eeWorkManager = (J2EEWorkManager) J2EEWorkManager
					.get(null, null, getWorkManager());
			j2eeWorkManager.processResponse(this);
		}

		public RemoteWorkStatusInfo getWorkStatusInfo() {
			return m_statusInfo;
		}

		public void setWorkStatusInfo(RemoteWorkStatusInfo info) {
			m_statusInfo = info;
		}

	}

	protected static class ReleaseWork extends RemoteWorkAdapter {
		private RemoteWorkStatusInfo m_statusInfo;

		public ReleaseWork() {
		}

		public ReleaseWork(long lWorkId, String nodeFrom) {
			super(lWorkId, nodeFrom);
		}

		public ReleaseWork(long lWorkId, String nodeFrom, String appName) {
			super(lWorkId, nodeFrom);
			this.applicationName = appName;
		}

		public void run() {
			J2EEWorkManager j2eeWorkManager = (J2EEWorkManager) J2EEWorkManager
					.get(null, null, getWorkManager());
			j2eeWorkManager.processReleaseWork(this);
		}

		public RemoteWorkStatusInfo getWorkStatusInfo() {
			return m_statusInfo;
		}

		public void setWorkStatusInfo(RemoteWorkStatusInfo info) {
			m_statusInfo = info;
		}

	}

	protected class RemoteWorkManager implements WorkManager {

		protected String gridNodePinned;

		public RemoteWorkManager(String gridNode) {
			gridNodePinned = gridNode;
		}

		public WorkItem schedule(Work work) throws WorkException,
				IllegalArgumentException {
			return schedule(work, null, gridNodePinned);
		}

		public WorkItem schedule(Work work, WorkListener workListener)
				throws WorkException, IllegalArgumentException {
			return schedule(work, workListener, gridNodePinned);
		}

		protected WorkItem schedule(Work work, WorkListener workListener,
				String gridNodePinned) throws WorkException {
			// TODO 判断该节点是否还存在
			if (!isNodeAlive(gridNodePinned))
				throw new WorkRejectedException("Remote JVM terminated");
			else
				return J2EEWorkManager.this.remoteSchedule(work, workListener,
						gridNodePinned);
		}

		public boolean waitForAll(Collection collWorkItems, long lTimeoutMillis)
				throws InterruptedException, IllegalArgumentException {
			return J2EEWorkManager.this.waitForAll(collWorkItems,
					lTimeoutMillis);
		}

		public Collection waitForAny(Collection collWorkItems,
				long lTimeoutMillis) throws InterruptedException,
				IllegalArgumentException {
			return J2EEWorkManager.this.waitForAny(collWorkItems,
					lTimeoutMillis);
		}
	}

	// TODO
	/**
	 * protected class WorkObserver implements RemoteInvocationObserver {
	 * 
	 * public void nodeCompleted(String node, Object oResult) {
	 * updateWork((WorkStatus) oResult); }
	 * 
	 * public void nodeFailed(String node, Throwable eFailure) { if (eFailure
	 * instanceof RemoteWorkRejectedException) { updateWork(new
	 * WorkStatus(eRejected.getWorkId(), 2, new
	 * WorkRejectedException(eRejected))); } else { //Unexpected processing
	 * failure, Removing the member from the active server list
	 * disableNode(node); } }
	 * 
	 * public void nodeLeft(String node) { }
	 * 
	 * public void remoteInvocationCompleted() { } }
	 */

	private static class WorkStatus implements WorkEvent, WorkItem {

		private static synchronized long getCounter() {
			return globalCount++;
		}

		final synchronized void setType(int i) {
			type = i;
			if (isCompleted() && callbacks != null && callbacks.size() > 0) {
				for (Iterator iterator = callbacks.iterator(); iterator
						.hasNext(); ((Callback) iterator.next())
						.completed(this))
					;
				callbacks = null;
			}
		}

		public final int getType() {
			return type;
		}

		public final WorkItem getWorkItem() {
			return this;
		}

		public final Work getWork() {
			return work;
		}

		final void setThrowable(Throwable throwable) {
			workException = new WorkException(throwable);
		}

		public final WorkException getException() {
			return workException;
		}

		public final int getStatus() {
			return type;
		}

		public void release() throws WorkException {
			if (isCompleted()) {
				throw new WorkException(
						"release called on already completed work");
			} else {
				work.release();
				return;
			}
		}

		public Work getResult() throws WorkException {
			if (workException != null)
				throw workException;
			if (isCompleted())
				return work;
			else
				return null;
		}

		public final synchronized void register(Callback callback) {
			if (isCompleted()) {
				callback.completed(this);
			} else {
				if (callbacks == null)
					callbacks = new ArrayList();
				callbacks.add(callback);
			}
		}

		protected synchronized boolean isCompleted() {
			return type == WORK_REJECTED || type == WORK_COMPLETED;
		}

		public String toString() {
			return "[" + counter + "] executing: " + work;
		}

		/**
		 * public int hashCode() { return (int)(counter ^ VM_DIFFERENTIATOR); }
		 */
		public int compareTo(Object obj) {
			try {
				return compare((WorkStatus) obj);
			} catch (ClassCastException classcastexception) {
				return -1;
			}
		}

		public int compare(WorkStatus workstatus) {
			if (counter > workstatus.counter)
				return 1;
			if (counter < workstatus.counter)
				return -1;
			if (serverIdentity > workstatus.serverIdentity)
				return 1;
			return serverIdentity >= workstatus.serverIdentity ? 0 : -1;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof WorkStatus)) {
				return false;
			} else {
				WorkStatus workstatus = (WorkStatus) obj;
				return counter == workstatus.counter
						&& serverIdentity == workstatus.serverIdentity;
			}
		}

		public long getWorkID() {
			return this.counter;
		}

		public static final int WORK_ACCEPTED = 1;

		public static final int WORK_REJECTED = 2;

		public static final int WORK_STARTED = 3;

		public static final int WORK_COMPLETED = 4;

		private static long globalCount;

		private int type;

		protected Work work;

		protected WorkException workException;

		private ArrayList callbacks;

		private long counter;

		private long serverIdentity;

		private WorkStatus(Work work1) {
			type = 1;
			work = work1;
			counter = getCounter();
		}

	}

	private class RemoteWorkStatus extends WorkStatus implements RemoteWorkItem {
		private String targetNode;

		private RemoteWorkStatusInfo remoteWorkStatusInfo;

		private WorkListener listener;

		private String appName;

		private RemoteWorkStatus(WorkListener listener, Work work,
				String targetNode) {
			super(work);
			this.targetNode = targetNode;
			this.listener = listener;
		}

		public RemoteWorkStatus(WorkListener workListener, Work work,
				String targetNode2, String appName) {
			super(work);
			this.targetNode = targetNode;
			this.listener = listener;
			this.appName = appName;
		}

		public final void release() {
			ReleaseWork releaseWork = new ReleaseWork(getWorkID(),
					J2EEWorkManager.getLocalNode(), appName);
			RemoteMsgDispacher.sendWork(releaseWork, targetNode,
					RemoteWorkManagerConstant.REMOTE_WORK_RELEASE_QUEUE);
			// ReleaseWorkDispacher.getRemoteWorkDispacher().sendWork(task,
			// targetNode);
		}

		public WorkManager getPinnedWorkManager() {
			return new RemoteWorkManager(targetNode);
		}

		public RemoteWorkStatusInfo getRemoteWorkStatusInfo() {
			return remoteWorkStatusInfo;
		}

		public void setRemoteWorkStatusInfo(
				RemoteWorkStatusInfo remoteWorkStatusInfo) {
			this.remoteWorkStatusInfo = remoteWorkStatusInfo;
			this.setType(remoteWorkStatusInfo.getStatus());
			if (this.isCompleted()) {
				work = (Work) remoteWorkStatusInfo.getData();
			}
			WorkListener listener = getListener();
			if (listener != null) {
				try {
					switch (getStatus()) {
					case 1: // '\001'
						listener.workAccepted(this);
						break;

					case 2: // '\002'
						listener.workRejected(this);
						break;

					case 3: // '\003'
						listener.workStarted(this);
						break;

					case 4: // '\004'
						listener.workCompleted(this);
						break;
					}
				} catch (RuntimeException e) {
					logger.warning("Exception during event dispatch:\n"
							+ getStackTrace(e));
				}
			}
		}

		public String getTargetNode() {
			return targetNode;
		}

		public void setTargetNode(String targetNode) {
			this.targetNode = targetNode;
		}

		public WorkListener getListener() {
			return listener;
		}

		public void setListener(WorkListener listener) {
			this.listener = listener;
		}
	}

	private static final class WorkWithListener extends WorkAdapter {
		private final Work work;

		private final WorkListener listener;

		private final WorkStatus status;

		private final InheritableThreadContext inheritableThreadContext;

		private WorkWithListener(Work work1, WorkListener worklistener,
				WorkStatus workstatus,
				InheritableThreadContext inheritablethreadcontext) {
			work = work1;
			listener = worklistener;
			status = workstatus;
			inheritableThreadContext = inheritablethreadcontext;
			// workAreaContext =
			// WorkContextHelper.getWorkContextHelper().getInterceptor().copyThreadContexts(2);
		}

		/*
		 * protected AuthenticatedSubject getAuthenticatedSubject() {
		 * weblogic.security.subject.AbstractSubject abstractsubject =
		 * context.getSubject(); if(abstractsubject instanceof
		 * AuthenticatedSubject) return (AuthenticatedSubject)abstractsubject;
		 * else return null; }
		 * 
		 * private boolean isAdminRequest() { AuthenticatedSubject
		 * authenticatedsubject = getAuthenticatedSubject(); return
		 * authenticatedsubject != null &&
		 * SubjectUtils.doesUserHaveAnyAdminRoles(authenticatedsubject); }
		 */
		public Runnable overloadAction(final String reason) {
			// if(isAdminRequest())
			// return null;
			// else
			return new Runnable() {

				public void run() {
					status.setType(WorkStatus.WORK_REJECTED);
					status.setThrowable(new WorkRejectedException(reason));
					try {
						if (listener != null)
							listener.workRejected(status);
					} catch (Throwable throwable) {
					}
				}
			};
		}

		public Runnable cancel(String s) {
			return overloadAction(s);
		}

		public void run() {
			if (inheritableThreadContext != null)
				inheritableThreadContext.push();
			status.setType(WorkStatus.WORK_STARTED);
			try {
				if (listener != null)
					listener.workStarted(status);
			} catch (Throwable throwable) {
			}
			try {
				work.run();
			} catch (Throwable throwable1) {
				status.setThrowable(throwable1);
			}
			status.setType(WorkStatus.WORK_COMPLETED);
			try {
				if (listener != null)
					listener.workCompleted(status);
			} catch (Throwable throwable2) {
			}
			if (inheritableThreadContext != null)
				inheritableThreadContext.pop();
		}
	}

	private static final class RemoteWork extends RemoteWorkAdapter {
		private final Work work;

		private final boolean isListened;

		private transient int m_nStatus;

		private Object m_oResult;

		public RemoteWork(Work work1, long lWorkId, String nodeFrom,
				boolean isListened1) {
			super(lWorkId, nodeFrom);
			work = work1;
			isListened = isListened1;
		}

		public RemoteWork(Work work1, long lWorkId, String nodeFrom,
				String wmName, String appName, String moduleName,
				boolean isListened1) {
			super(lWorkId, nodeFrom, wmName, appName, moduleName);
			work = work1;
			isListened = isListened1;
		}

		public Work getRemoteWork() {
			return work;
		}

		public Object getResult() {
			return m_oResult;
		}

		protected void setResult(Object oResult) {
			m_oResult = oResult;
		}

		public Work getWork() {
			return work;
		}

		/**
		 * public Runnable overloadAction(final String reason) { return new
		 * Runnable() { public void run() {
		 * status.setType(WorkStatus.WORK_REJECTED); status.setThrowable(new
		 * WorkRejectedException(reason)); try { if (listener != null)
		 * listener.workRejected(status); } catch (Throwable throwable) { } } }; }
		 */
		public Runnable cancel(String s) {
			return overloadAction(s);
		}

		public int getStatus() {
			return m_nStatus;
		}

		public void setStatus(int nStatus) {
			m_nStatus = nStatus;
		}

		/**
		 * TODO classloader context
		 */
		public void run() {
			J2EEWorkManager j2eeWorkManager = (J2EEWorkManager) J2EEWorkManager
					.get(this.applicationName, this.moduleName,
							getWorkManager());
			Response response = j2eeWorkManager.processRemoteWork(this);
			RemoteMsgDispacher.sendWork(response, this.getNodeFrom(),
					RemoteWorkManagerConstant.REMOTE_WORK_RESPONSE_QUEUE);
			// RemoteWorkResponseDispacher.getRemoteWorkDispacher().sendWork(response,
			// this.getNodeFrom());
		}
	}

	/**
	 * 
	 * <p>
	 * Title: Work Manager的实现
	 * </p>
	 * 
	 * <p>
	 * Description: 基于OnceAS平台
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2006
	 * </p>
	 * 
	 * <p>
	 * Company: 中国科学院软件研究所
	 * </p>
	 * 
	 * @author 张磊
	 * @version 1.0
	 * 
	 * 
	 */
	private static final class Callback {
		int count;

		boolean notifyWaitForAll;

		boolean notifyWaitForAny;

		ArrayList completedItems;

		private Callback() {
			if (DebugWM.debug_entrypoint == 1) {
				System.out.println("Construct Callback");
			}
			completedItems = new ArrayList();
		}

		boolean waitForAll(long l) throws InterruptedException {
			if (DebugWM.debug_entrypoint == 1) {
				System.out.println("Callback:   boolean waitForAll(long l)");
			}
			if (count == 0)
				return true;
			synchronized (this) {
				if (count == 0)
					return true;
				notifyWaitForAll = true;
				wait(l);
				completedItems = null;
				if (count != 0)
					return true;
				else
					return false;
			}
		}

		/**
		 * 当一个WorkItem完成时的notify（）策略
		 * 
		 * @param workstatus
		 *            WorkStatus
		 */
		synchronized void completed(WorkStatus workstatus) {
			if (DebugWM.debug_entrypoint == 1) {
				System.out
						.println("Callback:   void completed(WorkStatus workstatus)");
			}
			count--;
			if (completedItems != null)
				completedItems.add(workstatus);
			if (notifyWaitForAll && count == 0)
				notify();
			if (notifyWaitForAny)
				notify();
		}

		/**
		 * waitforAny
		 * 
		 * @param l
		 *            long
		 * @return Collection
		 * @throws InterruptedException
		 */

		synchronized Collection waitForAny(long l) throws InterruptedException {
			if (DebugWM.debug_entrypoint == 1) {
				System.out.println("Callback:  Collection waitForAny(long l)");
			}
			if (count == 0 || completedItems.size() > 0) {
				return completedItems;
			} else {
				notifyWaitForAny = true;
				wait(l);
				notifyWaitForAny = false;
				ArrayList arraylist = completedItems;
				completedItems = null;
				return arraylist;
			}
		}

		/**
		 * 将workstatus（一个WorkItem）注册到ArrayList
		 * 
		 * @param workstatus
		 *            WorkStatus
		 */
		synchronized void add(WorkStatus workstatus) {
			count++;
			workstatus.register(this);
		}
	}

	private static final J2EEWorkManager DEFAULT = new J2EEWorkManager(
			WorkManagerFactory.getInstance().getDefault());

	private static final J2EEWorkManager DIRECT = new J2EEWorkManager(
			WorkManagerFactory.getInstance().find(
					WorkManagerConstant.ONCEAS_DIRECT));

	private static Map contextInfoMap = new ConcurrentHashMap();

	private static Map m_mapPostedWork = new ConcurrentHashMap();

	private static Map m_mapAcceptedWork = new ConcurrentHashMap();

	private final com.onceas.work.WorkManager workManager;

	private Logger logger = Logger.getLogger(getClass().getName());

	private J2EEWorkManager(com.onceas.work.WorkManager workmanager) {
		this.workManager = workmanager;
	}

	public static WorkManager getDefault() {
		if (DebugWM.debug_entrypoint == 1) {
			System.out.println("J2EEWorkManager    WorkManager getDefault() ");
		}
		return DEFAULT;
	}

	public static WorkManager getDefault(String s) {
		if (DebugWM.debug_entrypoint == 1) {
			System.out
					.println("J2EEWorkManager    WorkManager getDefault(String s) ");
		}
		ApplicationContextInternal applicationcontextinternal = ApplicationAccess
				.getApplicationAccess().getApplicationContext(s);
		if (applicationcontextinternal == null)
			return null;
		else
			return new J2EEWorkManager(applicationcontextinternal
					.getWorkManagerCollection().getDefault());
	}

	/**
	 * 
	 * @param s
	 *            appname
	 * @param s1
	 *            module name
	 * @return
	 */
	public static WorkManager getModuleWorkManager(String s, String s1) {
		ApplicationContextInternal applicationcontextinternal = ApplicationAccess
				.getApplicationAccess().getApplicationContext(s);
		com.onceas.work.WorkManager wm = null;
		if (applicationcontextinternal != null) {
			if (s1 != null) {
				wm = applicationcontextinternal.getWorkManagerCollection()
						.getModuleWorkManager(s1);
			} else {
				wm = applicationcontextinternal.getWorkManagerCollection()
						.getAppWorkManager();
			}
			if (wm != null) {
				return new J2EEWorkManager(wm);
			}
		}
		return DEFAULT;
	}

	/**
	 * 
	 * @param s
	 *            appname
	 * @param s1
	 *            module name
	 * @return
	 */
	public static com.onceas.work.WorkManager getModuleOnceASWorkManager(
			String s, String s1) {
		return getModuleOnceASWorkManager(s,s1,true);
	}
/**
 * 
 * @param s
 * @param s1
 * @param singleton  true 共享同一个缺省WorkManager，false 每（应用，模块）对应一个缺省WorkManager
 * @return
 */
	public static com.onceas.work.WorkManager getModuleOnceASWorkManager(String s, String s1, boolean singleton) {
		ApplicationContextInternal applicationcontextinternal = ApplicationAccess
				.getApplicationAccess().getApplicationContext(s);
		com.onceas.work.WorkManager wm = null;
		if (applicationcontextinternal != null) {
			if (s1 != null) {
				wm = applicationcontextinternal.getWorkManagerCollection()
						.getModuleWorkManager(s1);
			}
			if (wm != null) {
				return wm;
			}
			wm = applicationcontextinternal.getWorkManagerCollection()
					.getAppWorkManager();
			if (wm != null) {
				return wm;
			}
		}
		
		if(singleton){
			return WorkManagerFactory.getInstance().getDefault();
		}else{
			return DefaultWorkManagerUtil.createDefaultWorkManager(s, s1);
		}
	}

	/**
	 * 
	 * @param s
	 *            appname
	 * @param s1
	 *            module name
	 * @param s2
	 *            wmname
	 * @return
	 */
	public static WorkManager get(String s, String s1, String s2) {
		if (DebugWM.debug_entrypoint == 1) {
			System.out
					.println("J2EEWorkManager      WorkManager get(String s, String s1, String s2) ");
		}
		ApplicationContextInternal applicationcontextinternal = ApplicationAccess
				.getApplicationAccess().getApplicationContext(s);

		if (applicationcontextinternal != null) {
			com.onceas.work.WorkManager wm = applicationcontextinternal
					.getWorkManagerCollection().get(s1, s2);
			// TODO should return WorkManagerConstant.ONCEAS_KERNEL_DEFAULT with
			// a
			// StuckThreadManager
			if (wm != null) {
				return new J2EEWorkManager(wm);
			}
		}

		if (s2 != null && s2.equals(WorkManagerConstant.ONCEAS_DIRECT)) {
			return DIRECT;
		}

		return DEFAULT;
	}

	public WorkItem schedule(Work work) {
		return schedule(work, null);
	}

	public WorkItem schedule(Work work, WorkListener workListener) {
		if (work == null)
			throw new IllegalArgumentException("null work instance");

		if (!(work instanceof Serializable)) {
			return localSchedule(work, workListener);
		} else {
			return remoteSchedule(work, workListener, chooseNode());
		}
	}

	public boolean waitForAll(Collection collection, long l)
			throws InterruptedException {
		if (collection == null || collection.size() == 0)
			return true;
		if (0L == l)
			return createCallback(collection).waitForAll(1L);
		else
			return createCallback(collection).waitForAll(l);
	}

	public Collection waitForAny(Collection collection, long l)
			throws InterruptedException {
		if (DebugWM.debug_entrypoint == 1) {
			System.out
					.println("J2EEWorkManager:waitForAny(Collection collection, long l)");
		}
		if (collection == null || collection.size() == 0)
			return Collections.EMPTY_LIST;
		if (0L == l)
			return createCallback(collection).waitForAny(1L);
		else
			return createCallback(collection).waitForAny(l);
	}

	private static Callback createCallback(Collection collection) {
		Object aobj[] = collection.toArray();
		Callback callback = new Callback();
		for (int i = 0; i < aobj.length; i++)
			if (aobj[i] instanceof WorkStatus) {
				WorkStatus workstatus = (WorkStatus) aobj[i];
				callback.add(workstatus);
			}

		return callback;
	}

	private WorkItem localSchedule(Work work, WorkListener workListener) {
		WorkStatus workstatus = new WorkStatus(work);
		if (workListener != null)
			workListener.workAccepted(workstatus);
		workManager.schedule(new WorkWithListener(work, workListener,
				workstatus, InheritableThreadContext.getContext()));
		return workstatus;
	}

	public WorkItem remoteSchedule(Work work, WorkListener workListener,
			String targetNode) {
		System.out.println("remote Schedule");

		// TODO 判断是否为集群版本
		if (!isClusterVersion()) {
			return localSchedule(work, workListener);
		} else {
			RemoteWorkStatus remoteWorkstatus = new RemoteWorkStatus(
					workListener, work, targetNode, workManager
							.getApplicationName());

			getPostedWork().put(remoteWorkstatus.getWorkID(), remoteWorkstatus);

			remoteWorkstatus.setRemoteWorkStatusInfo(new RemoteWorkStatusInfo(
					remoteWorkstatus.getWorkID(), 1, null));

			if (workListener != null)
				workListener.workAccepted(remoteWorkstatus);

			// 将Work包装到一个RemoteWork对象中，然后分发给相应节点
			RemoteWork remoteWork = new RemoteWork(work, remoteWorkstatus
					.getWorkID(), getLocalNode(), workManager.getName(),
					workManager.getApplicationName(), workManager
							.getModuleName(), workListener != null);
			RemoteMsgDispacher.sendWork(remoteWork, targetNode,
					RemoteWorkManagerConstant.REMOTE_WORK_QUEUE);

			return remoteWorkstatus;
		}

	}

	public void scheduleRemoteWork(RemoteWorkAdapter work) {
		workManager.schedule(work);
	}

	private String chooseNode() {
		return "133.133.133.30";
	}

	private boolean isClusterVersion() {
		return true;
	}

	public boolean isNodeAlive(String node) {
		return true;
	}

	public void disableNode(String node) {
		// TODO 关闭节点服务，更新仍被该节点所执行的状态
		// getServers().remove(member);
		do
			try {
				Iterator iter = getPostedWork().values().iterator();
				do {
					if (!iter.hasNext())
						break;
					RemoteWorkStatus remoteWorkStatus = (RemoteWorkStatus) iter
							.next();
					if (remoteWorkStatus.getTargetNode().equals(node)) {
						RemoteWorkStatusInfo status = new RemoteWorkStatusInfo(
								remoteWorkStatus.getWorkID(), 4,
								new WorkCompletedException("Server terminated"));
						updateWork(status);
					}
				} while (true);
				break;
			} catch (ConcurrentModificationException e) {
			}
		while (true);
	}

	public Response processRemoteWork(RemoteWork remoteWork) {
		Map mapWork = null;
		Exception exception = null;
		RemoteWorkStatusInfo remoteWorkStatusInfo = null;

		Response response = new Response(remoteWork.getApplicationName());
		response.setNodeFrom(this.getLocalNode());

		Map mapAccepted = getAcceptedWork();
		String nodeFrom = remoteWork.getNodeFrom();
		Long IdWork = new Long(remoteWork.getWorkId());
		if (remoteWork.getNodeFrom() != null) {
			mapWork = (Map) mapAccepted.get(nodeFrom);
			if (mapWork != null) {
				Object oWork = mapWork.get(IdWork);
				if (oWork == null) {
					exception = new ScheduleWorkRejectedException(IdWork,
							"Work has been removed");
					response.setExcepion(exception);
					return response;
				}

			}
		}

		InheritableThreadContext contextInfo = J2EEWorkManager
				.getConctextInfo(remoteWork.getApplicationName());
		if (contextInfo != null)
			contextInfo.push();

		synchronized (remoteWork) {
			if (remoteWork.getStatus() == 2) {
				if (mapWork != null) {
					exception = new ScheduleWorkRejectedException(IdWork,
							"Work has been released");
					response.setExcepion(exception);
					return response;
				}
			}
			remoteWork.setStatus(3);
		}

		synchronized (remoteWork) {
			remoteWork.setStatus(3);
			if (mapWork != null)
				mapWork.remove(IdWork);
		}

		if (nodeFrom != null && remoteWork.isListened) {
			SendFeedback feedback = new SendFeedback(new RemoteWorkStatusInfo(
					remoteWork.getWorkId(), 3, null), remoteWork
					.getApplicationName());
			RemoteMsgDispacher.sendWork(feedback, nodeFrom,
					RemoteWorkManagerConstant.REMOTE_WORK_FEEDBACK_QUEUE);
			// RemoteFeedbackDispacher.getRemoteWorkDispacher().sendWork(feedback,
			// nodeFrom);
		}

		try {
			remoteWork.getWork().run();
		} catch (Throwable throwable1) {
			remoteWorkStatusInfo = new RemoteWorkStatusInfo(IdWork, 4,
					throwable1);
			response.setWorkStatusInfo(remoteWorkStatusInfo);
			return response;
		}

		remoteWorkStatusInfo = new RemoteWorkStatusInfo(IdWork, 4, remoteWork
				.getWork());
		response.setWorkStatusInfo(remoteWorkStatusInfo);

		if (contextInfo != null)
			contextInfo.pop();

		return response;
	}

	//
	protected void processFeedback(SendFeedback workFeedback) {
		InheritableThreadContext contextInfo = J2EEWorkManager
				.getConctextInfo(workFeedback.getApplicationName());
		if (contextInfo != null)
			contextInfo.push();

		updateWork(workFeedback.getWorkStatusInfo());

		if (contextInfo != null)
			contextInfo.pop();
	}

	protected void processResponse(Response response) {
		Exception e = response.getExcepion();

		InheritableThreadContext contextInfo = J2EEWorkManager
				.getConctextInfo(response.getApplicationName());
		if (contextInfo != null)
			contextInfo.push();

		if (e == null) {
			updateWork(response.getWorkStatusInfo());
		} else {
			if (e instanceof ScheduleWorkRejectedException) {
				ScheduleWorkRejectedException eRejected = (ScheduleWorkRejectedException) e;
			} else {
				logger.severe("Unexpected processing failure at "
						+ response.getNodeFrom() + "; " + e + "\n"
						+ getStackTrace(e)
						+ "\nRemoving the node from the active server list");
				disableNode(response.getNodeFrom());
			}
		}

		if (contextInfo != null)
			contextInfo.pop();

	}

	protected void processReleaseWork(ReleaseWork releaseWork) {
		long lWorkId = releaseWork.getWorkId();
		Long IdWork = new Long(lWorkId);
		String nodeFrom = releaseWork.getNodeFrom();
		Map mapMember = (Map) getAcceptedWork().get(nodeFrom);
		if (mapMember != null) {
			RemoteWork remoteWork = (RemoteWork) mapMember.get(IdWork);
			if (remoteWork != null)
				synchronized (remoteWork) {
					if (remoteWork.getStatus() == 3)
						try {
							InheritableThreadContext contextInfo = J2EEWorkManager
									.getConctextInfo(remoteWork
											.getApplicationName());
							if (contextInfo != null)
								contextInfo.push();

							remoteWork.getWork().release();

							if (contextInfo != null)
								contextInfo.pop();
						} catch (Throwable e) {
							logger.warning("Exception during release:\n"
									+ getStackTrace(e));
						}
					remoteWork.setStatus(2);
					mapMember.remove(IdWork);
				}
		}
	}

	protected boolean updateWork(RemoteWorkStatusInfo statusInfo) {
		long lWorkId = statusInfo.getWorkId();
		RemoteWorkStatus remoteWorkStatus = getPostedWork(lWorkId);
		if (remoteWorkStatus == null)
			return false;
		boolean fNotify = false;
		switch (statusInfo.getStatus()) {
		case 2: // '\002'
		case 4: // '\004'
			removePostedWork(lWorkId);
			break;
		}
		remoteWorkStatus.setRemoteWorkStatusInfo(statusInfo);
		return true;
	}

	public Map getPostedWork() {
		return m_mapPostedWork;
	}

	public Map getAcceptedWork() {
		return m_mapAcceptedWork;
	}

	protected RemoteWorkStatus getPostedWork(long lWorkId) {
		return (RemoteWorkStatus) getPostedWork().get(new Long(lWorkId));
	}

	protected RemoteWorkStatus removePostedWork(long lWorkId) {
		return (RemoteWorkStatus) getPostedWork().remove(new Long(lWorkId));
	}

	protected void initWork(RemoteWork remoteWork) {
		long lWorkId = remoteWork.getWorkId();
		String nodeFrom = remoteWork.getNodeFrom();
		if (nodeFrom == null)
			return;

		Work work = remoteWork.getRemoteWork();
		Map mapAccepted = getAcceptedWork();
		Map mapWork = (Map) mapAccepted.get(nodeFrom);

		if (mapWork == null) {
			mapWork = new ConcurrentHashMap();
			mapAccepted.put(nodeFrom, mapWork);
		}
		Long IdWork = new Long(lWorkId);
		if (mapWork.containsKey(new Long(lWorkId)))
			throw new IllegalStateException("Same work scheduled twice: "
					+ lWorkId);
		mapWork.put(IdWork, remoteWork);
	}

	public static String localNode = null;

	public static String getLocalNode() {
		if (localNode != null) {
			return localNode;
		}
		Enumeration enums = null;
		try {
			enums = NetworkInterface.getNetworkInterfaces();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "127.0.0.1";
		}
		while (enums.hasMoreElements()) {
			NetworkInterface net = (NetworkInterface) enums.nextElement();
			Enumeration enum2 = net.getInetAddresses();
			while (enum2.hasMoreElements()) {
				InetAddress address = (InetAddress) enum2.nextElement();
				if (address.getHostAddress() == null
						|| address.getHostAddress().equals("127.0.0.1")) {
					continue;
				}
				localNode = address.getHostAddress();
				return localNode;
			}
		}
		return "127.0.0.1";
	}

	public static String getStackTrace(Throwable e) {
		String s = printStackTrace(e);
		if (s.startsWith(e.getClass().getName()))
			s = s.substring(s.indexOf('\n') + 1);
		return s;
	}

	public static String printStackTrace(Throwable e) {
		Writer writerRaw = new CharArrayWriter(1024);
		PrintWriter writerOut = new PrintWriter(writerRaw);
		e.printStackTrace(writerOut);
		String s = "No Stack Trace";
		try {
			writerRaw.flush();
			s = writerRaw.toString();
			writerRaw.close();
			writerOut.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			writerOut.close();
		}
		return s;
	}

	public static InheritableThreadContext getConctextInfo(String appName) {
		return (InheritableThreadContext) contextInfoMap.get(appName);
	}

	public static void saveConctextInfo(String appName,
			InheritableThreadContext contextInfo) {
		contextInfoMap.put(appName, contextInfo);
	}
}