package com.onceas.runtime.work;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.onceas.health.HealthState;
import com.onceas.runtime.Runtime;
import com.onceas.runtime.RuntimeMBean;
import com.onceas.work.DebugWM;
import com.onceas.work.WorkAdapter;
import com.onceas.work.constraint.CalendarQueue;
import com.onceas.work.constraint.MinThreadsConstraint;
import com.onceas.work.constraint.RequestManager;
import com.onceas.work.constraint.ServerWorkManagerImpl;
import com.onceas.work.threadpool.ExecuteThread;

public final class ThreadPoolRuntime extends Runtime implements
		ThreadPoolRuntimeMBean {
	private static final boolean DESTRUCTIVE_DUMP = false;

	private static final String SHARED_CAPACITY_EXCEEDED = "Shared WorkManager capacity exceeded";

	private static final String STUCK_THREADS = "ThreadPool has stuck threads";

	private final transient RequestManager requestManager;

	private static final HealthState HEALTH_OK = new HealthState(0);

	private static final HealthState HEALTH_OVERLOADED = new HealthState(4,
			"Shared WorkManager capacity exceeded");

	private static final HealthState HEALTH_WARNING = new HealthState(1,
			"ThreadPool has stuck threads");

	private HealthState healthState;

	private static final int DEFAULT_STUCk_THREAD_MAX_TIME = 30;

	public static ThreadPoolRuntimeMBean createThreadPoolRuntimeMBean(
			RequestManager requestmanager) {
		ThreadPoolRuntimeMBean threadPoolRuntimeMBean = new ThreadPoolRuntime(
				requestmanager);
		return threadPoolRuntimeMBean;
	}

	public ThreadPoolRuntime(RequestManager requestmanager) {
		super(Constant.ONCEAS_THREAD_POOL_RUNTIME);
		requestManager = requestmanager;
		type = Constant.THREAD_POOL_TYPE;
		try {
			this.objectName = new ObjectName(
					Constant.WORK_MANAGER_RUNTIME_MBEAN_DOMAIN + ":"
							+ Constant.RUNTIME_TYPE + "=" + type + ","
							+ Constant.THREAD_POOL_RUNTIME + "="
							+ Constant.ONCEAS_THREAD_POOL_RUNTIME);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public ExecuteThreadRuntimeMBean[] getExecuteThreads() {
		ExecuteThread aexecutethread[] = requestManager.getExecuteThreads();
		int i = aexecutethread.length;
		ExecuteThreadRuntimeMBean aexecutethread1[] = new ExecuteThreadRuntimeMBean[i];
		for (int j = 0; j < i; j++)
			aexecutethread1[j] = aexecutethread[j].getRuntime();

		return aexecutethread1;
	}

	private int getStuckThreadCount() {
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		try {
			Integer stuckThreadMaxTime = (Integer) server.getAttribute(
					new ObjectName("onceas.work:service=WorkManagerFactory"),
					"StuckThreadMaxTime");
			long l = stuckThreadMaxTime.intValue() * 1000;
			java.util.ArrayList arraylist = requestManager.getStuckThreads(l);
			return arraylist == null ? 0 : arraylist.size();
		} catch (AttributeNotFoundException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (MBeanException e) {
			e.printStackTrace();
		} catch (ReflectionException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public ExecuteThreadRuntimeMBean[] getStuckExecuteThreads() {
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		Integer stuckThreadMaxTime = null;
		try {
			stuckThreadMaxTime = (Integer) server.getAttribute(new ObjectName(
					"onceas.work:service=WorkManagerFactory"),
					"StuckThreadMaxTime");
		} catch (AttributeNotFoundException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (MBeanException e) {
			e.printStackTrace();
		} catch (ReflectionException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		if (stuckThreadMaxTime == null) {
			stuckThreadMaxTime = new Integer(DEFAULT_STUCk_THREAD_MAX_TIME);
		}
		if (DebugWM.debug_StuckThread)
			log("getStuckExecuteThreads()");
		long l = stuckThreadMaxTime.intValue() * 1000;
		java.util.ArrayList arraylist = requestManager.getStuckThreads(l);
		if (arraylist == null || arraylist.size() == 0)
			return null;
		int i = arraylist.size();
		ExecuteThreadRuntimeMBean aexecutethread[] = new ExecuteThreadRuntimeMBean[i];
		for (int j = 0; j < i; j++)
			aexecutethread[j] = ((ExecuteThread) arraylist.get(j)).getRuntime();

		return aexecutethread;
	}

	public int getExecuteThreadTotalCount() {
		return requestManager.getExecuteThreadCount();
	}

	public int getHoggingThreadCount() {
		return requestManager.getHogSize();
	}

	public int getHealthThreadCount() {
		return requestManager.getActiveExecuteThreadCount();
	}

	public int getExecuteThreadCount() {
		return requestManager.getExecuteThreadCount()
				- requestManager.getIdleThreadCount()
				- requestManager.getStandbyCount();
	}

	public int getStandbyThreadCount() {
		return requestManager.getStandbyCount();
	}

	public int getExecuteThreadIdleCount() {
		return requestManager.getIdleThreadCount();
	}

	public int getPendingUserRequestCount() {
		return ServerWorkManagerImpl.SHARED_OVERLOAD_MANAGER.getQueueDepth();
	}

	public int getQueueLength() {
		return requestManager.getQueueDepth();
	}

	public int getSharedCapacityForWorkManagers() {
		return ServerWorkManagerImpl.SHARED_OVERLOAD_MANAGER.getCapacity();
	}

	public long getCompletedRequestCount() {
		return requestManager.getQueueDepartures();
	}

	public double getThroughput() {
		double d = requestManager.getThroughput();
		return d <= 0.0D ? 0.0D : d;
	}

	public int getMinThreadsConstraintsPending() {
		return requestManager.getMustRunCount();
	}

	public long getMinThreadsConstraintsCompleted() {
		return requestManager.mtcDepartures;
	}

	public boolean isSuspended() {
		return false;
	}

	public HealthState getHealthState() {
		HealthState healthstate = HEALTH_OK;
		if (!ServerWorkManagerImpl.SHARED_OVERLOAD_MANAGER.accept())
			healthstate = HEALTH_OVERLOADED;
		else if (getStuckThreadCount() > 0)
			healthstate = HEALTH_WARNING;
		if (healthstate != healthState) {
			// _postSet("HealthState", healthState, healthstate);
			healthState = healthstate;
		}
		return healthState;
	}

	private void dumpAndDestroy() {
		synchronized (RequestManager.getInstance()) {
			// log("\n\n" + VM.getVM().threadDumpAsString() + "\n\n");
			CalendarQueue calendarqueue = RequestManager.getInstance().queue;
			StringBuffer stringbuffer = new StringBuffer();
			for (int i = 0; i < calendarqueue.size(); i++) {
				WorkAdapter workadapter = (WorkAdapter) calendarqueue.pop();
				log("---- count " + i + " ------------- ");
				log(workadapter.dump() + "\n");
			}

			log("###### PRINTING MIN THREADS CONSTRAINTS #######");
			MinThreadsConstraint aminthreadsconstraint[] = RequestManager
					.getInstance().minThreadsConstraints;
			for (int j = 0; j < aminthreadsconstraint.length; j++) {
				log("@@@@@@@@ MTC @@@@@@ " + aminthreadsconstraint[j].getName());
				aminthreadsconstraint[j].dumpAndDestroy();
			}

		}
	}

	private void log(String s) {
		System.out.println("<ThreadPoolRuntime>" + s);
	}

	public long getCanceledRequestCount() {
		return requestManager.canceledCount;
	}

	public long getRejectedRequestCount() {
		return requestManager.rejectedCount;
	}

	public void resetCompletedRequestCount() {
		requestManager.departures = 0;
	}
}
