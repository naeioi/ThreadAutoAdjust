package com.onceas.work.j2ee.remote;

import java.io.Serializable;

import commonj.work.Work;

public abstract class RemoteWorkAdapter implements com.onceas.work.Work,
		Serializable {
	protected String workManagerName;

	protected String applicationName;

	protected String moduleName;

	protected long creationTimeStamp;

	protected boolean started;

	protected boolean scheduled;

	protected Exception excepion;

	protected long m_lWorkId;

	protected String m_nNodeFrom;

	public RemoteWorkAdapter() {
		creationTimeStamp = System.currentTimeMillis();
	}

	public RemoteWorkAdapter(long lWorkId, String nNodeFrom) {
		this.m_lWorkId = lWorkId;
		this.m_nNodeFrom = nNodeFrom;
		this.creationTimeStamp = System.currentTimeMillis();
	}

	public RemoteWorkAdapter(long lWorkId, String nNodeFrom, String wmName,
			String appName, String moduleName) {
		this.m_lWorkId = lWorkId;
		this.m_nNodeFrom = nNodeFrom;
		this.workManagerName = wmName;
		this.applicationName = appName;
		this.moduleName = moduleName;
		this.creationTimeStamp = System.currentTimeMillis();
	}

	public void prepareForReuse() {
		started = false;
		creationTimeStamp = -1L;
		scheduled = false;
	}

	public Runnable overloadAction(String s) {
		return null;
	}

	public Runnable cancel(String s) {
		return null;
	}

	public boolean isTransactional() {
		return false;
	}

	public final void setWorkManager(String workManager) {
		if (workManager == null)
			return;
		workManagerName = workManager;
	}

	public final String getWorkManager() {
		return workManagerName;
	}

	public Work getWork() {
		return null;
	}

	public long getWorkId() {
		return m_lWorkId;
	}

	public String getNodeFrom() {
		return m_nNodeFrom;
	}

	public Exception getExcepion() {
		return excepion;
	}

	public void setExcepion(Exception excepion) {
		this.excepion = excepion;
	}

	public void setNodeFrom(String nodeFrom) {
		m_nNodeFrom = nodeFrom;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

}
