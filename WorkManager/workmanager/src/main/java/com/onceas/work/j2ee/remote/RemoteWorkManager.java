/**
 * <p>Title:   WorkManagerMService.java</p>
 *
 * <p>Description: 基于OnceAS平台</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 中国科学院软件研究所</p>
 *
 * @author 张磊
 * @version 1.0
 *
 */

package com.onceas.work.j2ee.remote;

import java.util.ArrayList;
import java.util.Collection;

import com.onceas.work.j2ee.J2EEWorkManager;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkManager;

public class RemoteWorkManager //extends AbstractServiceMService implements
		implements RemoteWorkManagerMBean {

	public void start() {
		RemoteWorkReceiver.getRemoteWorkReceiver();
		RemoteWorkResponseReceiver.getRemoteWorkDispacher();
		RemoteFeedbackReceiver.getRemoteWorkReceiver();
		ReleaseWorkReceiver.getRemoteWorkReceiver();
	}

	public void startRemoteWMTest() {
		System.out.println("RemoteWMTest1 Begin--------------");
		// long t1 = System.currentTimeMillis();
		RemoteWork rw1 = new RemoteWork("RW1", 500, 2);
		RemoteWork rw2 = new RemoteWork("RW2", 500, 4);
		RemoteWork rw3 = new RemoteWork("RW3", 500, 6);
		RemoteWork rw4 = new RemoteWork("RW4", 500, 8);
		WorkManager wm = J2EEWorkManager.getDefault();
		try {

			WorkItem w1 = wm.schedule(rw1);
			WorkItem w2 = wm.schedule(rw2);
			WorkItem w3 = wm.schedule(rw3);
			WorkItem w4 = wm.schedule(rw4);
			Collection coll = new ArrayList();
			coll.add(w1);
			coll.add(w2);
			coll.add(w3);
			coll.add(w4);
			wm.waitForAll(coll, WorkManager.INDEFINITE);
			// long t2 = System.currentTimeMillis();
			int w1_result = ((RemoteWork) w1.getResult()).getComputResult();
			int w1_result1 = rw1.getComputResult();
			System.out.println(rw1.getName() + "' result is " + w1_result + " "
					+ w1_result1);

			int w2_result = ((RemoteWork) w2.getResult()).getComputResult();
			int w2_result1 = rw2.getComputResult();
			System.out.println(rw2.getName() + "' result is " + w2_result + " "
					+ w2_result1);

			int w3_result = ((RemoteWork) w3.getResult()).getComputResult();
			int w3_result1 = rw3.getComputResult();
			System.out.println(rw3.getName() + "' result is " + w3_result + " "
					+ w3_result1);

			int w4_result = ((RemoteWork) w4.getResult()).getComputResult();
			int w4_result1 = rw4.getComputResult();
			System.out.println(rw4.getName() + "' result is " + w4_result + " "
					+ w4_result1);
			// System.out.println("Works are completed in " + (t2 - t1) + "ms");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (WorkException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startRemoteWMTest1() {
		System.out.println("RemoteWMTest1 Begin--------------");
		long t1 = System.currentTimeMillis();
		System.out.println("start at " + t1);
		RemoteWork rw1 = new RemoteWork("RW1", 2000, 1);
		WorkManager wm = J2EEWorkManager.getDefault();
		try {
			WorkItem w1 = wm.schedule(rw1);

			Collection coll = new ArrayList();
			coll.add(w1);
			wm.waitForAll(coll, WorkManager.INDEFINITE);
			long t2 = System.currentTimeMillis();

			int w1_result = ((RemoteWork) w1.getResult()).getComputResult();
			int w1_result1 = rw1.getComputResult();
			System.out.println(rw1.getName() + "' result is " + w1_result + " "
					+ w1_result1);
			System.out.println(rw1.getName() + " is completed at " + t2);
			System.out.println(rw1.getName() + " is completed in " + (t2 - t1)
					/ 1000 + "s");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (WorkException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
