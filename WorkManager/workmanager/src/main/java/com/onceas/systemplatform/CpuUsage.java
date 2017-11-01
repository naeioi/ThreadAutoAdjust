package com.onceas.systemplatform;

public abstract class CpuUsage {
	protected static final int CPUTIME = 2000;

	protected static final int PERCENT = 100;

	protected static final int FAULTLENGTH = 10;
	
	public abstract double getCpuRatio();
}
