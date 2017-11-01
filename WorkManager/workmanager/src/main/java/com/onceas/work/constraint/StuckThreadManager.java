package com.onceas.work.constraint;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.onceas.work.DebugWM;
import com.onceas.work.WorkAdapter;
import com.onceas.work.WorkManagerFactory;
import com.onceas.work.WorkManagerService;
import com.onceas.work.threadpool.ExecuteThread;

public final class StuckThreadManager {
	private final WorkManagerShutdownAction workManagerShutdown;

	private final StuckThreadAction stuckThreadActions[];

	private final String name;

	private static Logger log = Logger.getLogger(StuckThreadManager.class.toString());

	public StuckThreadManager() {
		workManagerShutdown = null;
		stuckThreadActions = null;
		name = "NO STUCK THREAD ACTIONS !";
	}

	public StuckThreadManager(
			WorkManagerShutdownAction workmanagershutdownaction,
			ApplicationAdminModeAction applicationadminmodeaction,
			ServerFailureAction serverfailureaction) {
		workManagerShutdown = workmanagershutdownaction;
		StringBuffer stringbuffer = new StringBuffer();
		ArrayList arraylist = new ArrayList();
		if (workmanagershutdownaction != null) {
			stringbuffer.append("WorkManagerShutdown: "
					+ workmanagershutdownaction + "\n");
			arraylist.add(workmanagershutdownaction);
		}
		if (applicationadminmodeaction != null) {
			stringbuffer.append("ApplicationAdminMode: "
					+ applicationadminmodeaction + "\n");
			arraylist.add(applicationadminmodeaction);
		}
		if (serverfailureaction != null) {
			stringbuffer.append("ServerFailureAction: " + serverfailureaction
					+ "\n");
			arraylist.add(serverfailureaction);
		}
		name = stringbuffer.toString();
		if (isDebugEnabled())
			debug("created StuckThreadManager");
		stuckThreadActions = new StuckThreadAction[arraylist.size()];
		arraylist.toArray(stuckThreadActions);
	}

	public void threadUnStuck(int i) {
		if (stuckThreadActions == null)
			return;
		if (isDebugEnabled())
			debug("Thread detected as unstuck !");
		for (int j = 0; j < stuckThreadActions.length; j++)
			stuckThreadActions[j].threadUnStuck(i);

	}

	public boolean threadStuck(ExecuteThread executethread, long l, long l1) {
		if (stuckThreadActions == null)
			return false;
		int i = executethread.id;
		boolean flag = false;
		/*
		 * if(isDebugEnabled()) debug("Checking for stuck threads");
		 */
		for (int j = 0; j < stuckThreadActions.length; j++) {
			if (!stuckThreadActions[j].threadStuck(i, l, l1))
				continue;
			flag = true;
			if (isDebugEnabled())
				debug("Thread detected as stuck !");
			cancelWork(executethread);
		}

		return flag;
	}

	/**
	 * Work Cancel is a Job which is impled by Container Work implier or app
	 * work implier.
	 * 
	 * @param executethread
	 */
	private void cancelWork(ExecuteThread executethread) {
		WorkAdapter workadapter = executethread.getCurrentWork();
		if (workadapter != null) {
			Runnable runnable = workadapter
					.cancel("Work cancelled due to stuck thread");
			if (runnable != null)
				WorkManagerFactory.getInstance().getRejector().schedule(
						runnable);
		}
	}

	public void setWorkManagerService(WorkManagerService workmanagerservice) {
		if (workManagerShutdown != null)
			workManagerShutdown.setWorkManagerService(workmanagerservice);
	}

	public int getStuckThreadCount() {
		if (stuckThreadActions == null)
			return 0;
		int i = 0;
		for (int j = 0; j < stuckThreadActions.length; j++)
			i += stuckThreadActions[j].getStuckThreadCount();

		return i;
	}

	public String toString() {
		return name;
	}

	private boolean isDebugEnabled() {
		return DebugWM.debug_StuckThread;
	}

	private void debug(String s) {
		// log.info("[StuckThreadManager][" + name + "]" + s);
		log("[StuckThreadManager][" + name + "]" + s);
	}

	private static void log(String s) {
		System.out.println("<StuckThreadManager>" + s);
	}

}
