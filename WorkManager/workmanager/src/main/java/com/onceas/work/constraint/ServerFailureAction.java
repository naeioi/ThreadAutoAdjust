package com.onceas.work.constraint;

import com.onceas.health.HealthMonitorMService;

public final class ServerFailureAction extends AbstractStuckThreadAction {

	public ServerFailureAction(int maxStuckThreadTime, int stuckThreadCount) {
		super(maxStuckThreadTime, stuckThreadCount);
	}

	public void execute() {
		HealthMonitorMService.subsystemFailed("Thread Pool",
				"Server failed as the number of stuck threads has exceeded the max limit of "
						+ maxCount);
	}

	public void withdraw() {
	}
}
