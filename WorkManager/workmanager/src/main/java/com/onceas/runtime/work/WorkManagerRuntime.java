package com.onceas.runtime.work;

import java.util.ArrayList;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.onceas.health.HealthState;
import com.onceas.runtime.Runtime;
import com.onceas.runtime.RuntimeAccess;
import com.onceas.runtime.RuntimeMBean;
import com.onceas.work.constraint.ServerWorkManagerImpl;

public class WorkManagerRuntime extends Runtime implements
		WorkManagerRuntimeMBean {
	private static final HealthState HEALTH_OK = new HealthState(0);

	private final ServerWorkManagerImpl wm;

	private MinThreadsConstraintRuntimeMBean minThreadsConstraintRuntimeMBean;

	private MaxThreadsConstraintRuntimeMBean maxThreadsConstraintRuntimeMBean;

	private RequestClassRuntimeMBean requestClassRuntimeMBean;

	// private static final AuthenticatedSubject kernelId =
	// (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
	private int pastStuckThreadCount;

	private long lastTime;

	public static WorkManagerRuntimeMBean creatWorkManagerRuntimeMBean(
			ServerWorkManagerImpl serverworkmanagerimpl, RuntimeMBean parent) {
		serverworkmanagerimpl.getApplicationName();

		WorkManagerRuntime workmanagerruntimembeanimpl;
		if (parent == null) {
			workmanagerruntimembeanimpl = new WorkManagerRuntime(
					serverworkmanagerimpl);
		} else {
			workmanagerruntimembeanimpl = new WorkManagerRuntime(
					serverworkmanagerimpl, parent);
		}
		workmanagerruntimembeanimpl.setRequestClassRuntime(RequestClassRuntime
				.createRequestClassRuntimeMBean(serverworkmanagerimpl
						.getRequestClass(), workmanagerruntimembeanimpl));
		workmanagerruntimembeanimpl
				.setMinThreadsConstraintRuntime(MinThreadsConstraintRuntime
						.createMinThreadsConstraintRuntimeMBean(
								serverworkmanagerimpl.getMinThreadsConstraint(),
								workmanagerruntimembeanimpl));
		workmanagerruntimembeanimpl
				.setMaxThreadsConstraintRuntime(MaxThreadsConstraintRuntime
						.createMaxThreadsConstraintRuntimeMBean(
								serverworkmanagerimpl.getMaxThreadsConstraint(),
								workmanagerruntimembeanimpl));
		return workmanagerruntimembeanimpl;
	}

	WorkManagerRuntime(ServerWorkManagerImpl serverworkmanagerimpl) {
		super(serverworkmanagerimpl.getName());
		wm = serverworkmanagerimpl;
		this.type = Constant.WORK_MANAGER_TYPE;

		// save type to wm by syk
		wm.setRuntimeType(Constant.WORK_MANAGER_TYPE);
		// end

		try {
			this.objectName = new ObjectName(
					Constant.WORK_MANAGER_RUNTIME_MBEAN_DOMAIN + ":"
							+ Constant.RUNTIME_TYPE + "=" + type + ","
							+ Constant.CONTEXT_APP_NAME + "="
							+ wm.getApplicationName() + ","
							+ Constant.CONTEXT_MODULE_NAME + "="
							+ wm.getModuleName() + ","
							+ Constant.WORK_MANAGER_NAME + "=" + wm.getName());
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private WorkManagerRuntime(ServerWorkManagerImpl serverworkmanagerimpl,
			RuntimeMBean parent) {
		super(serverworkmanagerimpl.getName(), parent);
		wm = serverworkmanagerimpl;
		this.type = Constant.CHILDE_WORK_MANAGER_TYPE;
		// save type to wm by syk
		wm.setRuntimeType(Constant.CHILDE_WORK_MANAGER_TYPE);
		// end
		try {
			this.objectName = new ObjectName(
					Constant.WORK_MANAGER_RUNTIME_MBEAN_DOMAIN + ":"
							+ Constant.RUNTIME_TYPE + "=" + type + ","
							+ Constant.CONTEXT_APP_NAME + "="
							+ wm.getApplicationName() + ","
							+ Constant.CONTEXT_MODULE_NAME + "="
							+ wm.getModuleName() + ","
							+ Constant.WORK_MANAGER_NAME + "=" + wm.getName());
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public int getPendingRequests() {
		int i = (int) (wm.getAcceptedCount() - wm.getStartedCount() - wm
				.getCompletedCount());
		return i <= 0 ? 0 : i;
	}

	public int getExecutingRequests() {
		int i = (int) (wm.getStartedCount() - wm.getCompletedCount());
		return i <= 0 ? 0 : i;
	}

	public long getCompletedRequests() {
		return wm.getCompletedCount();
	}

	public int getStuckThreadCount() {
		if (timeToSync()) {
			ThreadPoolRuntimeMBean threadpoolruntimembean = RuntimeAccess
					.getRuntimeAccess().getThreadPoolRuntime();
			ExecuteThreadRuntimeMBean aexecutethread[] = threadpoolruntimembean
					.getStuckExecuteThreads();
			if (aexecutethread == null) {
				pastStuckThreadCount = 0;
				return 0;
			}
			int i = 0;
			for (int j = 0; j < aexecutethread.length; j++)
				if (claimThread(aexecutethread[j]))
					i++;

			pastStuckThreadCount = i;
			return i;
		} else {
			return pastStuckThreadCount;
		}
	}

	public MinThreadsConstraintRuntimeMBean getMinThreadsConstraintRuntime() {
		return minThreadsConstraintRuntimeMBean;
	}

	public MaxThreadsConstraintRuntimeMBean getMaxThreadsConstraintRuntime() {
		return maxThreadsConstraintRuntimeMBean;
	}

	public void setMinThreadsConstraintRuntime(
			MinThreadsConstraintRuntimeMBean minthreadsconstraintruntimembean) {
		minThreadsConstraintRuntimeMBean = minthreadsconstraintruntimembean;
	}

	public void setMaxThreadsConstraintRuntime(
			MaxThreadsConstraintRuntimeMBean maxthreadsconstraintruntimembean) {
		maxThreadsConstraintRuntimeMBean = maxthreadsconstraintruntimembean;
	}

	public void setRequestClassRuntime(
			RequestClassRuntimeMBean requestclassruntimembean) {
		requestClassRuntimeMBean = requestclassruntimembean;
	}

	public RequestClassRuntimeMBean getRequestClassRuntime() {
		return requestClassRuntimeMBean;
	}

	public String getApplicationName() {
		return wm.getApplicationName();
	}

	public String getModuleName() {
		return wm.getModuleName();
	}

	public HealthState getHealthState() {
		ArrayList arraylist = new ArrayList();
		byte byte0 = 0;
		if (wm.isInternal())
			return HEALTH_OK;
		if (wm.getOverloadManager() != null
				&& !wm.getOverloadManager().accept()) {
			byte0 = 4;
			arraylist.add(ServerWorkManagerImpl.getOverloadMessage(wm
					.getOverloadManager()));
		}
		if (!ServerWorkManagerImpl.SHARED_OVERLOAD_MANAGER.accept()) {
			byte0 = 4;
			arraylist
					.add(ServerWorkManagerImpl
							.getOverloadMessage(ServerWorkManagerImpl.SHARED_OVERLOAD_MANAGER));
		}
		if (ServerWorkManagerImpl.LOW_MEMORY_LISTENER.lowMemory()) {
			byte0 = 4;
			arraylist.add(wm.getLowMemoryMessage());
		}
		if (wm.getStuckThreadManager() != null
				&& wm.getStuckThreadManager().getStuckThreadCount() > 0) {
			byte0 = 2;
			arraylist.add(wm.getStuckThreadManager().getStuckThreadCount()
					+ " stuck threads detected in WorkManager '" + wm.getName()
					+ "'");
		}
		if (arraylist.size() == 0) {
			return new HealthState(byte0);
		} else {
			String as[] = new String[arraylist.size()];
			arraylist.toArray(as);
			return new HealthState(byte0, as);
		}
	}

	private synchronized boolean timeToSync() {
		long l = System.currentTimeMillis();
		if (l - lastTime > 10000L) {
			lastTime = l;
			return true;
		} else {
			return false;
		}
	}

	private boolean claimThread(ExecuteThreadRuntimeMBean executethreadRuntime) {
		return compareNames(executethreadRuntime.getWorkManagerName(),
				getName())
				&& compareNames(executethreadRuntime.getApplicationName(),
						getApplicationName())
				&& compareNames(executethreadRuntime.getModuleName(),
						getModuleName());
	}

	private static boolean compareNames(String s, String s1) {
		if (s == null && s1 == null)
			return true;
		return s != null && s.equalsIgnoreCase(s1);
	}
}
