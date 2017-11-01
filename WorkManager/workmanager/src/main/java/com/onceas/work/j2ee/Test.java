package com.onceas.work.j2ee;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;

public class Test implements WorkListener, Work {

	public Test() {
	}

	public Test(boolean flag) {
		throwException = flag;
	}

	public void workAccepted(WorkEvent workevent) {
		System.out.println("work accepted : " + workevent.getType());
	}

	public void workRejected(WorkEvent workevent) {
		System.out.println("work rejected : " + workevent.getType()
				+ ", workitem : " + (item == null ? -1 : item.getStatus()));
	}

	public void workStarted(WorkEvent workevent) {
		System.out.println("work started : " + workevent.getType()
				+ ", workitem : " + (item == null ? -1 : item.getStatus()));
	}

	public void workCompleted(WorkEvent workevent) {
		System.out.println("work completed : " + workevent.getType()
				+ ", workitem : " + item.getStatus() + ", exception : "
				+ workevent.getException());
	}

	public static void main(String args[]) {
		Test test = new Test();
		Test test1 = new Test(true);// (true);
		// Kernel.ensureInitialized();
		WorkManager workmanager = J2EEWorkManager.getDefault();
		try {
			test.item = workmanager.schedule(test, test);
			test1.item = workmanager.schedule(test1, test1);
		} catch (WorkException workexception) {
			workexception.printStackTrace();
		} catch (IllegalArgumentException illegalargumentexception) {
			illegalargumentexception.printStackTrace();
		}
		// while(test.item.getStatus() != 4) ;
		while (test1.item.getStatus() != 4)
			;
	}

	public void run() {
		System.out.println("Test:    void run()");
		try {
			System.out.println("started run. sleeping ...");
			Thread.sleep(10000L);
		} catch (InterruptedException interruptedexception) {
			interruptedexception.printStackTrace();
		}
		if (throwException) {
			throw new RuntimeException("testing testing");
		} else {
			System.out.println("completed run");
			return;
		}
	}

	public void release() {

	}

	public boolean isDaemon() {
		return false;
	}

	private WorkItem item;

	private boolean throwException;
}
