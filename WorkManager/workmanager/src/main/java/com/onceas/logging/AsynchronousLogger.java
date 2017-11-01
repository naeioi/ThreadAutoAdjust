package com.onceas.logging;


import java.util.logging.Logger;

import com.onceas.work.WorkManager;
import com.onceas.work.WorkManagerFactory;

public class AsynchronousLogger {
	private class LogWorker implements Runnable {
		private String logInfo;

		private final static int INFO = 0;

		private final static int DEBUG = 1;

		private final static int ERROR = 2;

		private final static int FATAL = 3;

		private int logType = 0;

		private Throwable exception = null;

		public LogWorker(String s, int i, Throwable e) {
			logInfo = s;
			logType = i;
			exception = e;
		}

		public void run() {
			switch (logType) {
			case INFO:
				if (exception == null) {
					log.info(logInfo);
				} else {
					log.info(logInfo+"\n"+exception);
				}
				break;
			case DEBUG:
				if (exception == null) {
					log.info(logInfo);
				} else {
					log.info(logInfo+"\n"+exception);
				}
				break;
			case ERROR:
				if (exception == null) {
					log.severe(logInfo);
				} else {
					log.severe(logInfo+"\n"+exception);
				}
				break;
			case FATAL:
				if (exception == null) {
					log.severe(logInfo);
				} else {
					log.severe(logInfo+"\n"+exception);
				}
				break;
			default:
				if (exception == null) {
					log.info(logInfo);
				} else {
					log.info(logInfo+"\n"+exception);
				}
				break;
			}
			logInfo = null;
		}
	}

	protected Logger log;

	protected WorkManager wm;

	public AsynchronousLogger() {
		wm = WorkManagerFactory.getInstance().getSystem();
	}

	protected void info(String s) {
		LogWorker lw = new LogWorker(s, LogWorker.INFO, null);
		wm.schedule(lw);
	}

	protected void info(String s, Throwable e) {
		LogWorker lw = new LogWorker(s, LogWorker.INFO, e);
		wm.schedule(lw);
	}

	protected void debug(String s) {
		LogWorker lw = new LogWorker(s, LogWorker.DEBUG, null);
		wm.schedule(lw);
	}

	protected void debug(String s, Throwable e) {
		LogWorker lw = new LogWorker(s, LogWorker.DEBUG, e);
		wm.schedule(lw);
	}

	protected void error(String s) {
		LogWorker lw = new LogWorker(s, LogWorker.ERROR, null);
		wm.schedule(lw);
	}

	protected void error(String s, Throwable e) {
		LogWorker lw = new LogWorker(s, LogWorker.ERROR, e);
		wm.schedule(lw);
	}

	protected void fatal(String s) {
		LogWorker lw = new LogWorker(s, LogWorker.FATAL, null);
		wm.schedule(lw);
	}

	protected void fatal(String s, Throwable e) {
		LogWorker lw = new LogWorker(s, LogWorker.FATAL, e);
		wm.schedule(lw);
	}
}
