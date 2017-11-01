package com.onceas.work.threadpool;

public interface ThreadStorage {

	public abstract Object get(int i);

	public abstract void reset();

	public abstract void set(int i, Object obj);

	public static final Object UNINITIALIZED = new Object();

}
