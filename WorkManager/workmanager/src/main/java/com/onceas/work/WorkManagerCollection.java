package com.onceas.work;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import com.onceas.descriptor.wm.dd.CapacityConstraintDD;
import com.onceas.descriptor.wm.dd.ContextCaseDD;
import com.onceas.descriptor.wm.dd.ContextRequestDD;
import com.onceas.descriptor.wm.dd.FairShareRequestDD;
import com.onceas.descriptor.wm.dd.MaxThreadsConstraintDD;
import com.onceas.descriptor.wm.dd.MinThreadsConstraintDD;
import com.onceas.descriptor.wm.dd.ResponseTimeRequestDD;
import com.onceas.descriptor.wm.dd.WmDD;
import com.onceas.descriptor.wm.dd.WorkManagerBeanDD;
import com.onceas.descriptor.wm.dd.WorkManagerShutDownTriggerDD;
import com.onceas.runtime.RuntimeMBean;
import com.onceas.runtime.work.Constant;
import com.onceas.runtime.work.WorkManagerRuntime;
import com.onceas.runtime.work.WorkManagerRuntimeMBean;
import com.onceas.work.config.WorkManagerConfigRegister;
import com.onceas.work.constraint.ApplicationAdminModeAction;
import com.onceas.work.constraint.ContextRequestClass;
import com.onceas.work.constraint.FairShareRequestClass;
import com.onceas.work.constraint.MaxThreadsConstraint;
import com.onceas.work.constraint.MinThreadsConstraint;
import com.onceas.work.constraint.OverloadManager;
import com.onceas.work.constraint.RequestClass;
import com.onceas.work.constraint.ResponseTimeRequestClass;
import com.onceas.work.constraint.ServerFailureAction;
import com.onceas.work.constraint.ServerWorkManagerImpl;
import com.onceas.work.constraint.ServiceClassSupport;
import com.onceas.work.constraint.StuckThreadManager;
import com.onceas.work.constraint.WorkManagerShutdownAction;
import com.onceas.work.management.notification.UnifiedListenerRepository;
import com.onceas.work.management.notification.WMUpdateEvent;
import com.onceas.work.util.WMUpdateListenerName;

public final class WorkManagerCollection extends AbstractCollection {
	private static final DebugCategory debugWMCollection = Debug
			.getCategory("onceas.workmanagercollection");

	private static final String MODULE_DELIMITER = "@";

	private Map<String, WorkManager> workManagers;

	private Map<String, WorkManager> moduleWorkManagers;

	private Map<String, RequestClass> requestClassMap;

	private Map maxMap;

	private Map minMap;

	private Map overloadMap;

	private final String applicationName;

	private boolean initialized;

	private boolean internal;

	// TODO to shop a app
	private ApplicationAdminModeAction adminModeAction = null;

	private ServerFailureAction serverFailureAction = null;

	private List<ObjectName> runtimeMBeanObjectNames = new ArrayList<ObjectName>();

	private WorkManager appWorkManager = null;

	// fields to indicates type
	public static final int WORKMANAGER_TYPE = 0;

	public static final int MAX_THTREADS_CONSTRAINT_TYPE = 1;

	public static final int MIN_THTREADS_CONSTRAINT_TYPE = 2;

	public static final int OVERLOAD_MANAGER_TYPE = 4;

	public static final int REQUEST_CLASS_TYPE = 8;

	// to add WMUpdateListener
	UnifiedListenerRepository listenerRepository;

	// to store workmanagerruntimembean so can delete it at run time
	private Map<ObjectName, WorkManagerRuntimeMBean> workManagerRuntimeMap;

	// save reference to workmanager config register in order to unreigister it
	// when undeploy
	private WorkManagerConfigRegister workManagerConfigRegister;

	public WorkManagerCollection(String s) {
		this(s, false);
	}

	public WorkManagerCollection(String s, boolean flag) {
		workManagers = new ConcurrentHashMap<String, WorkManager>();
		moduleWorkManagers = new ConcurrentHashMap<String, WorkManager>();
		requestClassMap = new ConcurrentHashMap();
		maxMap = new ConcurrentHashMap();
		minMap = new ConcurrentHashMap();
		overloadMap = new ConcurrentHashMap();
		applicationName = s;
		internal = flag;
		workManagerRuntimeMap = new ConcurrentHashMap();
		debug("creating a new collection for app: " + s + ", internal: " + flag);
		// for listener
		listenerRepository = new UnifiedListenerRepository();
	}

