package com.onceas.runtime.work;

import com.onceas.runtime.Runtime;
import com.onceas.runtime.RuntimeMBean;
import com.onceas.work.constraint.MinThreadsConstraint;

public final class MinThreadsConstraintRuntime extends Runtime implements
		MinThreadsConstraintRuntimeMBean {
	public static MinThreadsConstraintRuntimeMBean createMinThreadsConstraintRuntimeMBean(
			MinThreadsConstraint minthreadsconstraint, RuntimeMBean runtimembean) {
		if (minthreadsconstraint == null) {
			return null;
		}
		MinThreadsConstraintRuntimeMBean minThreadsConstraintRuntimeMBean = new MinThreadsConstraintRuntime(
				minthreadsconstraint, runtimembean);
		return minThreadsConstraintRuntimeMBean;
	}

	public MinThreadsConstraintRuntime(MinThreadsConstraint minthreadsconstraint) {
		super(minthreadsconstraint.getName());
		mtc = minthreadsconstraint;
	}

	public MinThreadsConstraintRuntime(
			MinThreadsConstraint minthreadsconstraint, RuntimeMBean runtimembean) {
		super(minthreadsconstraint.getName(), runtimembean);
		mtc = minthreadsconstraint;
	}

	public int getExecutingRequests() {
		return mtc.getExecutingCount();
	}

	public long getCompletedRequests() {
		return mtc.getCompletedCount();
	}

	public int getPendingRequests() {
		return mtc.getQueueSize();
	}

	public long getOutOfOrderExecutionCount() {
		return mtc.getOutOfOrderExecutionCount();
	}

	public int getMustRunCount() {
		return mtc.getMustRunCount();
	}

	public long getMaxWaitTime() {
		return mtc.getMaxWaitTime();
	}

	public long getCurrentWaitTime() {
		return mtc.getCurrentWaitTime();
	}

	private final MinThreadsConstraint mtc;
}
