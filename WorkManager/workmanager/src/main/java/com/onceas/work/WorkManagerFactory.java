package com.onceas.work;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorkManagerFactory {

	public WorkManagerFactory() {
	}

	public static synchronized boolean isInitialized() {
		return SINGLETON != null;
	}

	static synchronized void set(WorkManagerFactory workmanagerfactory) {
		if (SINGLETON != null) {
			throw new AssertionError("Duplicate initialization of WorkManager");
		} else {
			SINGLETON = workmanagerfactory;
			return;
		}
	}

	public static WorkManagerFactory getInstance() {
		if (SINGLETON != null) {
			return SINGLETON;
		} else {
			initDelegate();
			return SINGLETON;
		}
	}

	public final WorkManager getDefault() {
		return DEFAULT;
	}

	public final WorkManager getSystem() {
		return SYSTEM;
	}

	public final WorkManager getRejector() {
		return REJECTOR;
	}

	public final WorkManager findOrCreate(String s, int i, int j, int k) {
		if (SINGLETON == null)
			initDelegate();
		WorkManager workmanager = (WorkManager) byName.get(s);
		if (workmanager != null) {
			return workmanager;
		} else {
			WorkManager workmanager1 = create(s, i, j, k);
			byName.put(s, workmanager1);
			return workmanager1;
		}
	}

	public final WorkManager findOrCreateResponseTime(String s, int i, int j,
			int k) {
		WorkManager workmanager = (WorkManager) byName.get(s);
		if (workmanager != null) {
			return workmanager;
		} else {
			WorkManager workmanager1 = createResponseTime(s, i, j, k);
			byName.put(s, workmanager1);
			return workmanager1;
		}
	}

	public final WorkManager findOrCreate(String s, int i, int j) {
		return findOrCreate(s, -1, i, j);
	}

	public final WorkManager find(String s) {
		WorkManager workmanager = (WorkManager) byName.get(s);
		if (workmanager != null) {
			return workmanager;
		} else {
			WorkManager workmanager1 = findAppScoped(s, null, null);
			return workmanager1 == null ? DEFAULT : workmanager1;
		}
	}

	public final WorkManager find(String s, String s1, String s2) {
		WorkManager workmanager = (WorkManager) byName.get(s);
		if (workmanager != null) {
			return workmanager;
		} else {
			WorkManager workmanager1 = findAppScoped(s, s1, s2);
			return workmanager1 == null ? DEFAULT : workmanager1;
		}
	}

	private static synchronized void initDelegate() {
		if (SINGLETON != null) {
			return;
		} else {
			SINGLETON = new WorkManagerFactory();
			SINGLETON.initialize();
			return;
		}
	}

	private void initialize() {
		DEFAULT = new WorkManagerLite("default");
		SYSTEM = DEFAULT;
		byName.put(WorkManagerConstant.ONCEAS_KERNEL_DEFAULT, DEFAULT);
		byName.put("default", DEFAULT);
		byName.put(WorkManagerConstant.ONCEAS_KERNEL_SYSTEM, SYSTEM);
		WorkManagerLite workmanagerlite = new WorkManagerLite();
		byName.put(WorkManagerConstant.ONCEAS_DIRECT, workmanagerlite);
	}

	protected WorkManager create(String s, int i, int j, int k) {
		return new WorkManagerLite(s, Math.max(j, k));
	}

	protected WorkManager createResponseTime(String s, int i, int j, int k) {
		return create(s, -1, j, k);
	}

	protected WorkManager findAppScoped(String s, String s1, String s2) {
		return null;
	}

	public static final int UNSPECIFIED = -1;

	public static final int HIGH_FAIR_SHARE = 100;

	private static WorkManagerFactory SINGLETON;

	WorkManager DEFAULT;

	WorkManager SYSTEM;

	WorkManager REJECTOR;

	final Map byName = Collections.synchronizedMap(new HashMap());
}
