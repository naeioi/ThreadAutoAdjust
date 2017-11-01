package com.onceas.work.management.notification;

import com.onceas.work.util.WMUpdateListenerName;

public interface UnifiedListenerRepositoryI {

    public abstract void addWMUpdateListener(WMUpdateListenerName listenerName, WMUpdateListener wmUpdateListener);

    public abstract void removeWMUpdateListener(WMUpdateListenerName listenerName, WMUpdateListener wmUpdateListener);
    
    public abstract void emmitWMUpdateEvent(WMUpdateListenerName listenerName, WMUpdateEvent wmUpdateEvent);

}
