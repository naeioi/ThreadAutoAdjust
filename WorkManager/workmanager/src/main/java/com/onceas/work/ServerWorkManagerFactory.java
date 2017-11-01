package com.onceas.work;

import com.onceas.work.constraint.FairShareRequestClass;
import com.onceas.work.constraint.MaxThreadsConstraint;
import com.onceas.work.constraint.MinThreadsConstraint;
import com.onceas.work.constraint.OverloadManager;
import com.onceas.work.constraint.RequestClass;
import com.onceas.work.constraint.RequestManager;
import com.onceas.work.constraint.ResponseTimeRequestClass;
import com.onceas.work.constraint.ServerWorkManagerImpl;
import com.onceas.work.constraint.StuckThreadManager;

public final class ServerWorkManagerFactory extends WorkManagerFactory {

	private ServerWorkManagerFactory() {
	}

	static ServerWorkManagerFactory get() {
		return SINGLETON;
	}

	public static synchronized void initialize(int i) {
		if (SINGLETON != null) {
			return;
		} else {
			SINGLETON = new ServerWorkManagerFactory();
			WorkManagerFactory.set(SINGLETON);
			WorkManagerLogger.logInitializingSelfTuning();
			SINGLETON.initializeHere(i);
			return;
		}
	}

	private void initializeHere(int i) {
		ServerWorkManagerImpl.initialize(i);
		REJECTOR = create(WorkManagerConstant.ONCEAS_REJECTOR, 5, -1);
		DEFAULT = create(WorkManagerConstant.ONCEAS_KERNEL_DEFAULT, -1, -1);
		SYSTEM = create(WorkManagerConstant.ONCEAS_KERNEL_SYSTEM, 5, -1);
		WorkManager workmanager = create(
				WorkManagerConstant.ONCEAS_KERNEL_NON_BLOCKING, 3, -1);
		WorkManager workmanager1 = create(WorkManagerConstant.ONCEAS_DIRECT,
				-1, -1);
		byName.put(WorkManagerConstant.ONCEAS_KERNEL_SYSTEM, SYSTEM);
		byName.put(WorkManagerConstant.ONCEAS_KERNEL_NON_BLOCKING, workmanager);
		byName.put(WorkManagerConstant.ONCEAS_DIRECT, workmanager1);
		RequestManager.initInternalRequests();
	}

	public static WorkManager create(String s, int i, int j) {
		return create(s, -1, -1, i, j);
	}

	private static WorkManager create(String s, int i, int j, int k, int l) {
		MinThreadsConstraint minthreadsconstraint = null;
		if (k != -1)
			minthreadsconstraint = new MinThreadsConstraint(s, k);
		MaxThreadsConstraint maxthreadsconstraint = null;
		if (l != -1)
			maxthreadsconstraint = new MaxThreadsConstraint(s, l);
		Object obj = null;
		if (j > 0)
			obj = new ResponseTimeRequestClass(s, j);
		else if (i > 0)
			obj = new FairShareRequestClass(s, i);
		WorkManager workmanager = create(s, null, null, ((RequestClass) (obj)),
				maxthreadsconstraint, minthreadsconstraint, null, null);
		// if(KernelStatus.isServer())
		// createRuntimeMBean(workmanager, ((RequestClass) (obj)),
		// minthreadsconstraint, maxthreadsconstraint);
		return workmanager;
	}

