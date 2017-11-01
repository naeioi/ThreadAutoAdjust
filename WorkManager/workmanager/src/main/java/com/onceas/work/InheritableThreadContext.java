/**
 * TODO
 * 线程的上下文信息
 * 包括安全信息，ClassLoader信息
 */
package com.onceas.work;

import java.security.Principal;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.security.auth.Subject;



import com.onceas.util.security.SecurityContext;
import com.onceas.work.jndi.javaURLContextFactory;
import com.onceas.work.ThreadLocalSnapshot;

public class InheritableThreadContext {
	private ClassLoader classLoader;

	private ClassLoader savedClassLoader;

	private Context context;

	private Subject subject;

	private Principal userPrincipal;

	private int flags;

	private static final int SUBJECT_PUSHED = 1;

	private static final int CONTEXT_PUSHED = 2;

	private static final int CLASS_LOADER_PUSHED = 4;
	
	private static final int THREAD_LOCAL_PUSHED = 8;
	
	/*  For backup threadlocals in the current thread,
	 * then restore these threadlocals in the worker thread. 
	 * Thread switches without propagating thread locals variable will cause context lost
	 * such as ActionContext lost in Struts web framework.
	 * 
	 * : add by syk*/
	
	private Map<ThreadLocal,Object> threadLocalSnapshots = null;
	/* end syk*/

	public static InheritableThreadContext getContext() {
		return new InheritableThreadContext();
	}

	private InheritableThreadContext() {
		subject = SecurityContext.getSubject();
		userPrincipal = SecurityContext.getPrincipal();
		classLoader = Thread.currentThread().getContextClassLoader();
		try {
			javaURLContextFactory javaurlcontextfactory = new javaURLContextFactory();
			context = (Context) javaurlcontextfactory.getObjectInstance(null,
					null, null, null);
		} catch (NamingException namingexception) {
		}
		//save threadlocals 
		threadLocalSnapshots = ThreadLocalSnapshot.take(Thread.currentThread());
	}

	/*
	 * public AbstractSubject getSubject() { return subject; }
	 */
	public void push() {
		if (flags != 0)
			throw new IllegalStateException();
		SecurityContext.setSubject(subject);
		SecurityContext.setPrincipal(userPrincipal);
		flags |= SUBJECT_PUSHED;
		javaURLContextFactory.pushContext(context);
		flags |= CONTEXT_PUSHED;
		Thread thread = Thread.currentThread();
		savedClassLoader = thread.getContextClassLoader();
		thread.setContextClassLoader(classLoader);
		flags |= CLASS_LOADER_PUSHED;
		
		if(threadLocalSnapshots != null && !threadLocalSnapshots.isEmpty()){
			ThreadLocalSnapshot.set(threadLocalSnapshots);
			flags |= THREAD_LOCAL_PUSHED;
		}
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
		//pop threadlocals
		if((flags & THREAD_LOCAL_PUSHED) != 0){
			ThreadLocalSnapshot.clear(threadLocalSnapshots.keySet());
			flags &= -9;
		}
	}
}
