package com.onceas.work.management.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.onceas.work.util.WMUpdateListenerName;

/**
 * 提供注册、移除WMUpdateListener，emmit WMUpdateEvent的方法，是对监听器和事件发送进行统一处理的 场所。
 * 
 * @author Administrator
 * 
 */
public class UnifiedListenerRepository implements UnifiedListenerRepositoryI {

	// private static UnifiedListenerRepository singleton = null;

	private final ConcurrentHashMap<WMUpdateListenerName, List<WMUpdateListener>> listenerMap;

	public UnifiedListenerRepository() {
		listenerMap = new ConcurrentHashMap<WMUpdateListenerName, List<WMUpdateListener>>();

	}

	public void addWMUpdateListener(WMUpdateListenerName listenerName,
			WMUpdateListener wmUpdateListener) {
		if (listenerName == null)
			throw new IllegalArgumentException("[" + this.getClass().getName()
					+ "WorkMamager UpdateListenerName is null! ");

		List<WMUpdateListener> tmpList = listenerMap.get(listenerName);
		if (tmpList == null) {
			tmpList = new ArrayList<WMUpdateListener>();
			listenerMap.put(listenerName, tmpList);
		} else {
			listenerMap.put(listenerName, tmpList);
		}
		// synchronized?
		tmpList.add(wmUpdateListener);
	}

	/**
	 * 删除给定名字的list中指定的listener
	 */
	public void removeWMUpdateListener(WMUpdateListenerName listenerName,
			WMUpdateListener wmUpdateListener) {
		if (!listenerMap.containsKey(listenerName)) {
			System.out.println("Listener:[" + listenerName + "] not found!");
		} else {
			List<WMUpdateListener> tmpList = listenerMap.get(listenerName);
			if (tmpList != null && tmpList.size() != 0) {
				if (tmpList.contains(wmUpdateListener)) {
					// synchronized?
					tmpList.remove(wmUpdateListener);
					if (tmpList.size() == 0) {
						// last element in list so remove list from map. may not
						// necessary
						tmpList = null;
						listenerMap.remove(listenerName);
					}
				}
			}
		}
	}

	/**
	 * 删除listenerName对应的整个list
	 * 
	 * @param listenerName
	 */
	public void removeWMUpdateListener(WMUpdateListenerName listenerName) {
		listenerMap.remove(listenerName);
	}

	public void removeAll() {
		listenerMap.clear();
	}

	public void emmitWMUpdateEvent(WMUpdateListenerName listenerName,
			WMUpdateEvent wmUpdateEvent) {
		// synchronized/lock
		List<WMUpdateListener> tmpList = listenerMap.get(listenerName);
		if (tmpList != null) {
			synchronized (this) {
				for (WMUpdateListener listener : tmpList) {
					listener.updatePerformed(wmUpdateEvent);
				}
			}
		}
	}

}
