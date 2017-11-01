package com.onceas.runtime;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.onceas.runtime.work.ThreadPoolRuntimeMBean;

public final class RuntimeAccess {
	private static final class RuntimeAccessSingleton {
		private static final RuntimeAccess SINGLETON = new RuntimeAccess();

		private RuntimeAccessSingleton() {
		}
	}

	private RuntimeAccess() {
	}

	public static RuntimeAccess getRuntimeAccess() {
		return RuntimeAccessSingleton.SINGLETON;
	}

	public ThreadPoolRuntimeMBean getThreadPoolRuntime() {
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		ObjectName objName;
		try {
			objName = new ObjectName(
					com.onceas.runtime.work.Constant.WORK_MANAGER_RUNTIME_MBEAN_DOMAIN
							+ ":"
							+ com.onceas.runtime.work.Constant.RUNTIME_TYPE
							+ "="
							+ com.onceas.runtime.work.Constant.THREAD_POOL_TYPE
							+ ","
							+ com.onceas.runtime.work.Constant.THREAD_POOL_RUNTIME
							+ "="
							+ com.onceas.runtime.work.Constant.ONCEAS_THREAD_POOL_RUNTIME);
			return (ThreadPoolRuntimeMBean) server.invoke(objName,
					"getRuntimeMBean", null, null);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (MBeanException e) {
			e.printStackTrace();
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
