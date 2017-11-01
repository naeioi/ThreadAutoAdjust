package com.onceas.runtime.work;

import com.onceas.runtime.Runtime;
import com.onceas.runtime.RuntimeMBean;
import com.onceas.work.constraint.MaxThreadsConstraint;

public final class MaxThreadsConstraintRuntime extends Runtime implements
		MaxThreadsConstraintRuntimeMBean {
	public static MaxThreadsConstraintRuntimeMBean createMaxThreadsConstraintRuntimeMBean(
			MaxThreadsConstraint maxthreadsconstraint, RuntimeMBean runtimembean) {
		if (maxthreadsconstraint == null) {
			return null;
		}
		MaxThreadsConstraintRuntimeMBean maxThreadsConstraintRuntimeMBean = new MaxThreadsConstraintRuntime(
				maxthreadsconstraint, runtimembean);
		return maxThreadsConstraintRuntimeMBean;
	}

	public MaxThreadsConstraintRuntime(MaxThreadsConstraint maxthreadsconstraint) {
		super(maxthreadsconstraint.getName());
		mtc = maxthreadsconstraint;
	}

	public MaxThreadsConstraintRuntime(
			MaxThreadsConstraint maxthreadsconstraint, RuntimeMBean runtimembean) {
		super(maxthreadsconstraint.getName(), runtimembean);
		mtc = maxthreadsconstraint;
	}

	public int getExecutingRequests() {
		return mtc.getExecutingCount();
	}

	public int getDeferredRequests() {
		return mtc.getQueueSize();
	}

	private final MaxThreadsConstraint mtc;
}
