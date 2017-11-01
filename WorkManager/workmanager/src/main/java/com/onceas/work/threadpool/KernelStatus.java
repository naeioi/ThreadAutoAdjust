package com.onceas.work.threadpool;

import com.onceas.work.WorkManagerConstant;

public class KernelStatus {

	public KernelStatus() {
	}

	private static final boolean initDebug() {
		boolean flag = false;
		flag = System.getProperty("onceas.kernel.debug") != null;
		if ("true".equals(System.getProperty("java.class.version.applet"))
				|| "true".equals(System.getProperty("java.vendor.applet"))
				|| "true".equals(System.getProperty("java.version.applet"))) {
			isApplet = true;
			return false;
		}
		try {
			return flag;
		} catch (SecurityException securityexception) {
			isApplet = true;
		}
		return false;
	}

	public static boolean isApplet() {
		return isApplet;
	}

	public static void setIsServer(boolean flag) {
		isServer = flag;
	}

	public static boolean isServer() {
		return isServer;
	}

	static void initialized() {
		isInitialized = true;
	}

	public static boolean isInitialized() {
		return isInitialized;
	}

	public static final String DIRECT_DISPATCH = WorkManagerConstant.ONCEAS_DIRECT;

	public static final String DEFAULT_DISPATCH_ALIAS = "default";

	public static final String DEFAULT_DISPATCH = WorkManagerConstant.ONCEAS_KERNEL_DEFAULT;

	public static final String NON_BLOCKING_DISPATCH = WorkManagerConstant.ONCEAS_KERNEL_NON_BLOCKING;

	public static final String SYSTEM_DISPATCH = WorkManagerConstant.ONCEAS_KERNEL_SYSTEM;

	private static boolean isServer = false;

	private static boolean isApplet = false;

	private static boolean isInitialized = false;

	private static final String DEBUG_PROP = "onceas.kernel.debug";

	public static final boolean DEBUG = initDebug();

}
