package com.onceas.work.diagnostics.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public final class DebugLogger {

	public static synchronized DebugLogger getDebugLogger(String s) {
		DebugLogger debuglogger = (DebugLogger) debugLoggers.get(s);
		if (debuglogger == null) {
			debuglogger = new DebugLogger(s);
			debugLoggers.put(s, debuglogger);
		}
		return debuglogger;
	}

	public static DebugLogger createUnregisteredDebugLogger(String s,
			boolean flag) {
		DebugLogger debuglogger = new DebugLogger(s);
		debuglogger.setDebugEnabled(flag);
		return debuglogger;
	}

	static Logger getLogger() {
		return myLogger;
	}

	static void setLogger(Logger logger) {
		myLogger = logger;
	}

	static void setContextMode(int i) {
		contextMode = i;
	}

	static void setDebugContext(DebugContext debugcontext) {
		debugContext = debugcontext;
	}

	static void setDebugMask(long l) {
		debugMask = l;
	}

	private static Logger createAndInitAnonymousLogger() {
		Logger logger = Logger.getAnonymousLogger();
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINE);
		StreamHandler streamhandler = new StreamHandler(System.out,
				new SimpleFormatter()) {

			public void publish(LogRecord logrecord) {
				super.publish(logrecord);
				super.flush();
			}

			public void close() {
				super.flush();
			}

		};
		try {
			streamhandler.setLevel(Level.FINE);
		} catch (SecurityException securityexception) {
		}
		logger.addHandler(streamhandler);
		return logger;
	}

	private DebugLogger(String s) {
		debugLoggerName = null;
		debugEnabled = false;
		debugLoggerName = s;
		try {
			debugEnabled = Boolean.getBoolean("weblogic.debug." + s);
		} catch (SecurityException securityexception) {
		}
	}

	public String getDebugLoggerName() {
		return debugLoggerName;
	}

	public final boolean isDebugEnabled() {
		switch (contextMode) {
		case 1: // '\001'
			return debugEnabled
					&& (debugMask & debugContext.getDyeVector()) == debugMask;

		case 2: // '\002'
			return debugEnabled
					&& (debugMask | debugContext.getDyeVector()) != 0L;
		}
		return debugEnabled;
	}

	final void setDebugEnabled(boolean flag) {
		debugEnabled = flag;
	}

	public void debug(String s) {
		log(Level.FINE, s, null);
	}

	public void debug(String s, Throwable throwable) {
		log(Level.FINE, s, throwable);
	}

	private void log(Level level, String s, Throwable throwable) {
		if (isDebugEnabled()) {
			LogRecord logrecord = new LogRecord(level, s);
			logrecord.setLoggerName(debugLoggerName);
			logrecord.setThrown(throwable);
			logrecord
					.setSourceClassName((DebugLogger.class)
							.getName());
			logrecord.setSourceMethodName("debug");
			myLogger.log(logrecord);
		}
	}

	private static final String PROP_PREFIX = "weblogic.debug.";

	static final int CTX_MODE_OFF = 0;

	static final int CTX_MODE_AND = 1;

	static final int CTX_MODE_OR = 2;

	private static Map debugLoggers = new HashMap();

	private static Logger myLogger = createAndInitAnonymousLogger();

	private static DebugContext debugContext = null;

	private static int contextMode = 0;

	private static long debugMask = 0L;

	private String debugLoggerName;

	private boolean debugEnabled;

}