	/**
	 * private static void createRuntimeMBean(WorkManager workmanager,
	 * RequestClass requestclass, MinThreadsConstraint minthreadsconstraint,
	 * MaxThreadsConstraint maxthreadsconstraint) { if(workmanager instanceof
	 * ServerWorkManagerImpl) try { WorkManagerRuntimeMBeanImpl
	 * workmanagerruntimembeanimpl = new
	 * WorkManagerRuntimeMBeanImpl((ServerWorkManagerImpl)workmanager);
	 * if(requestclass != null)
	 * workmanagerruntimembeanimpl.setRequestClassRuntime(new
	 * RequestClassRuntimeMBeanImpl(requestclass)); if(minthreadsconstraint !=
	 * null) workmanagerruntimembeanimpl.setMinThreadsConstraintRuntime(new
	 * MinThreadsConstraintRuntimeMBeanImpl(minthreadsconstraint));
	 * if(maxthreadsconstraint != null)
	 * workmanagerruntimembeanimpl.setMaxThreadsConstraintRuntime(new
	 * MaxThreadsConstraintRuntimeMBeanImpl(maxthreadsconstraint));
	 * ManagementService.getRuntimeAccess(kernelId).getServerRuntime().addWorkManagerRuntime(workmanagerruntimembeanimpl); }
	 * catch(ManagementException managementexception) {
	 * WorkManagerLogger.logRuntimeMBeanCreationError(workmanager.getName(),
	 * managementexception); } }
	 */

	protected WorkManager create(String s, int i, int j, int k) {
		return create(s, i, -1, j, k);
	}

	protected WorkManager createResponseTime(String s, int i, int j, int k) {
		return create(s, -1, i, j, k);
	}

	/**
	 * protected WorkManager findAppScoped(String s, String s1, String s2) {
	 * ApplicationContextInternal applicationcontextinternal; if(s1 != null &&
	 * s1.length() != 0) applicationcontextinternal =
	 * ApplicationAccess.getApplicationAccess().getApplicationContext(getSanitizedAppName(s1));
	 * else applicationcontextinternal =
	 * ApplicationAccess.getApplicationAccess().getCurrentApplicationContext();
	 * if(applicationcontextinternal == null) { return null; } else { String s3 =
	 * s2 == null ?
	 * ApplicationAccess.getApplicationAccess().getCurrentModuleName() : s2;
	 * WorkManager workmanager =
	 * applicationcontextinternal.getWorkManagerCollection().get(s3, s); return
	 * workmanager == null ? DEFAULT : workmanager; } }
	 */
	private String getSanitizedAppName(String s) {
		if (s != null && s.length() != 0) {
			int i = s.indexOf("@");
			if (i >= 0)
				s = s.substring(0, i);
		}
		return s;
	}

	static WorkManager create(String s, String s1, String s2) {
		return create(s, s1, s2, null, null, null, null, null);
	}

	static WorkManager create(String s, String s1, String s2,
			StuckThreadManager stuckthreadmanager) {
		return create(s, s1, s2, null, null, null, null, stuckthreadmanager);
	}

	public static WorkManager create(String s, String s1, String s2,
			RequestClass requestclass,
			MaxThreadsConstraint maxthreadsconstraint,
			MinThreadsConstraint minthreadsconstraint,
			OverloadManager overloadmanager,
			StuckThreadManager stuckthreadmanager) {
		return new ServerWorkManagerImpl(s, s1, s2, requestclass,
				maxthreadsconstraint, minthreadsconstraint, overloadmanager,
				stuckthreadmanager);
	}

	/**
	 * static WorkManager create(String s, String s1, WorkManagerMBean
	 * workmanagermbean, StuckThreadManager stuckthreadmanager) {
	 * GlobalWorkManagerComponentsFactory.WorkManagerTemplate
	 * workmanagertemplate =
	 * GlobalWorkManagerComponentsFactory.getInstance().findWorkManagerTemplate(workmanagermbean);
	 * return create(workmanagermbean.getName(), s, s1,
	 * workmanagertemplate.getRequestClass(),
	 * workmanagertemplate.getMaxThreadsConstraint(),
	 * workmanagertemplate.getMinThreadsConstraint(),
	 * workmanagertemplate.getCapacity(), stuckthreadmanager); }
	 */
	// private static final AuthenticatedSubject kernelId =
	// (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
	private static ServerWorkManagerFactory SINGLETON;

}
