package com.onceas.work.threadpool;

public class AuditableThreadLocalFactory {

	public AuditableThreadLocalFactory() {
	}

	public static AuditableThreadLocal createThreadLocal() {
		return createThreadLocal(new ThreadLocalInitialValue());
	}

	public static AuditableThreadLocal createThreadLocal(
			ThreadLocalInitialValue threadlocalinitialvalue) {
		if (KernelStatus.isServer() && !FinalThreadLocal.isFinalized())
			return new FinalThreadLocal(threadlocalinitialvalue);
		else
			return new ResettableThreadLocal(threadlocalinitialvalue);
	}
}
