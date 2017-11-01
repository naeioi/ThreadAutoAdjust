package com.onceas.work.constraint;

import com.onceas.work.DebugWM;
import com.onceas.work.management.notification.WMUpdateEvent;
import com.onceas.work.management.notification.WMUpdateListener;

public class ResponseTimeRequestClass extends ServiceClassSupport implements
		WMUpdateListener {
	private static final boolean DEBUG = DebugWM.debug_ResponseTimeRequest;

	private static final double PERIOD = 2000D;

	private static final double HALF_LIFE = 20000D;

	private static final double W;

	private static final double WC;

	private static final long DEFAULT_INCR = 100L;

	private double responseTime;

	private int previouslyCompleted;

	private double interval;

	static {
		W = Math.pow(0.5D, PERIOD / HALF_LIFE);
		WC = 1.0D - W;
		// W =0.933
		// WC = 0.067
	}

	public ResponseTimeRequestClass(String s, int i) {
		super(s);
		previouslyCompleted = 0;
		setResponseTime(i);
	}

	public double getInterval() {
		return interval;
	}

	public void timeElapsed(long l, ServiceClassesStats serviceclassesstats) {
		int i = previouslyCompleted;
		previouslyCompleted = getCompleted();
		int j = previouslyCompleted - i;
		if (j == 0 || previouslyCompleted == 0)
			return;
		interval = W * interval + (WC * responseTime) / (double) j;
		long l1 = serviceclassesstats.adjustResponseTime(interval);
		if (l1 <= 0L)
			l1 = 1L;
		long l2 = (long) (responseTime - (2.2999999999999998D * (double) getThreadUse())
				/ (double) previouslyCompleted);
		if (l2 > l1)
			l2 = l1;
		if (l2 < 1L)
			l2 = 1L;
		setIncrements(l2, l1);

		log("** RT ** " + this + "\nCompleted=" + j + ", interval=" + interval
				+ ", responseTime=" + responseTime + ", incr=" + l1
				+ ", acceptableWait=" + l2 + ", previouslyCompleted="
				+ previouslyCompleted + ", threadUse=" + getThreadUse());
	}

	// by syk
	public int getGoalMs() {
		// double?
		return (int) responseTime;
	}

	public void updatePerformed(WMUpdateEvent e) {
		Object proposed = e.getProposed();
		if (proposed instanceof ResponseTimeRequestClass) {
			setResponseTime(((ResponseTimeRequestClass) proposed).getGoalMs());
			// return;
		}
		// return;
	}

	// end

	private void setResponseTime(double d) {
		if (responseTime == d) {
			return;
		} else {
			setIncrements(DEFAULT_INCR, DEFAULT_INCR);
			responseTime = d;
			interval = responseTime;
			return;
		}
	}

	private static void log(String s) {
		if (DEBUG)
			System.out.println("<ResponeTimeRequestClass>" + s);
	}

}
