package com.onceas.timers.internal;

import javax.naming.Context;
import javax.naming.NamingException;

import com.onceas.work.jndi.javaURLContextFactory;

class TimerContext {
	TimerContext() {
		classLoader = Thread.currentThread().getContextClassLoader();
		try {
			javaURLContextFactory javaurlcontextfactory = new javaURLContextFactory();
			context = (Context) javaurlcontextfactory.getObjectInstance(null,
					null, null, null);
		} catch (NamingException namingexception) {
		}
	}

	void push() {
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

	void pop() {
		if ((flags & SUBJECT_PUSHED) != 0) {
			// subjectManager.popSubject(kernelId);
			flags &= -2;
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

	// private static final AbstractSubject kernelId = getKernelIdentity();
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

}
