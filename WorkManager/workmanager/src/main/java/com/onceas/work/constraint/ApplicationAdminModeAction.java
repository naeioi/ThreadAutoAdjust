package com.onceas.work.constraint;

import com.onceas.work.WorkManagerFactory;

public final class ApplicationAdminModeAction extends AbstractStuckThreadAction {
	public ApplicationAdminModeAction(long l, int i, String s) {
		super(l, i);
		applicationName = s;
	}

	public void execute() {
		WorkManagerFactory.getInstance().getSystem().schedule(new Runnable() {
			public void run() {
				// TODO
			}
		});

		return;
	}

	public void withdraw() {
		WorkManagerFactory.getInstance().getSystem().schedule(new Runnable() {
			public void run() {
				// TODO
			}
		});
		return;
	}

	private final String applicationName;

	static final boolean $assertionsDisabled; /* synthetic field */

	static {
		$assertionsDisabled = !(ApplicationAdminModeAction.class)
				.desiredAssertionStatus();
	}
}
