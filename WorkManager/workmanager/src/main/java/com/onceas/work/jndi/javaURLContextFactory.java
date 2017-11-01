package com.onceas.work.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import com.onceas.work.threadpool.ThreadLocalStack;

public final class javaURLContextFactory implements ObjectFactory {
	private static class JavaURLContext extends AbstractURLContext {

		protected String removeURL(String s) throws InvalidNameException {
			if (s.startsWith("java:comp/"))
				return s.substring(5);
			else
				return super.removeURL(s);
		}

		protected Context getContext(String s) throws NamingException {
			return actualCtx;
		}

		private Context actualCtx;

		public JavaURLContext(Context context, Hashtable hashtable) {
			actualCtx = context;
		}
	}

	public javaURLContextFactory() {
	}

	public static void pushContext(Context context) {
		threadContext.push(context);
	}

	public static void popContext() {
		threadContext.pop();
	}

	public Object getObjectInstance(Object obj, Name name, Context context,
			Hashtable<?, ?> hashtable) throws NamingException {
		Context context1 = (Context) threadContext.peek();
		if (context1 == null)
			context1 = DEFAULT_CONTEXT;
		ReadOnlyContextWrapper readonlycontextwrapper = new ReadOnlyContextWrapper(
				context1);
		JavaURLContext javaurlcontext = new JavaURLContext(
				(Context) readonlycontextwrapper, hashtable);
		return javaurlcontext;
	}

	private static final Context DEFAULT_CONTEXT = null;

	private static ThreadLocalStack threadContext = new ThreadLocalStack(true);

}
