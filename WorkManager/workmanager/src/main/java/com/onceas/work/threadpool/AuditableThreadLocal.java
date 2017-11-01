package com.onceas.work.threadpool;

public interface AuditableThreadLocal {

	public abstract Object get();

	public abstract Object get(AuditableThread auditablethread);

	public abstract void set(Object obj);
}
