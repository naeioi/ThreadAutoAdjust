package com.onceas.work;

/**
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
 * doc说明： The WorkManager is the abstraction for dispatching and monitoring
 * asynchronous work and is a factory for creating application short or long
 * lived Works. WorkManagers are created by the server administrator. The vendor
 * specific systems management console allows the administrator to create one or
 * more WorkManagers and associate a JNDI name with each one. The administrator
 * may specify implementation specific information such as min/max Works for
 * each WorkManager. An application that requires a WorkManager should declare a
 * resource-ref in the EJB or webapp that needs the WorkManager. The vendor
 * descriptor editor or J2EE IDE can be used to bind this resource-ref to a
 * physical WorkManager at deploy or development time. An EJB or servlet can
 * then get a reference to a WorkManager by looking up the resource-ref name in
 * JNDI and then casting it. For example, if the resource-ref was called
 * wm/WorkManager: <resource-ref> <res-ref-name>wm/WorkManager</res-ref-name>
 * <res-type>commonj.work.WorkManager</res-type> <res-auth>Container</res-auth>
 * <res-sharing-scope>Shareable</res-sharing-scope> </resource-ref> The Java
 * code to look this up would look like: InitialContext ic = new
 * InitialContext(); WorkManager wm =
 * (WorkManager)ic.lookup("java:comp/env/wm/WorkManager"); The res-auth and
 * res-sharing scopes are ignored in this version of the specification. The EJB
 * or servlet can then use the WorkManager as it needs to. When a Work is
 * scheduled, the declared context that is present on the thread (the J2EE
 * context) will be saved and propagated to the asynchronous methods that are
 * executed. This J2EE context at minimum will contain the java:comp namespace
 * and ClassLoader of the scheduler unless specified otherwise. Other J2EE
 * contexts such as security or a transactional context may be optionally added
 * by the application server vendor. Global transactions are always available
 * using the java:comp/UserTransaction JNDI name and are used in the same
 * fashion as they are used in servlets and bean-managed transaction Enterprise
 * Java Beans. A WorkManager can also be a pinned WorkManager. This is a
 * WorkManager obtained using the RemoteWorkItem.getWorkManager method. This
 * allows subsequent scheduleWorks to be send to the same remote WorkManager as
 * the one that is associated with the RemoteWorkItem. Pinned WorkManagers are
 * only supported on vendor implementations that support remote Works. However,
 * applications that follow the programming model will work on all
 * implementations however serializable Works will be executed within the local
 * JVM only on those implementations. If the scheduled Work is a daemon Work,
 * then the life-cycle of that Work is tied to the application that scheduled
 * it. If the application is stopped, the Work.release() method will be called.
 * 
 * @author 张磊
 * @version 1.0
 */

public interface WorkManager {
	public abstract String getName();

	public abstract String getApplicationName();

	public abstract String getModuleName();

	public abstract int getType();

	public abstract int getConfiguredThreadCount();

	public abstract void schedule(Runnable runnable);

	public abstract boolean executeIfIdle(Runnable runnable);

	public abstract boolean scheduleIfBusy(Runnable runnable);

	public abstract int getQueueDepth();

	public abstract void setInternal();

	public abstract boolean isInternal();

	public abstract boolean isThreadOwner(Thread thread);

	public abstract void setThreadCount(int i) throws IllegalStateException,
			SecurityException;

	public static final int SELF_TUNING_TYPE = 1;

	public static final int FIXED_THREAD_COUNT_TYPE = 2;
	
	// add by syk 20100918
	public abstract void setModuleName(String moduleName);
}
