package com.onceas.aspect.connector;

import com.onceas.work.InheritableThreadContext;
import com.onceas.work.Work;
/**
 * internal wrapper work
 *
 */
public abstract class RequestHandleWork implements Work {

	private InheritableThreadContext threadContext = null;
	private boolean isSignal = false;
	private RequestContext requestContext = null;

	public abstract Object executeRequest();
	
	public RequestHandleWork(RequestContext requestContext){
		this.requestContext = requestContext;
		this.threadContext = InheritableThreadContext.getContext();
	}

	public void run() {
		if (threadContext != null)
			threadContext.push();
		
		this.executeRequest();
		
		if (!isSignal) {
			this.signal();
		}
		if (threadContext != null)
			threadContext.pop();

	}

	private synchronized void setSignal(boolean flag) {
		isSignal = flag;
	}

	private void signal() {
		this.requestContext.lock.lock();
		try {
			this.requestContext.workComplete.signal();
			setSignal(true);
		} finally {
			this.requestContext.lock.unlock();
		}
	}

	public Runnable cancel(String s) {
		return null;
		/**
		 return new Runnable() {
		 public void run() {
		 signal();
		 }
		 };
		 */
	}

	public Runnable overloadAction(String s) {
		return new Runnable() {
			public void run() {
				signal();
			}
		};
	}

	public boolean isDaemon() {
		return false;
	}
}
