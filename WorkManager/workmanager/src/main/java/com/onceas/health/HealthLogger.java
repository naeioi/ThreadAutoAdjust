package com.onceas.health;

import java.util.logging.Logger;



public class HealthLogger extends com.onceas.logging.AsynchronousLogger {

	static HealthLogger temp;

	public HealthLogger() {
		log = Logger.getLogger(getClass().getName());
	}

	protected static void initial() {
		if (temp == null) {
			temp = new HealthLogger();
		}
	}

	public static void logDebugMsg(String s) {
		initial();
		temp.info(s);
	}

	public static void logErrorSubsystemFailed(String s) {
		initial();
		temp
				.info(Messages.getString("HealthLogger.1") + s + Messages.getString("HealthLogger.2")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void logDeadlockedThreads(String s) {
		initial();
		temp.info(Messages.getString("HealthLogger.3") + s); //$NON-NLS-1$
	}

	public static void logFreeMemoryChanged(int i) {
		initial();
		temp.info(Messages.getString("HealthLogger.4") + i); //$NON-NLS-1$
	}

	public static void logOOMEImminent(long l) {
		initial();
		temp.info(Messages.getString("HealthLogger.5") + l); //$NON-NLS-1$
	}

	public static void logMemoryMonitorStarted(long l) {
		initial();
		temp.info(Messages.getString("HealthLogger.6") + l); //$NON-NLS-1$
	}

	public static void logMemoryMonitorStopped(Exception exception) {
		initial();
		temp.info(Messages.getString("HealthLogger.7"), exception); //$NON-NLS-1$
	}

	public static void logErrorSubsystemFailedWithReason(String s, String s1) {
		initial();
		temp
				.info(Messages.getString("HealthLogger.8") + s + Messages.getString("HealthLogger.9") + s1); //$NON-NLS-1$ //$NON-NLS-2$

	}

	public static void logWarnPossibleStuckThread(String name, long l,
			String currentRequest, long m, String s1) {
		initial();
		temp.info(s1);
	}

	public static void logLowMemory(String s) {
		initial();
		temp.info(s);
	}

}
