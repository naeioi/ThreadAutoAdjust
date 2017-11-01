/**
 * TODO
 * 线程的上下文信息
 * 包括安全信息，ClassLoader信息
 */
package com.onceas.work.j2ee.remote;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;

import com.onceas.util.jmx.MBeanServerLoader;
import com.onceas.work.jndi.javaURLContextFactory;

public class RemoteThreadContext {
	// private static final AbstractSubject kernelId =
	// (AbstractSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
	// private final SubjectManager subjectManager =
	// SubjectManager.getSubjectManager();
	private ClassLoader classLoader;

	private ClassLoader savedClassLoader;

	private Context context;

	// private AbstractSubject subject;
	private int flags;

	private static final int SUBJECT_PUSHED = 1;

	private static final int CONTEXT_PUSHED = 2;

	private static final int CLASS_LOADER_PUSHED = 4;

	private static Map classLoaderMap = new ConcurrentHashMap();

	public static RemoteThreadContext getContext(String appName) {
		return new RemoteThreadContext(appName);
	}

	private RemoteThreadContext(String appName) {
		try {
			if (appName == null) {
				classLoader = Thread.currentThread().getContextClassLoader();
			} else if (classLoaderMap.containsKey(appName)) {
				classLoader = (ClassLoader) classLoaderMap.get(appName);
			} else {
				MBeanServer mserver = MBeanServerLoader.loadOnceas();
				String onStr1 = "Adam:J2EEApplication=none,J2EEServer=none,j2eeType=WebModule,name=//localhost"
						+ appName;
				ObjectName objectName1 = new ObjectName(onStr1);
				// comment for seperation of wm
//				WebappLoader webclassloader = (com.onceas.webcontainer.plugin.loader.WebappLoader) mserver
//						.getAttribute(objectName1, "loader");
//				classLoader = webclassloader.getClassLoader();
				classLoaderMap.put(appName, classLoader);
			}

			javaURLContextFactory javaurlcontextfactory = new javaURLContextFactory();
			context = (Context) javaurlcontextfactory.getObjectInstance(null,
					null, null, null);
		} catch (Exception ei) {
			ei.printStackTrace();
		}
	}

	/*
	 * public AbstractSubject getSubject() { return subject; }
	 */
	public void push() {
		if (flags != 0)
			throw new IllegalStateException();
		// subjectManager.pushSubject(kernelId, subject);
		flags |= SUBJECT_PUSHED;
		javaURLContextFactory.pushContext(context);
		flags |= CONTEXT_PUSHED;
		Thread thread = Thread.currentThread();
		savedClassLoader = thread.getContextClassLoader();
		thread.setContextClassLoader(classLoader);
		flags |= CLASS_LOADER_PUSHED;
	}

	public void pop() {
		if ((flags & SUBJECT_PUSHED) != 0) {
			// subjectManager.popSubject(kernelId);
			flags &= -CONTEXT_PUSHED;
		}
		if ((flags & CONTEXT_PUSHED) != 0) {
			javaURLContextFactory.popContext();
			flags &= -3;
		}
		if ((flags & CLASS_LOADER_PUSHED) != 0) {
			Thread.currentThread().setContextClassLoader(savedClassLoader);
			flags &= -5;
		}
	}
}
