package com.onceas.runtime.work;

import com.onceas.runtime.Runtime;
import com.onceas.runtime.RuntimeMBean;
import com.onceas.work.constraint.FairShareRequestClass;
import com.onceas.work.constraint.RequestClass;
import com.onceas.work.constraint.ResponseTimeRequestClass;
import com.onceas.work.constraint.ServiceClassSupport;

public final class RequestClassRuntime extends Runtime implements
		RequestClassRuntimeMBean {
	public static RequestClassRuntimeMBean createRequestClassRuntimeMBean(
			RequestClass requestClass, RuntimeMBean runtimembean) {
		if (requestClass == null) {
			return null;
		}
		RequestClassRuntimeMBean requestClassRuntimeMBean = new RequestClassRuntime(
				requestClass, runtimembean);
		return requestClassRuntimeMBean;
	}

	public RequestClassRuntime(RequestClass requestclass) {
		super(requestclass.getName());
		rc = (ServiceClassSupport) requestclass;
	}

	public RequestClassRuntime(RequestClass requestclass,
			RuntimeMBean runtimembean) {
		super(requestclass.getName(), runtimembean);
		rc = (ServiceClassSupport) requestclass;
	}

	public String getRequestClassType() {
		if (rc instanceof FairShareRequestClass)
			return "fairshare";
		if (rc instanceof ResponseTimeRequestClass)
			return "responsetime";
		else
			return null;
	}

	public long getCompletedCount() {
		return (long) rc.getCompleted();
	}

	public long getTotalThreadUse() {
		return rc.getThreadUse();
	}

	public long getThreadUseSquares() {
		return rc.getThreadUseSquares();
	}

	public long getDeltaFirst() {
		return rc.getDeltaFirst();
	}

	public long getDeltaRepeat() {
		return rc.getDelta();
	}

	public long getMyLast() {
		return rc.getMyLast();
	}

	public int getPendingRequestCount() {
		return rc.getPendingRequestsCount();
	}

	public long getVirtualTimeIncrement() {
		return rc.getVirtualTimeIncrement();
	}

	public double getInterval() {
		if (!(rc instanceof ResponseTimeRequestClass))
			return -1D;
		else
			return ((ResponseTimeRequestClass) rc).getInterval();
	}

	private final ServiceClassSupport rc;
}
