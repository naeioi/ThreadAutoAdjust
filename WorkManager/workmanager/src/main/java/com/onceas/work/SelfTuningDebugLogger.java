package com.onceas.work;

import com.onceas.work.diagnostics.debug.DebugLogger;

public class SelfTuningDebugLogger {

	public SelfTuningDebugLogger() {
	}

	public static final boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public static void debug(String s) {
		logger.debug(s);
	}

	public static void debug(String s, Throwable throwable) {
		logger.debug(s, throwable);
	}

	private static final DebugLogger logger = DebugLogger
			.getDebugLogger("DebugSelfTuning");

}
