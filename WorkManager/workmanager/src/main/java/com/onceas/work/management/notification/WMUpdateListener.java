package com.onceas.work.management.notification;

import java.util.EventListener;

public interface WMUpdateListener extends EventListener {
//	public WorkManagerName workManagerName;
	public abstract void updatePerformed(WMUpdateEvent e);

}