	public synchronized void initialize() {
		if (initialized)
			return;
		// init server fail action
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		try {
			serverFailureAction = (ServerFailureAction) server.getAttribute(
					new ObjectName("onceas.work:service=WorkManagerFactory"),
					"ServerFailureAction");
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
		initialized = true;
	}

	public Iterator iterator() {
		return workManagers.values().iterator();
	}

	public int size() {
		return workManagers.size();
	}

	private void populate(FairShareRequestDD afairsharerequestDD[]) {
		if (afairsharerequestDD == null)
			return;
		for (int i = 0; i < afairsharerequestDD.length; i++) {
			FairShareRequestDD fairsharerequestDD = afairsharerequestDD[i];
			FairShareRequestClass fairsharerequestclass = new FairShareRequestClass(
					fairsharerequestDD.getName(), fairsharerequestDD
							.getFairShare());
			requestClassMap.put(fairsharerequestDD.getName(),
					fairsharerequestclass);
		}
	}

	private ContextRequestClass getContextRequest(
			WorkManagerBeanDD workManagerBeanDD) throws Exception {
		ContextRequestDD contextrequestclassDD = workManagerBeanDD
				.getContextRequestClass();
		if (contextrequestclassDD != null) {
			ContextRequestClass contextrequestclass = new ContextRequestClass(
					contextrequestclassDD.getName());
			Iterator iterator = contextrequestclassDD.getContextCaseDDs().iterator();
			while (iterator.hasNext()) {
				ContextCaseDD contextDD = (ContextCaseDD) iterator.next();
				RequestClass requestclass = getRequestClass(contextDD
						.getRequestClassName());
				if (requestclass == null)
					throw new Exception("request class "
							+ contextDD.getRequestClassName() + " not found");
				if (contextDD.getUserName() != null) {
					contextrequestclass.addUser(contextDD.getUserName(),
							requestclass);
					continue;
				}
				if (contextDD.getGroupName() != null)
					contextrequestclass.addGroup(contextDD.getGroupName(),
							requestclass);
			}
			requestClassMap.put(contextrequestclass.getName(),
					contextrequestclass);
			// TODO 当dd更新时，需要通过事件及时更新相应的Bean;
			// ((DescriptorBean)maxthreadsconstraintbean).addBeanUpdateListener(maxthreadsconstraint);
			return contextrequestclass;
		}
		return null;
	}

	/**
	 * @param s
	 *            module name
	 */
	// change modifie private to public :by syk
	public void populate(MaxThreadsConstraint maxThreadsConstraint, String s) {
		if (maxThreadsConstraint == null)
			return;
		maxMap.put(maxThreadsConstraint.getName(), maxThreadsConstraint);
		WorkManagerService workmanagerservice = WorkManagerServiceImpl
				.createService(maxThreadsConstraint.getName(), applicationName,
						s, null, maxThreadsConstraint, null, null, null);
		if (internal)
			workmanagerservice.setInternal();
		if (s != null)
			workManagers.put(s + "@" + maxThreadsConstraint.getName(),
					workmanagerservice);
		else
			workManagers
					.put(maxThreadsConstraint.getName(), workmanagerservice);
		setJNDIName(applicationName, maxThreadsConstraint.getName());

		// TODO PoolBasedMaxThreadsConstraint
	}

	/**
	 * @param s
	 *            module name
	 */
	public void populate(MinThreadsConstraint minThreadsConstraint, String s) {
		if (minThreadsConstraint == null)
			return;
		minMap.put(minThreadsConstraint.getName(), minThreadsConstraint);
		WorkManagerService workmanagerservice = WorkManagerServiceImpl
				.createService(minThreadsConstraint.getName(), applicationName,
						s, null, null, minThreadsConstraint, null, null);
		if (internal)
			workmanagerservice.setInternal();
		if (s != null)
			workManagers.put(s + "@" + minThreadsConstraint.getName(),
					workmanagerservice);
		else
			workManagers
					.put(minThreadsConstraint.getName(), workmanagerservice);
		setJNDIName(applicationName, minThreadsConstraint.getName());
	}

	/**
	 * @param s
	 *            module name
	 */
	public void populate(RequestClass requestclass, String s) {
		if (requestclass == null)
			return;
		// unregistContextCase, why? by syk
		unregistContextCase(requestclass);
		requestClassMap.put(requestclass.getName(), requestclass);
		WorkManagerService workmanagerservice = WorkManagerServiceImpl
				.createService(requestclass.getName(), applicationName, s,
						requestclass, null, null, null, null);
		if (internal)
			workmanagerservice.setInternal();
		if (s != null)
			workManagers.put(s + "@" + requestclass.getName(),
					workmanagerservice);
		else
			workManagers.put(requestclass.getName(), workmanagerservice);
		setJNDIName(applicationName, requestclass.getName());
	}

	public void populate(OverloadManager overloadmanager, String s) {
		if (overloadmanager == null)
			return;
		overloadMap.put(overloadmanager.getName(), overloadmanager);
		WorkManagerService workmanagerservice = WorkManagerServiceImpl
				.createService(overloadmanager.getName(), applicationName, s,
						null, null, null, overloadmanager, null);
		if (internal)
			workmanagerservice.setInternal();
		if (s != null)
			workManagers.put(s + "@" + overloadmanager.getName(),
					workmanagerservice);
		else
			workManagers.put(overloadmanager.getName(), workmanagerservice);
		setJNDIName(applicationName, overloadmanager.getName());
	}

	/**
	 * @param s
	 *            module name
	 */
	public WorkManagerService populate(String s,
			WorkManagerBeanDD workManagerBeanDD) {

		RequestClass requestclass = getPolicy(workManagerBeanDD);
		MinThreadsConstraint minthreadsconstraint = getMinConstraint(workManagerBeanDD);
		OverloadManager overloadmanager = getCapacity(workManagerBeanDD);

		StuckThreadManager stuckthreadmanager = getStuckThreadManager(workManagerBeanDD);
		MaxThreadsConstraint maxthreadsconstraint = getMaxConstraint(workManagerBeanDD);

		return populate(workManagerBeanDD.getName(), applicationName, s,
				requestclass, maxthreadsconstraint, minthreadsconstraint,
				overloadmanager, stuckthreadmanager);
	}

	// begin syk

	// called when creating new workmanager in WorkManagerConfig
	public WorkManagerService populate(String wmName, String appName, String s,
			RequestClass requestclass,
			MaxThreadsConstraint maxthreadsconstraint,
			MinThreadsConstraint minthreadsconstraint,
			OverloadManager overloadmanager,
			StuckThreadManager stuckthreadmanager) {

		populate(maxthreadsconstraint, s);
		populate(minthreadsconstraint, s);
		populate(requestclass, s);
		populate(overloadmanager, s);

		WorkManagerService workmanagerservice = WorkManagerServiceImpl
				.createService(wmName, applicationName, s, requestclass,
						maxthreadsconstraint, minthreadsconstraint,
						overloadmanager, stuckthreadmanager);
		if (stuckthreadmanager != null)
			stuckthreadmanager.setWorkManagerService(workmanagerservice);
		if (internal)
			workmanagerservice.setInternal();
		if (s != null)
			workManagers.put(s + "@" + wmName, workmanagerservice);
		else
			workManagers.put(wmName, workmanagerservice);

		// add listener for each workmanger component which is not null by syk
		addListener(maxthreadsconstraint, wmName, s);
		addListener(minthreadsconstraint, wmName, s);
		addListener(overloadmanager, wmName, s);
		addListener(requestclass, wmName, s);
		// end
		registWorkManagerRuntime(workmanagerservice.getDelegate());
		setJNDIName(applicationName, wmName);
		return workmanagerservice;
	}

	// ---------------------------------------------------------------
	// 将新创建的workmanager的组件添加到其中
	// ServerWorkManagerImpl中的属性由final变为可更改，是否会产生问题？
	public void addWorkManagerComponent(String s, String wmName,
			OverloadManager overloadManager) {
		String key = constructMapKey(s, wmName);
		WorkManagerService wmservice = (WorkManagerService) workManagers
				.get(key);
		if (wmservice != null) {
			ServerWorkManagerImpl swmi = (ServerWorkManagerImpl) wmservice
					.getDelegate();
			swmi.setOverloadManager(overloadManager);
		}
	}

	public void addWorkManagerComponent(String s, String wmName,
			MaxThreadsConstraint maxthreadsconstraint) {
		String key = constructMapKey(s, wmName);
		WorkManagerService wmservice = (WorkManagerService) workManagers
				.get(key);
		if (wmservice != null) {
			ServerWorkManagerImpl swmi = (ServerWorkManagerImpl) wmservice
					.getDelegate();
			swmi.setMaxThreadsConstraint(maxthreadsconstraint);
		}
	}

	public void addWorkManagerComponent(String s, String wmName,
			MinThreadsConstraint minthreadsconstraint) {
		String key = constructMapKey(s, wmName);
		WorkManagerService wmservice = (WorkManagerService) workManagers
				.get(key);
		if (wmservice != null) {
			ServerWorkManagerImpl swmi = (ServerWorkManagerImpl) wmservice
					.getDelegate();
			// register this min to RequestManager like construting
			// ServerWorkManagerImpl
			// which contains this min. if need this, uncomment the following
			// states
			// swmi.register(minthreadsconstraint);

			swmi.setMinThreadsConstraint(minthreadsconstraint);
		}
	}

	public void addWorkManagerComponent(String s, String wmName,
			RequestClass requestclass) {
		String key = constructMapKey(s, wmName);
		WorkManagerService wmservice = (WorkManagerService) workManagers
				.get(key);
		if (wmservice != null) {
			ServerWorkManagerImpl swmi = (ServerWorkManagerImpl) wmservice
					.getDelegate();
			swmi.setRequestClass((ServiceClassSupport) requestclass);
		}
	}

	/**
	 * 由workmanager name 和module name 构造存储workmanager的各Map的key
	 * 
	 * @param s
	 * @param wmName
	 * @return
	 */
	private String constructMapKey(String s, String wmName) {
		String key;
		key = (s == null) ? wmName : s + "@" + wmName;
		return key;
	}

	// create workmanager config content tree pass listener repository to it and
	// register WorkManagerConfig
	public void registerWorkManagerConfig(String appName, String moduleName,
			WmDD wmDD) {
		// WorkManagerConfigRegister wmcr = new
		// WorkManagerConfigRegister(appName,moduleName,url,listenerRepository);
		workManagerConfigRegister = new WorkManagerConfigRegister(appName,
				moduleName, wmDD);
		workManagerConfigRegister.createAndRegister();
	}

	/**
	 * 当在WorkManagerConfig中为wm添加组件，利用该方法为新建的组件注册监听器
	 * 
	 * @param obj
	 * @param wmName
	 * @param moduleName
	 */
	public void addListener(Object obj, String wmName, String moduleName) {
		if (obj instanceof MaxThreadsConstraint)
			addListener((MaxThreadsConstraint) obj, wmName, moduleName);
		else if (obj instanceof MinThreadsConstraint)
			addListener((MinThreadsConstraint) obj, wmName, moduleName);
		else if (obj instanceof RequestClass)
			addListener((RequestClass) obj, wmName, moduleName);
		else if (obj instanceof OverloadManager)
			addListener((OverloadManager) obj, wmName, moduleName);
		else
			throw new IllegalArgumentException(
					obj
							+ " is not a valid Object that implements WMUpdateListener, cannot be added as listenner !");
	}

	// overriding private method of addListener
	private void addListener(MaxThreadsConstraint maxthreadsconstraint,
			String wmName, String moduleName) {
		if (maxthreadsconstraint != null) {
			WMUpdateListenerName name = constructWMUListenerName(wmName,
					moduleName, maxthreadsconstraint.getName());
			listenerRepository.addWMUpdateListener(name, maxthreadsconstraint);
		}
	}

	private void removeListener(MaxThreadsConstraint maxthreadsconstraint,
			String wmName, String moduleName) {
		if (maxthreadsconstraint != null) {
			WMUpdateListenerName name = constructWMUListenerName(wmName,
					moduleName, maxthreadsconstraint.getName());
			listenerRepository.removeWMUpdateListener(name,
					maxthreadsconstraint);
		}

	}

	private void addListener(MinThreadsConstraint minthreadsconstraint,
			String wmName, String moduleName) {
		if (minthreadsconstraint != null) {
			WMUpdateListenerName name = constructWMUListenerName(wmName,
					moduleName, minthreadsconstraint.getName());
			listenerRepository.addWMUpdateListener(name, minthreadsconstraint);
		}
	}

	private void removeListener(MinThreadsConstraint minthreadsconstraint,
			String wmName, String moduleName) {
		if (minthreadsconstraint != null) {
			WMUpdateListenerName name = constructWMUListenerName(wmName,
					moduleName, minthreadsconstraint.getName());
			listenerRepository.removeWMUpdateListener(name,
					minthreadsconstraint);
		}
	}

	private void addListener(OverloadManager overloadmanager, String wmName,
			String moduleName) {
		if (overloadmanager != null) {
			WMUpdateListenerName name = constructWMUListenerName(wmName,
					moduleName, overloadmanager.getName());
			listenerRepository.addWMUpdateListener(name, overloadmanager);
		}
	}

	private void removeListener(OverloadManager overloadmanager, String wmName,
			String moduleName) {
		if (overloadmanager != null) {
			WMUpdateListenerName name = constructWMUListenerName(wmName,
					moduleName, overloadmanager.getName());
			listenerRepository.removeWMUpdateListener(name, overloadmanager);
		}
	}

	private void addListener(RequestClass requestclass, String wmName,
			String moduleName) {
		if (requestclass != null) {
			WMUpdateListenerName name = constructWMUListenerName(wmName,
					moduleName, requestclass.getName());
			if (requestclass instanceof ResponseTimeRequestClass)
				listenerRepository.addWMUpdateListener(name,
						(ResponseTimeRequestClass) requestclass);
			else if (requestclass instanceof FairShareRequestClass)
				listenerRepository.addWMUpdateListener(name,
						(FairShareRequestClass) requestclass);
			else
				throw new IllegalArgumentException(
						requestclass
								+ " is not a valid requetclass type that is allowed to add as  WMUpdateListener !");
		}
	}

	private void removeListener(RequestClass requestclass, String wmName,
			String moduleName) {
		if (requestclass != null
				&& ((requestclass instanceof ResponseTimeRequestClass) || (requestclass instanceof FairShareRequestClass))) {
			WMUpdateListenerName name = constructWMUListenerName(wmName,
					moduleName, requestclass.getName());
			if (requestclass instanceof ResponseTimeRequestClass)
				listenerRepository.removeWMUpdateListener(name,
						(ResponseTimeRequestClass) requestclass);
			else
				listenerRepository.removeWMUpdateListener(name,
						(FairShareRequestClass) requestclass);
		}
	}

	/**
	 * 创建WMUpdateListenerName
	 * 
	 * @param wmName
	 * @param moduleName
	 * @param componentName
	 * @return
	 */
	private WMUpdateListenerName constructWMUListenerName(String wmName,
			String moduleName, String componentName) {
		WMUpdateListenerName name = null;
		if (moduleName != null) {
			name = new WMUpdateListenerName(wmName, moduleName, componentName);
		} else {
			name = new WMUpdateListenerName(wmName, componentName);
		}
		return name;
	}

	// Notification related

	public void sendNotification(WMUpdateListenerName listenerName,
			WMUpdateEvent event) {
		listenerRepository.emmitWMUpdateEvent(listenerName, event);
	}

	// ------------------------------------------------------------------------------

	/**
	 * 
	 * @param moduleName
	 * @param wmName
	 * @param componentName
	 *            wm的组件名字，当type=WORKMANAGER_TYPE，次参数不起作用
	 * @param type
	 *            indicate which type (workmanager or workmanager components) is
	 *            to be unpopulated
	 */
	public void unPopulate(String moduleName, String componentName,
			String wmName, int type) {
		if (type == WORKMANAGER_TYPE) {
			unPopulate(moduleName, wmName);
		} else if (type == MAX_THTREADS_CONSTRAINT_TYPE) {
			unPopulateMaxThreadsConstraint(moduleName, componentName, wmName);
		} else if (type == MIN_THTREADS_CONSTRAINT_TYPE) {
			unPopulateMinThreadsConstraint(moduleName, componentName, wmName);
		} else if (type == OVERLOAD_MANAGER_TYPE) {
			unPopulateOverloadManager(moduleName, componentName, wmName);
		} else if (type == REQUEST_CLASS_TYPE) {
			unPopulateRequestClass(moduleName, componentName, wmName);
		}
	}

	/**
	 * :删除整个wm 对每一非空组件 1）删除给定wm的非空组件 2）将wm实例包含的非空组件域设置为null 3）移除listener
	 * 4）移除runtime
	 * 
	 * 5）当所有非空组件都完成1-4，最后从workManagers Map中删除wm
	 * 
	 * module name 到底是什么角色？ s - module name，moduleWorkmanagers 如何处理
	 */

	private void unPopulate(String s, String wmName) {
		ServerWorkManagerImpl wm = null;
		String key = constructMapKey(s, wmName);
		if (workManagers.containsKey(key)) {
			wm = (ServerWorkManagerImpl) ((WorkManagerService) workManagers
					.get(key)).getDelegate();
			unregisterWorkManagerRuntime(wm, s);
			if (wm.getMaxThreadsConstraint() != null) {
				unPopulateMaxThreadsConstraint(s, wm.getMaxThreadsConstraint()
						.getName(), wmName);
				wm.setMaxThreadsConstraint(null);
				// 在unpopulateMax时，已经removelistener故不需要下面语句
				// removeListener(wm.getMaxThreadsConstraint(), wmName, s);
			}

			if (wm.getMinThreadsConstraint() != null) {
				unPopulateMinThreadsConstraint(s, wm.getMinThreadsConstraint()
						.getName(), wmName);
				wm.setMinThreadsConstraint(null);
				// removeListener(wm.getMinThreadsConstraint(), wmName, s);
			}

			if (wm.getOverloadManager() != null) {
				unPopulateOverloadManager(s, wm.getOverloadManager().getName(),
						wmName);
				wm.setOverloadManager(null);
				// removeListener(wm.getOverloadManager(), wmName, s);
			}

			if (wm.getRequestClass() != null) {
				unPopulateRequestClass(s, wm.getRequestClass().getName(),
						wmName);
				wm.setRequestClass(null);
				// removeListener(wm.getRequestClass(), wmName, s);
			}

			// unpopulate 时，对moduleWorkmanagers的处理？？？？
			// if(s != null){
			// moduleWorkManagers.remove(s);
			// }

			// 删除ServerWorkManagerImpl与WorkManagerServiceImpl之间的相互引用,
			// populae时，StuckThreadManager
			// 设定了对WorkManagerServiceImpl的引用，所以应该需要下面的语句
			// wm.setWorkManagerService(null);
			// WorkManagerServiceImpl中的delegate是final，故不能set
			// ((WorkManagerServiceImpl)workManagers.get(key)).setDelegate(null);

			workManagers.remove(key);

			clearJNDIName(applicationName, wmName);
		}
	}

	/**
	 * 功能：将wmName下的名为componentName的max组件移除 1)移除单个组件注册的“额外”runtime
	 * 2)将仅由一个组件构成的wm的组件字段设置成null 3）将组件对应的wm从workManagers Map中移除 4）将组件从**Map中移除
	 * 5)该组件在所属wm中的对应字段设置成null 6）移除该组件对应的listener
	 * 
	 * @param s
	 * @param wmName
	 */

	private void unPopulateMaxThreadsConstraint(String s, String componentName,
			String wmName) {
		String key = constructMapKey(s, componentName);
		// 用来获得组件所属wm的key
		// String compsiteKey = constructMapKey(s, wmName);
		if (workManagers.containsKey(key)) {
			WorkManagerServiceImpl wmService = (WorkManagerServiceImpl) workManagers
					.get(key);
			ServerWorkManagerImpl wmImpl = (ServerWorkManagerImpl) wmService
					.getDelegate();

			// unregister runtime??
			unregisterChildWorkManagerRuntime(wmImpl);

			// remove listener
			removeListener(wmImpl.getMaxThreadsConstraint(), wmName, s);

			// process min queue

			// set max component in wm to null
			wmImpl.setMaxThreadsConstraint(null);

			// remove corresponding wm
			workManagers.remove(key);

			clearJNDIName(applicationName, componentName);
		}
		// remove this max constraint from wmName which contains it
		removeMaxThreadConstraintFromWM(s, wmName);
		maxMap.remove(key);
	}

	private void removeMaxThreadConstraintFromWM(String s, String wmName) {
		String key = constructMapKey(s, wmName);
		WorkManagerService wmservice = (WorkManagerService) workManagers
				.get(key);
		if (wmservice != null) {
			ServerWorkManagerImpl swmi = (ServerWorkManagerImpl) wmservice
					.getDelegate();
			swmi.setMaxThreadsConstraint(null);
		}
	}

	private void unPopulateMinThreadsConstraint(String s, String componentName,
			String wmName) {
		String key = constructMapKey(s, componentName);
		if (workManagers.containsKey(key)) {
			// set min component in wm to null
			WorkManagerServiceImpl wmService = (WorkManagerServiceImpl) workManagers
					.get(key);
			ServerWorkManagerImpl wmImpl = (ServerWorkManagerImpl) wmService
					.getDelegate();
			// unregister runtime?? twice
			unregisterChildWorkManagerRuntime(wmImpl);

			// remove listener
			removeListener(wmImpl.getMinThreadsConstraint(), wmName, s);

			// TODO process min queue

			// TODO unregist min in RequestManager's
			// minThreadsConstraints array registed when creating wm which
			// contains this min

			wmImpl.setMinThreadsConstraint(null);
			// remove corresponding wm
			workManagers.remove(key);

			clearJNDIName(applicationName, componentName);
		}
		// remove this min constraint from wmName which contains it
		removeMinThreadConstraintFromWM(s, wmName);
		minMap.remove(key);
	}

	private void removeMinThreadConstraintFromWM(String s, String wmName) {
		String key = constructMapKey(s, wmName);
		WorkManagerService wmservice = (WorkManagerService) workManagers
				.get(key);
		if (wmservice != null) {
			ServerWorkManagerImpl swmi = (ServerWorkManagerImpl) wmservice
					.getDelegate();
			swmi.setMinThreadsConstraint(null);
		}

	}

	private void unPopulateOverloadManager(String s, String componentName,
			String wmName) {
		String key = constructMapKey(s, componentName);
		if (workManagers.containsKey(key)) {
			// set overload component in wm to null
			WorkManagerServiceImpl wmService = (WorkManagerServiceImpl) workManagers
					.get(key);
			ServerWorkManagerImpl wmImpl = (ServerWorkManagerImpl) wmService
					.getDelegate();

			// unregister runtime?? comment because does not register its
			// runtime at all
			// unregisterChildWorkManagerRuntime(wmImpl);

			// remove listener
			removeListener(wmImpl.getOverloadManager(), wmName, s);

			wmImpl.setOverloadManager(null);
			// remove corresponding wm
			workManagers.remove(key);

			clearJNDIName(applicationName, componentName);
		}
		// remove this capacity constraint from wmName which contains it
		removeOverloadManagerFromWM(s, wmName);
		overloadMap.remove(key);
	}

	private void removeOverloadManagerFromWM(String s, String wmName) {
		String key = constructMapKey(s, wmName);
		WorkManagerService wmservice = (WorkManagerService) workManagers
				.get(key);
		if (wmservice != null) {
			ServerWorkManagerImpl swmi = (ServerWorkManagerImpl) wmservice
					.getDelegate();
			swmi.setOverloadManager(null);
		}

	}

	private void unPopulateRequestClass(String s, String componentName,
			String wmName) {
		String key = constructMapKey(s, componentName);
		if (workManagers.containsKey(key)) {
			// set requst class component in wm to null
			WorkManagerServiceImpl wmService = (WorkManagerServiceImpl) workManagers
					.get(key);
			ServerWorkManagerImpl wmImpl = (ServerWorkManagerImpl) wmService
					.getDelegate();

			// unregister runtime
			unregisterChildWorkManagerRuntime(wmImpl);

			// remove listener
			removeListener(wmImpl.getRequestClass(), wmName, s);

			// unregist request class in RequestManager registed when
			// constructing it
			// wmImpl.getRequestClass().cleanup(); // remove anyway
			wmImpl.cleanup(); // remove only when not shared. what's the
								// influence of shared rc???

			wmImpl.setRequestClass(null);
			// remove corresponding wm
			workManagers.remove(key);

			clearJNDIName(applicationName, componentName);
		}
		// remove this requestclass constraint from wmName which contains it
		removeRequestClassFromWM(s, wmName);
		requestClassMap.remove(key);
	}

	// unpopulate module workmanager ????

	// ----------------------------------------------------------------------------

	private void removeRequestClassFromWM(String s, String wmName) {
		String key = constructMapKey(s, wmName);
		WorkManagerService wmservice = (WorkManagerService) workManagers
				.get(key);
		if (wmservice != null) {
			ServerWorkManagerImpl swmi = (ServerWorkManagerImpl) wmservice
					.getDelegate();
			swmi.setRequestClass(null);
		}

	}

	// end by syk

	private StuckThreadManager getStuckThreadManager() {
		return getStuckThreadManager((WorkManagerBeanDD) null);
	}

	private StuckThreadManager getStuckThreadManager(
			WorkManagerBeanDD workManagerBeanDD) {
		WorkManagerShutdownAction workmanagershutdownaction = null;
		if (workManagerBeanDD != null) {
			if (workManagerBeanDD.getWorkManagerShutDownTriggerDD() == null)
				return new StuckThreadManager();
			WorkManagerShutDownTriggerDD workmanagershutdowntriggerDD = workManagerBeanDD
					.getWorkManagerShutDownTriggerDD();

			if (workmanagershutdowntriggerDD != null) {
				debug("Found WorkManagerShutdownTriggerDD with "
						+ workmanagershutdowntriggerDD.getMaxStuckThreadTime()
						+ " seconds and "
						+ workmanagershutdowntriggerDD.getStuckThreadCount()
						+ " count");
				workmanagershutdownaction = new WorkManagerShutdownAction(
						workmanagershutdowntriggerDD);
			}
		}
		if (workmanagershutdownaction == null && adminModeAction == null
				&& serverFailureAction == null) {
			debug("NO global ServerFailureAction found. No stuck thread action !");
			return null;
		} else {
			debug("Global ServerFailureAction FOUND. Creating StuckThreadManager !");
			/*
			 * return new StuckThreadManager(workmanagershutdownaction,
			 * adminModeAction, GlobalWorkManagerComponentsFactory
			 * .getInstance().getServerFailedAction());
			 */

			return new StuckThreadManager(workmanagershutdownaction,
					adminModeAction, serverFailureAction);
		}
	}

	private MaxThreadsConstraint getMaxConstraint(
			WorkManagerBeanDD workManagerBeanDD) {
		MaxThreadsConstraintDD maxThreadsConstrainDD = workManagerBeanDD
				.getMaxThreadsConstrain();
		if (maxThreadsConstrainDD != null) {
			// TODO PoolBasedMaxThreadsConstraint

			MaxThreadsConstraint maxthreadsconstraint = new MaxThreadsConstraint(
					maxThreadsConstrainDD.getName(), maxThreadsConstrainDD
							.getCount());
			// TODO 当dd更新时，需要通过事件及时更新相应的Bean;
			return maxthreadsconstraint;
		}
		return null;
	}

	private MinThreadsConstraint getMinConstraint(
			WorkManagerBeanDD workManagerBeanDD) {
		MinThreadsConstraintDD minthreadsconstraintDD = workManagerBeanDD
				.getMinThreadsConstrain();
		if (minthreadsconstraintDD != null) {
			MinThreadsConstraint minthreadsconstraint = new MinThreadsConstraint(
					minthreadsconstraintDD.getName(), minthreadsconstraintDD
							.getCount());
			// TODO 当dd更新时，需要通过事件及时更新相应的Bean;
			// ((DescriptorBean)maxthreadsconstraintbean).addBeanUpdateListener(maxthreadsconstraint);
			return minthreadsconstraint;
		}
		return null;
	}

	private OverloadManager getCapacity(WorkManagerBeanDD workManagerBeanDD) {
		CapacityConstraintDD capacityConstraintDD = workManagerBeanDD
				.getCapacityConstraint();
		if (capacityConstraintDD != null) {
			OverloadManager overloadManager = new OverloadManager(
					capacityConstraintDD.getName(), capacityConstraintDD
							.getCount());

			// TODO 当dd更新时，需要通过事件及时更新相应的Bean;
			// ((DescriptorBean)maxthreadsconstraintbean).addBeanUpdateListener(maxthreadsconstraint);
			return overloadManager;
		}
		return null;
	}

	private RequestClass getPolicy(WorkManagerBeanDD workmanagerbeanDD) {
		// fairshare ,response and context are mutex ???? by songyk
		FairShareRequestDD fairsharerequestDD = workmanagerbeanDD
				.getFairShareRequest();
		if (fairsharerequestDD != null) {
			RequestClass fairsharerequestclass = new FairShareRequestClass(
					fairsharerequestDD.getName(), fairsharerequestDD
							.getFairShare());
			// TODO listener
			// ((DescriptorBean) fairsharerequestclassbean)
			// .addBeanUpdateListener(fairsharerequestclass);

			return fairsharerequestclass;
		}

		ResponseTimeRequestDD responsetimerequestDD = workmanagerbeanDD
				.getResponseTimeRequest();
		if (responsetimerequestDD != null) {
			RequestClass responsetimerequestclass = new ResponseTimeRequestClass(
					responsetimerequestDD.getName(), responsetimerequestDD
							.getResponseTime());
			return responsetimerequestclass;
		}

		ContextRequestDD contextrequestclassDD = workmanagerbeanDD
				.getContextRequestClass();
		if (contextrequestclassDD != null) {
			ContextRequestClass contextrequestclass = new ContextRequestClass(
					contextrequestclassDD.getName());
			Iterator iterator = contextrequestclassDD.getContextCaseDDs().iterator();
			while (iterator.hasNext()) {
				ContextCaseDD contextDD = (ContextCaseDD) iterator.next();
//TODO 目前实现中，只能引用前面定义的requestClass，即先于ContextRequest完成解析 
				RequestClass requestclass = getRequestClass(contextDD
						.getRequestClassName());

				// if the target requestclass havent been loaded,then regist the
				// contextrequestclass and add user/group later
				if (requestclass == null) {
					registContextCase(contextDD);
					return contextrequestclass;
				}
				if (contextDD.getUserName() != null) {
					contextrequestclass.addUser(contextDD.getUserName(),
							requestclass);
					continue;
				}
				if (contextDD.getGroupName() != null) {
					contextrequestclass.addGroup(contextDD.getGroupName(),
							requestclass);
				}
			}
			return contextrequestclass;
		}
		return null;
	}

	private RequestClass getRequestClass(String s) {
		RequestClass requestclass = (RequestClass) requestClassMap.get(s);
		if (requestclass != null)
			return requestclass; // may only this be ok
		else
			return null;

		// return GlobalWorkManagerComponentsFactory.getInstance()
		// .findRequestClass(s);
	}

	private MaxThreadsConstraint getMaxConstraint(String s) {
		MaxThreadsConstraint maxthreadsconstraint = (MaxThreadsConstraint) maxMap
				.get(s);
		if (maxthreadsconstraint != null)
			return maxthreadsconstraint;
		return null;
	}

	private MinThreadsConstraint getMinConstraint(String s) {
		MinThreadsConstraint minthreadsconstraint = (MinThreadsConstraint) minMap
				.get(s);
		if (minthreadsconstraint != null)
			return minthreadsconstraint;
		return null;
	}

	private OverloadManager getOverload(String s) {
		OverloadManager overloadmanager = (OverloadManager) overloadMap.get(s);
		if (overloadmanager != null)
			return overloadmanager;
		return null;
	}

	private void setJNDIName(String appName, String wmName) {
		InitialContext initCtx;
		try {
			initCtx = new InitialContext();
			initCtx.bind(appName + "@" + wmName, new WorkManagerFactoryForJNDI(
					appName, wmName));
		} catch (NamingException e) {
			if (e instanceof NameAlreadyBoundException) {
				return;
			}
			e.printStackTrace();
		}
	}

	private void clearJNDIName(String appName, String wmName) {
		InitialContext initCtx;
		try {
			initCtx = new InitialContext();
			initCtx.unbind(appName + "@" + wmName);
		} catch (NamingException e) {
			if (e instanceof NameAlreadyBoundException) {
				return;
			}
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param s
	 *            module name
	 * @param s1
	 *            wm name
	 */
	public void registModuleWorkManager(String s, String s1) {
		System.out.println("==============================================================================");
		System.out.println("Registering Module WorkManager[moduleName=" + s + ",wmName=" + s1);
		if (s != null) {
			WorkManager wm = (WorkManager) workManagers.get(s1);
			System.out.println("Looked up the WorkManager[" + wm + "] for name[" + s1 +"]");
			if (wm != null) {
				//	add to assocaite wm with module. one wm may effect many modules, so the moduleName shoud be a set ?
				wm.setModuleName(s);
				System.out.println("Set moduleNmae["+s + "] for WM[" + wm + "].");
				moduleWorkManagers.put(s, wm);
				System.out.println("Puting wm[" +wm + "] using key=" +s );
			}
		}
		
		System.out.println("==============================================================================");
	}

	/**
	 * 
	 * @param s
	 *            module name
	 * @return
	 */
	public WorkManager getModuleWorkManager(String s) {
//		System.out.println("==============================================================================");
//		System.out.println("To getting ModuleWorkManager using key=" +s);
		if (s != null) {
			WorkManager wm = (WorkManager) moduleWorkManagers.get(s);
//			System.out.println("Looked up Module WorkManager[" +wm + "].");
			if (wm != null)
				return wm;
			
//			System.out.println("*****************************");
//			System.out.println("ModuleWorkManagers in map(moduleWorkManagers):");
//			for(String key : moduleWorkManagers.keySet()){
//				System.out.println("(key=" + key + ", value=" + moduleWorkManagers.get(key) + ").");
//			}
//			
//			System.out.println("WorkManagers in map(workManagers):");
//			for(String key : workManagers.keySet()){
//				System.out.println("(key=" + key + ", value=" + workManagers.get(key) + ").");
//			}
//			System.out.println("*****************************");
		} 
		
//		System.out.println("==============================================================================");
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public WorkManager getAppWorkManager() {
		return this.appWorkManager;
	}

	// by syk

	public WorkManager getDirectManager() {
		WorkManagerService direcwm = WorkManagerServiceImpl.createService(
				WorkManagerConstant.ONCEAS_DIRECT, applicationName, null, null,
				null, null, null, null);
		return direcwm;
	}

	// end
	/**
	 * @param s
	 *            workmanager name
	 * @return
	 */
	public WorkManager get(String s) {
		WorkManager workmanager = null;
		if (s != null) {
			workmanager = (WorkManager) workManagers.get(s);
			if (workmanager != null) {
				return workmanager;
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param s
	 *            module name
	 * @param s1
	 *            workmanager name
	 * @return
	 */
	public WorkManager get(String s, String s1) {
		WorkManager workmanager = null;
		if (s != null) {
			String s2 = s + "@" + s1;
			workmanager = (WorkManager) workManagers.get(s2);
			if (workmanager != null) {
				return workmanager;
			}
		}
		if (s1 != null) {
			workmanager = (WorkManager) workManagers.get(s1);
			if (workmanager != null) {
				return workmanager;
			}
			return null;
		} else {
			return null;
		}
	}

	public void removeModuleEntries(String s) {
		Iterator iterator1 = workManagers.keySet().iterator();
		do {
			if (!iterator1.hasNext())
				break;
			String s1 = (String) iterator1.next();
			if (s1.startsWith(s + "@"))
				iterator1.remove();
		} while (true);
	}

	public List getWorkManagers(String s) {
		if (s == null)
			return Collections.EMPTY_LIST;
		ArrayList arraylist = new ArrayList();
		Iterator iterator1 = workManagers.keySet().iterator();
		do {
			if (!iterator1.hasNext())
				break;
			String s1 = (String) iterator1.next();
			if (s1.startsWith(s + "@"))
				arraylist.add(workManagers.get(s1));
		} while (true);
		return arraylist;
	}

	public WorkManager getDefault() {
		return (WorkManager) workManagers
				.get(WorkManagerConstant.ONCEAS_KERNEL_DEFAULT);
	}

	private void registWorkManagerRuntime(WorkManager workmanager) {
		ServerWorkManagerImpl serverWorkManagerImpl = (ServerWorkManagerImpl) workmanager;
		com.onceas.runtime.work.WorkManagerRuntimeMBean workmanagerruntimembean;
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		try {
			workmanagerruntimembean = WorkManagerRuntime
					.creatWorkManagerRuntimeMBean(serverWorkManagerImpl, null);
			// check whether this object has already registered
			if (server.isRegistered(workmanagerruntimembean.getObjectName())) {
				try {
					server.unregisterMBean(workmanagerruntimembean
							.getObjectName());
				} catch (InstanceNotFoundException e) {
					// e.printStackTrace();
				}
			}
			server.registerMBean(workmanagerruntimembean,
					workmanagerruntimembean.getObjectName());

			runtimeMBeanObjectNames
					.add(workmanagerruntimembean.getObjectName());

			// store workmanagerruntimembean :by syk
			workManagerRuntimeMap.put(workmanagerruntimembean.getObjectName(),
					workmanagerruntimembean);

			// regist children runtime
			// 此处，仅仅用name =
			// serverWorkManagerImpl.getMaxThreadsConstraint().getName()做键去检索workManagers
			// 漏掉了s ！= null的情况，如果与前面保持一致，应该传进s参数，根据s的情况构造key。但为什么没有呢？
			// 所以问题还是对于module的理解，module到底应用到啥场景？？ 不理解 by syk
			if (serverWorkManagerImpl.getMaxThreadsConstraint() != null) {
				WorkManagerService workmanagerservice = (WorkManagerService) workManagers
						.get(serverWorkManagerImpl.getMaxThreadsConstraint()
								.getName());
				if (workmanagerservice != null) {
					registChildWorkManagerRuntime(workmanagerservice
							.getDelegate(), workmanagerruntimembean);
				}
			}
			if (serverWorkManagerImpl.getMinThreadsConstraint() != null) {
				WorkManagerService workmanagerservice = (WorkManagerService) workManagers
						.get(serverWorkManagerImpl.getMinThreadsConstraint()
								.getName());
				if (workmanagerservice != null) {
					registChildWorkManagerRuntime(workmanagerservice
							.getDelegate(), workmanagerruntimembean);
				}
			}
			if (serverWorkManagerImpl.getRequestClass() != null) {
				WorkManagerService workmanagerservice = (WorkManagerService) workManagers
						.get(serverWorkManagerImpl.getRequestClass().getName());
				// 不是系统自动添加的默认fairshare request class
				if (workmanagerservice != null) {
					registChildWorkManagerRuntime(workmanagerservice
							.getDelegate(), workmanagerruntimembean);
				}
			}
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}
	}

	private void registChildWorkManagerRuntime(WorkManager workmanager,
			RuntimeMBean parent) {
		ServerWorkManagerImpl serverWorkManagerImpl = (ServerWorkManagerImpl) workmanager;
		com.onceas.runtime.work.WorkManagerRuntimeMBean workmanagerruntimembean;
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		try {
			workmanagerruntimembean = WorkManagerRuntime
					.creatWorkManagerRuntimeMBean(serverWorkManagerImpl, parent);
			server.registerMBean(workmanagerruntimembean,
					workmanagerruntimembean.getObjectName());
			runtimeMBeanObjectNames
					.add(workmanagerruntimembean.getObjectName());
			// by syk
			workManagerRuntimeMap.put(workmanagerruntimembean.getObjectName(),
					workmanagerruntimembean);
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}

	}

	// : begin by syk
	/**
	 * 该方法存在原因是，populate单个wm组件过程中，并没有为该组件注册child runtime 。
	 * 当WorkManagerConfig中为wm添加组件，并populate新建组件后，需要调用此方法为新建组件注册runtime。
	 * 
	 * componentName - 用于到workManagers map中获得
	 * 新建组件populate得到的workManagerServiceImpl parentName -
	 * componentName隶属的wm名字，用于到workManagers map中获得 parent
	 * runtime对应的wm，进而构造其object name，获得parent runtime
	 */
	public void registChildWorkManagerRuntime(String moduleName,
			String componentName, String parentWMName) {
		String componentKey = constructMapKey(moduleName, componentName);
		String parentKey = constructMapKey(moduleName, parentWMName);
		WorkManagerService cwm = (WorkManagerService) workManagers
				.get(componentKey);
		if (cwm != null) {
			ServerWorkManagerImpl cwmImpl = (ServerWorkManagerImpl) cwm
					.getDelegate();

			// to get parent wm runtime
			WorkManagerService pwm = (WorkManagerService) workManagers
					.get(parentKey);
			if (pwm != null) {
				ServerWorkManagerImpl pwmImpl = (ServerWorkManagerImpl) pwm
						.getDelegate();
				RuntimeMBean parentRuntime = workManagerRuntimeMap
						.get(constructRumtimeObjectName(pwmImpl));

				registChildWorkManagerRuntime(cwmImpl, parentRuntime);
			}
		}
	}

	// unregister runtime used by deleting workmanager methods.
	/**
	 * 
	 * s module name
	 */
	public void unregisterWorkManagerRuntime(WorkManager workmanager, String s) {
		if (workmanager == null)
			throw new IllegalArgumentException(
					"the argument must not null in 'WorkManagerCollection.unregisterWorkManagerRuntime(WorkManager)'!");
		ServerWorkManagerImpl wmImpl = (ServerWorkManagerImpl) workmanager;
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		ObjectName wmRuntimeName = null;
		wmRuntimeName = constructRumtimeObjectName(wmImpl);
		//
		WorkManagerRuntimeMBean wmruntimembean = workManagerRuntimeMap
				.get(wmRuntimeName);

		if (wmruntimembean != null) {// exists ,so delete the runtime
			// set subruntime fields in workmanagerruntime null (only 3
			// constraints) necessary ????? may be not!
			// 那样的话，也不需要workManagerRuntimeMap了
			if (wmruntimembean.getMaxThreadsConstraintRuntime() != null)
				wmruntimembean.setMaxThreadsConstraintRuntime(null);
			if (wmruntimembean.getMinThreadsConstraintRuntime() != null)
				wmruntimembean.setMinThreadsConstraintRuntime(null);
			if (wmruntimembean.getRequestClassRuntime() != null)
				wmruntimembean.setRequestClassRuntime(null);

			// remove corresponding item in runtimeMBeanObjectNames &
			// workManagerRuntimeMap
			runtimeMBeanObjectNames.remove(wmRuntimeName);
			workManagerRuntimeMap.remove(wmRuntimeName);
			wmruntimembean = null; // help GC

			// unregister from MBeanServer
			try {
				server.unregisterMBean(wmRuntimeName);
			} catch (InstanceNotFoundException e) {
				e.printStackTrace();
			} catch (MBeanRegistrationException e) {
				e.printStackTrace();
			}

			// unregister subruntime generated in addition
			if (wmImpl.getMaxThreadsConstraint() != null) {
				String key = constructMapKey(s, wmImpl
						.getMaxThreadsConstraint().getName());
				// 要求workManagers的workmanager应在unregister runtime之后移除
				WorkManagerService wmservice = (WorkManagerService) workManagers
						.get(key);
				if (wmservice != null) {
					unregisterChildWorkManagerRuntime(wmservice.getDelegate());
				}
			}
			if (wmImpl.getMinThreadsConstraint() != null) {
				String key = constructMapKey(s, wmImpl
						.getMinThreadsConstraint().getName());
				WorkManagerService wmservice = (WorkManagerService) workManagers
						.get(key);
				if (wmservice != null) {
					unregisterChildWorkManagerRuntime(wmservice.getDelegate());
				}
			}
			if (wmImpl.getRequestClass() != null) {
				String key = constructMapKey(s, wmImpl.getRequestClass()
						.getName());
				WorkManagerService wmservice = (WorkManagerService) workManagers
						.get(key);
				if (wmservice != null) {
					unregisterChildWorkManagerRuntime(wmservice.getDelegate());
				}
			}
		} else {
			// does not exist the corresponding runtime
		}
	}

	private ObjectName constructRumtimeObjectName(WorkManager wm) {
		ObjectName wmRuntimeName = null;
		try {
			ServerWorkManagerImpl wmImpl = (ServerWorkManagerImpl) wm;
			wmRuntimeName = new ObjectName(
					Constant.WORK_MANAGER_RUNTIME_MBEAN_DOMAIN + ":"
							+ Constant.RUNTIME_TYPE + "="
							+ wmImpl.getRuntimeType() + ","
							+ Constant.CONTEXT_APP_NAME + "="
							+ wmImpl.getApplicationName() + ","
							+ Constant.CONTEXT_MODULE_NAME + "="
							+ wmImpl.getModuleName() + ","
							+ Constant.WORK_MANAGER_NAME + "="
							+ wmImpl.getName());
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return wmRuntimeName;
	}

	private void unregisterChildWorkManagerRuntime(WorkManager workmanager) {

		if (workmanager == null)
			throw new IllegalArgumentException(
					"the argument must not null in 'WorkManagerCollection.unregisterChildWorkManagerRuntime(WorkManager)'!");
		ServerWorkManagerImpl wmImpl = (ServerWorkManagerImpl) workmanager;
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();

		ObjectName wmRuntimeName = constructRumtimeObjectName(wmImpl);

		//
		WorkManagerRuntimeMBean wmruntimembean = workManagerRuntimeMap
				.get(wmRuntimeName);
		// set subruntime fields in workmanagerruntime null (only one case can
		// happen )
		if (wmruntimembean != null) {
			if (wmruntimembean.getMaxThreadsConstraintRuntime() != null)
				wmruntimembean.setMaxThreadsConstraintRuntime(null);
			else if (wmruntimembean.getMinThreadsConstraintRuntime() != null)
				wmruntimembean.setMinThreadsConstraintRuntime(null);
			else if (wmruntimembean.getRequestClassRuntime() != null)
				wmruntimembean.setRequestClassRuntime(null);

			// set parent null
			if (wmruntimembean.getParent() != null) {
				// ((WorkManagerRuntimeMBean)wmruntimembean.getParent()).
				wmruntimembean.setParent(null);
			}

			// remove corresponding item in runtimeMBeanObjectNames &
			// workManagerRuntimeMap
			runtimeMBeanObjectNames.remove(wmRuntimeName);
			workManagerRuntimeMap.remove(wmRuntimeName);
			wmruntimembean = null; // help GC

			// unregister from MBeanServer
			try {
				server.unregisterMBean(wmRuntimeName);
			} catch (InstanceNotFoundException e) {
				e.printStackTrace();
			} catch (MBeanRegistrationException e) {
				e.printStackTrace();
			}
		} else {
			// does not exist the runtime
		}
	}

	// end syk

	private static void debug(String s) {
		// if (debugWMCollection.isEnabled())
		if (DebugWM.debug_WMColl)
			WorkManagerLogger.logDebug("<WMCollection>" + s);
	}

	public Map getWorkManagerMap() {
		return workManagers;
	}

	public Map getMaxMap() {
		return this.maxMap;
	}

	public Map getMinMap() {
		return this.minMap;
	}

	public Map getOverloadMap() {
		return this.overloadMap;
	}

	public Map getRequestClassMap() {
		return this.requestClassMap;
	}

	public void destroy() {
		for (Object obj : this.getWorkManagerMap().keySet()) {
			String wmName = (String) obj;
			InitialContext initCtx;
			try {
				initCtx = new InitialContext();
				initCtx.unbind(this.applicationName + "@" + wmName);

			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		// unregist runtime mbean
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		for (ObjectName objName : this.runtimeMBeanObjectNames) {
			try {
				server.unregisterMBean(objName);
			} catch (InstanceNotFoundException e) {
				e.printStackTrace();
			} catch (MBeanRegistrationException e) {
				e.printStackTrace();
			}
		}
		// unregist workmanager config by syk
		workManagerConfigRegister.unregist();

	}

	private Map<String, List> contextCaseMap = new HashMap();

	/**
	 * 
	 */
	private void registContextCase(ContextCaseDD contextDD) {
		if (contextCaseMap.get(contextDD.getRequestClassName()) != null) {
			contextCaseMap.get(contextDD.getRequestClassName()).add(contextDD);
		} else {
			List<ContextCaseDD> contextCases = new ArrayList();
			contextCases.add(contextDD);
			contextCaseMap.put(contextDD.getRequestClassName(), contextCases);
		}
	}

	// 下面方法似乎混淆了context request class和 context case ？？？？？ by syk
	private void unregistContextCase(RequestClass requestClass) {
		if (contextCaseMap.get(requestClass.getName()) != null) {
			List<ContextCaseDD> contextCases = contextCaseMap.get(requestClass
					.getName());
			for (ContextCaseDD contextCase : contextCases) {
				if (requestClassMap.get(contextCase.getContextClassName()) != null) {
					ContextRequestClass contextRequestClass = (ContextRequestClass) requestClassMap
							.get(contextCase.getContextClassName());
					if (contextCase.getUserName() != null) {
						contextRequestClass.addUser(contextCase.getUserName(),
								requestClass);
						continue;
					}
					if (contextCase.getGroupName() != null) {
						contextRequestClass.addGroup(
								contextCase.getGroupName(), requestClass);
					}
				}
			}
		}
	}

	public void populateAppWorkManager(WorkManagerBeanDD workManagerBeanDD) {
		RequestClass requestclass = getPolicy(workManagerBeanDD);
		MinThreadsConstraint minthreadsconstraint = getMinConstraint(workManagerBeanDD);
		OverloadManager overloadmanager = getCapacity(workManagerBeanDD);

		StuckThreadManager stuckthreadmanager = getStuckThreadManager(workManagerBeanDD);
		MaxThreadsConstraint maxthreadsconstraint = getMaxConstraint(workManagerBeanDD);

		WorkManagerService workmanagerservice = WorkManagerServiceImpl
				.createService(workManagerBeanDD.getName(), applicationName,
						null, requestclass, maxthreadsconstraint,
						minthreadsconstraint, overloadmanager,
						stuckthreadmanager);
		if (stuckthreadmanager != null)
			stuckthreadmanager.setWorkManagerService(workmanagerservice);
		if (internal)
			workmanagerservice.setInternal();

		this.appWorkManager = workmanagerservice;

		registWorkManagerRuntime(workmanagerservice.getDelegate());
		setJNDIName(applicationName, workManagerBeanDD.getName());
	}

}