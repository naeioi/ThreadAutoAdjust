package com.onceas.work;

import java.util.logging.Logger;

public class WorkManagerLogger {
	private static final String LOCALIZER_CLASS = "OnceAS.i18n.WorkManagerLogLocalizer";

	public static final Logger log = Logger.getLogger(WorkManagerLogger.class.toString());

	public WorkManagerLogger() {

	}

	public static String logInitializingSelfTuning() {
		Object aobj[] = new Object[0];
		return "002900";
	}

	/**
	 * @return Loggable
	 */
	public static Logger logInitializingSelfTuningLoggable() {
		Object aobj[] = new Object[0];

		log.info("002900" + "OnceAS.i18n.WorkManagerLogLocalizer");
		return log;
	}

	public static String logCreatingWorkManagerService(String s, String s1,
			String s2) {
		log.info("002901  " + s + " " + s1 + "  " + s2
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002901";
	}

	public static String logCreatingExecuteQueueFromMBean(String s) {
		log.info("002902  " + s + "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002902";
	}

	public static String logCreatingServiceFromMBean(String s, String s1) {
		log.info("002903  " + s + "  " + s1
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002903";
	}

	public static String logIncreasingThreads(int i, int j) {
		log.info("002904  " + i + "  " + j
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002904";
	}

	public static String logDecreasingThreads(int i) {
		log.info("002905  " + i + "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002905";
	}

	public static String logHogger(String s, long l, String s1, long l1) {
		log.info("002906  " + s + "  " + l + "   " + s1 + "  " + 11
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002906";
	}

	public static String logThreadNoLongerHogs(String s) {
		log.info("002907  " + s + "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002907";
	}

	public static String logRuntimeMBeanCreationError(String s,
			Throwable throwable) {
		log.info("002908  " + s + "  OnceAS.i18n.WorkManagerLogLocalizer"+"\n"+
				throwable);

		return "002908";
	}

	public static String logDebug(String s) {
		log.info("002909  " + s + "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002909";
	}

	public static String logIncreasingThreadsForWM(String s, int i, int j) {
		log.info("002910  " + s + "  " + i + " " + j
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002910";
	}

	public static String logScheduleFailed(String s, Throwable throwable) {
		log.info("002911  " + s + "  OnceAS.i18n.WorkManagerLogLocalizer"+"\n"+
				throwable);

		return "002911";
	}

	public static Logger logOverloadActionLoggable(String s, int i, int j) {
		Object aobj[] = { s, new Integer(i), new Integer(j) };
		log.info("002912  " + s + " " + i + " " + j
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");
		return log;
	}

	public static String logOverloadAction(String s, int i, int j) {
		log.info("002912  " + s + " " + i + " " + j
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002912";
	}

	public static Logger logLowMemoryLoggable(String s) {
		Object aobj[] = { s };
		log.info("002913  " + s + "  OnceAS.i18n.WorkManagerLogLocalizer");
		return log;
	}

	public static String logLowMemory(String s) {
		log.info("002913  " + s + "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002913";
	}

	public static String logShutdownCallbackFailed(Throwable throwable) {
		log.info("002914  " + "  OnceAS.i18n.WorkManagerLogLocalizer"+"\n"+
				throwable);
		return "002914";
	}

	public static String logThreadPriorityChanged(String s, int i, int j) {
		log.info("002915  " + s + "  " + i + "  " + j
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002915";
	}

	public static Logger logCancelBeforeEnqueueLoggable(String s, String s1) {
		Object aobj[] = { s, s1 };
		log.info("002916  " + s + "  " + s1
				+ " OnceAS.i18n.WorkManagerLogLocalizer");
		return log;
	}

	public static String logCancelBeforeEnqueue(String s, String s1) {
		log.info("002916  " + s + "  " + s1
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002916";
	}

	public static Logger logCancelAfterEnqueueLoggable(String s, String s1) {
		Object aobj[] = { s, s1 };
		log.info("002917  " + s + "  " + s1
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");
		return log;
	}

	public static String logCancelAfterEnqueue(String s, String s1) {
		log.info("002917  " + s + "  " + s1
				+ "  OnceAS.i18n.WorkManagerLogLocalizer");

		return "002917";
	}

	public static void logFairShareConstraints(String s) {
		log.info("0029X1  " + s + "  OnceAS.i18n.WorkManagerLogLocalizer");
	}

}
