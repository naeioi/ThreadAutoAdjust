package com.onceas.systemplatform;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class VM15 extends VM {

	public VM15() {
	}

	public String threadDumpAsString() {
		ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
		if (threadmxbean == null)
			return null;
		long al[] = threadmxbean.getAllThreadIds();
		if (al == null || al.length == 0)
			return null;
		ThreadInfo athreadinfo[] = threadmxbean.getThreadInfo(al, 0x7fffffff);
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < athreadinfo.length; i++) {
			ThreadInfo threadinfo = athreadinfo[i];
			if (threadinfo != null)
				stringbuffer.append(formattedThreadInfo(threadinfo) + "\n");
		}

		return stringbuffer.toString();
	}

	private static String formattedThreadInfo(ThreadInfo threadinfo) {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("\"" + threadinfo.getThreadName() + "\"");
		String s = threadinfo.getLockName();
		if (s != null)
			stringbuffer.append(" waiting for lock " + s);
		stringbuffer.append(" " + threadinfo.getThreadState());
		if (threadinfo.isInNative())
			stringbuffer.append(" native");
		if (threadinfo.isSuspended())
			stringbuffer.append(" suspended");
		if (threadinfo.getStackTrace() != null)
			stringbuffer.append("\n"
					+ getStackTrace(threadinfo.getStackTrace()));
		return stringbuffer.toString();
	}

	public String threadDumpAsString(Thread thread) {
		if (thread == null)
			return null;
		else
			return getStackTrace(thread.getStackTrace());
	}

	public String dumpDeadlockedThreads() {
		ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
		if (threadmxbean == null)
			return null;
		long al[] = threadmxbean.findMonitorDeadlockedThreads();
		if (al == null || al.length == 0)
			return null;
		ThreadInfo athreadinfo[] = threadmxbean.getThreadInfo(al, 0x7fffffff);
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < athreadinfo.length; i++) {
			ThreadInfo threadinfo = athreadinfo[i];
			if (threadinfo != null) {
				String s = "[deadlocked thread] " + threadinfo.getThreadName();
				stringbuffer.append(s + ":\n");
				stringbuffer.append(underline(s.length()));
				stringbuffer.append("Thread '" + threadinfo.getThreadName()
						+ "' is waiting to acquire lock '"
						+ threadinfo.getLockName()
						+ "' that is held by thread '"
						+ threadinfo.getLockOwnerName() + "'\n");
				stringbuffer.append("\nStack trace:\n");
				stringbuffer.append(underline(12));
				stringbuffer.append(getStackTrace(threadinfo.getStackTrace())
						+ "\n");
			}
		}

		return stringbuffer.toString();
	}

	private static String underline(int i) {
		StringBuffer stringbuffer = new StringBuffer(i);
		for (int j = 0; j < i; j++)
			stringbuffer.append("-");

		stringbuffer.append("\n");
		return stringbuffer.toString();
	}

	private static String getStackTrace(StackTraceElement astacktraceelement[]) {
		if (astacktraceelement == null || astacktraceelement.length == 0)
			return null;
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < astacktraceelement.length; i++)
			stringbuffer.append("\t" + astacktraceelement[i].toString() + "\n");

		return stringbuffer.toString();
	}
}
