package com.onceas.work.threadpool;

import com.onceas.work.WorkManagerConstant;

//×´Ì¬Àà
public class AppletStatus {

	public static final String DIRECT_DISPATCH = WorkManagerConstant.ONCEAS_DIRECT;

	public static final String DEFAULT_DISPATCH_ALIAS = "default";

	public static final String DEFAULT_DISPATCH = WorkManagerConstant.ONCEAS_KERNEL_DEFAULT;

	public static final String NON_BLOCKING_DISPATCH = WorkManagerConstant.ONCEAS_KERNEL_NON_BLOCKING;

	public static final String SYSTEM_DISPATCH = WorkManagerConstant.ONCEAS_KERNEL_SYSTEM;

	private static boolean isApplet = false;

	public AppletStatus() {
	}

	public static boolean isApplet() {
		if ("true".equals(System.getProperty("java.class.version.applet"))
				|| "true".equals(System.getProperty("java.vendor.applet"))
				|| "true".equals(System.getProperty("java.version.applet"))) {
			isApplet = true;
		}
		return isApplet;
	}
}
