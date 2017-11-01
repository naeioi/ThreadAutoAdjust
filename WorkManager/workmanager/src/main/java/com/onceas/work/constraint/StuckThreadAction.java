package com.onceas.work.constraint;

public interface StuckThreadAction {

	public abstract boolean threadStuck(int i, long l, long l1);

	public abstract void threadUnStuck(int i);

	public abstract int getStuckThreadCount();

	public abstract void execute();

	public abstract void withdraw();
}
