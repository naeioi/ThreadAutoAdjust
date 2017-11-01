package com.onceas.work.constraint;

import java.util.BitSet;
import java.util.logging.Logger;

import com.onceas.work.DebugWM;

public abstract class AbstractStuckThreadAction implements StuckThreadAction {

	protected int maxCount;

	long maxStuckTime;

	private BitSet stuckThreads;

	private boolean actionTaken;

	private static Logger log = Logger
			.getLogger(AbstractStuckThreadAction.class.toString());

	public AbstractStuckThreadAction(long l, int i) {
		stuckThreads = new BitSet();
		maxStuckTime = l * 1000L;
		maxCount = i;
	}

	/***************************************************************************
	 * @param i
	 *            �߳�ID
	 * @param l
	 *            ����ʱ��
	 * @param l1
	 *            MaxStuckTime
	 */
	public synchronized boolean threadStuck(int i, long l, long l1) {
		if (stuckThreads.get(i))// ����Ѿ���ʧЧ�б���
			return true;
		if (!isStuck(l, l1)) {
			// û��ʧЧ
			if (isDebugEnabled())
				debug("thread " + i + " is not stuck");
			return false;
		}
		// ʧЧ��
		if (isDebugEnabled())
			debug("thread " + i + " is stuck !");

		// �����б�
		stuckThreads.set(i);
		// ��ʧЧ�̴߳������ֵʱִ��ʧЧ���ö���
		if (maxCount <= 0) {
			return false;
		}
		if (!actionTaken && stuckThreads.cardinality() >= maxCount) {
			if (isDebugEnabled())
				debug("Stuck thread count is >= " + maxCount
						+ ", execute action");
			execute();
			actionTaken = true;
		}
		return true;
	}

	public synchronized void threadUnStuck(int i) {
		if (!stuckThreads.get(i))
			return;
		if (isDebugEnabled())
			debug("thread " + i + " is unstuck. "
					+ "Removing from stuck thread list");
		stuckThreads.clear(i);
		if (actionTaken && stuckThreads.cardinality() == 0) {
			if (isDebugEnabled())
				debug("All threads unstuck. Withdraw action");
			withdraw();
			actionTaken = false;
		}
	}

	/**
	 * ���ڼ���߳��Ƿ�ʧЧ
	 * 
	 * @param l
	 *            ����ʱ��
	 * @param l1
	 *            MaxStuckTime
	 * @return
	 */
	private boolean isStuck(long l, long l1) {
		long l2 = maxStuckTime > 0L ? maxStuckTime : l1;
		return l2 > 0L && l >= l2;
	}

	public synchronized int getStuckThreadCount() {
		return stuckThreads.cardinality();
	}

	public String toString() {
		return getClass().getName() + " with stuck time " + maxCount
				+ " and count " + maxStuckTime;
	}

	private boolean isDebugEnabled() {
		return DebugWM.debug_StuckThread;
	}

	private void debug(String s) {
		log.info("[" + toString() + "]" + s);
		log("[" + toString() + "]" + s);
	}

	private static void log(String s) {
		System.out.println("<AbstractorStuckThreadAction>" + s);
	}
}
