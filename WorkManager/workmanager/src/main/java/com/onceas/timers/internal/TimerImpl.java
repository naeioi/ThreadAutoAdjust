package com.onceas.timers.internal;

import java.util.Arrays;
import java.util.Collection;

import com.onceas.timers.CancelTimerListener;
import com.onceas.timers.NakedTimerListener;
import com.onceas.timers.StopTimerListener;
import com.onceas.timers.Timer;
import com.onceas.timers.TimerListener;
import com.onceas.util.collection.WeakConcurrentHashMap;

public class TimerImpl implements Timer, Comparable, Runnable {
	public static final class TimerRuntime implements
			com.onceas.runtime.timers.Timer {

		public String getTimerManagerName() {
			return timerManagerName;
		}

		public long getTimeout() {
			return timeout;
		}

		public long getPeriod() {
			return period;
		}

		public boolean isStopped() {
			return stopped;
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public long getExpirationCount() {
			return (long) pastExpirationTimes.length;
		}

		public long[] getPastExpirationTimes() {
			return pastExpirationTimes;
		}

		static final long serialVersionUID = 0xe940bf451af2dc16L;

		private final String timerManagerName;

		private final long timeout;

		private final long period;

		private final boolean stopped;

		private final boolean cancelled;

		private final long pastExpirationTimes[];

		public TimerRuntime(TimerImpl timerimpl) {
			timerManagerName = timerimpl.getTimerManager().getName() + "["
					+ timerimpl.toString() + "]";
			timeout = timerimpl.getTimeout();
			period = Math.abs(timerimpl.getPeriod());
			stopped = timerimpl.isStopped();
			cancelled = timerimpl.isCancelled();
			pastExpirationTimes = timerimpl.sortedExpirationTimes();
		}
	}

	private static final class HistoryMany {

		private void recordExpirationTime(long l) {
			pastExpirationTimes[historyIndex] = l;
			if (historyIndex == 99)
				historyIndex = 0;
			else
				historyIndex++;
		}

		private long[] sortedExpirationTimes() {
			long al[] = new long[pastExpirationTimes.length];
			System.arraycopy(pastExpirationTimes, 0, al, 0, al.length);
			Arrays.sort(al);
			int i;
			for (i = 0; i < al.length && al[i] <= 0L; i++)
				;
			if (i > 0) {
				long al1[] = new long[al.length - i];
				System.arraycopy(al, i, al1, 0, al1.length);
				return al1;
			} else {
				return al;
			}
		}

		private int historyIndex;

		private long pastExpirationTimes[];

		private HistoryMany() {
			historyIndex = 0;
			pastExpirationTimes = new long[100];
		}

	}

	TimerImpl(TimerManagerImpl timermanagerimpl, TimerListener timerlistener,
			long l, long l1) {
		oneExpirationTime = -1L;
		timerManager = timermanagerimpl;
		listener = timerlistener;
		timeout = l;
		period = l1;
		if (!(timerlistener instanceof NakedTimerListener))
			context = new TimerContext();
		timerMap.put(this, this);
	}

	void cleanup() {
		timerMap.remove(this);
	}

	public static Timer[] getTimers() {
		Collection collection = timerMap.values();
		if (collection == null || collection.isEmpty()) {
			return null;
		} else {
			Timer atimer[] = new Timer[collection.size()];
			return (Timer[]) (Timer[]) collection.toArray(atimer);
		}
	}

	TimerManagerImpl getTimerManager() {
		return timerManager;
	}

	void setCounter(long l) {
		counter = l;
	}

	void setStopped() {
		stopped = true;
	}

	void setTimeout(long l) {
		timeout = l;
	}

	public long getTimeout() {
		return timeout;
	}

	public long getPeriod() {
		return period;
	}

	public TimerListener getListener() {
		return listener;
	}

	public long getScheduledExecutionTime() throws IllegalStateException {
		return timeout;
	}

	public boolean cancel() {
		return timerManager.cancel(this);
	}

	public boolean isStopped() {
		return stopped;
	}

	public final void run() {
		if (context != null)
			context.push();
		if (stopped) {
			if (listener instanceof StopTimerListener)
				((StopTimerListener) listener).timerStopped(this);
		} else if (cancelled) {
			if (listener instanceof CancelTimerListener)
				((CancelTimerListener) listener).timerCancelled(this);
		} else {
			recordExpirationTime();
			listener.timerExpired(this);
		}
		if (context != null)
			context.pop();
		timerManager.complete(this);
	}

	boolean isExpired() {
		return timeout <= System.currentTimeMillis();
	}

	public boolean isCancelled() {
		return cancelled;
	}

	void setCancelled(boolean flag) {
		cancelled = flag;
	}

	public int compareTo(Object obj) {
		TimerImpl timerimpl = (TimerImpl) obj;
		long l = timeout - timerimpl.timeout;
		if (l == 0L)
			l = counter - timerimpl.counter;
		return l != 0L ? l >= 0L ? 1 : -1 : 0;
	}

	public String toString() {
		return "" + timeout + "." + counter + "(" + period + ")";
	}

	private void recordExpirationTime() {
		HistoryMany historymany = history;
		if (historymany == null) {
			if (oneExpirationTime == -1L) {
				oneExpirationTime = System.currentTimeMillis();
				return;
			}
			history = historymany = new HistoryMany();
			historymany.recordExpirationTime(oneExpirationTime);
		}
		historymany.recordExpirationTime(System.currentTimeMillis());
	}

	public com.onceas.runtime.timers.Timer getRuntime() {
		return new TimerRuntime(this);
	}

	private long[] sortedExpirationTimes() {
		HistoryMany historymany = history;
		if (historymany == null) {
			if (oneExpirationTime == -1L) {
				return new long[0];
			} else {
				long al[] = new long[1];
				al[0] = oneExpirationTime;
				return al;
			}
		} else {
			return historymany.sortedExpirationTimes();
		}
	}

	private static final WeakConcurrentHashMap timerMap = new WeakConcurrentHashMap(
			100);

	private TimerManagerImpl timerManager;

	private TimerListener listener;

	private long timeout;

	private long period;

	private long counter;

	boolean stopped;

	boolean cancelled;

	private TimerContext context;

	private static final int HISTORY_SIZE = 100;

	private HistoryMany history;

	private static final long UNUSED_HISTORY = -1L;

	private long oneExpirationTime;

}
