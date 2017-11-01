package com.onceas.work.constraint;

import com.onceas.descriptor.wm.dd.WorkManagerShutDownTriggerDD;
import com.onceas.work.WorkManagerService;

public final class WorkManagerShutdownAction extends AbstractStuckThreadAction {

	public WorkManagerShutdownAction(
			WorkManagerShutDownTriggerDD workmanagershutdowntriggerDD) {
		super(workmanagershutdowntriggerDD.getMaxStuckThreadTime(),
				workmanagershutdowntriggerDD.getStuckThreadCount());
	}

	public void execute() {
		wmService.forceShutdown();
	}

	public void withdraw() {
		wmService.start();
	}

	public void setWorkManagerService(WorkManagerService workmanagerservice) {
		wmService = workmanagerservice;
	}

	private WorkManagerService wmService;
}
