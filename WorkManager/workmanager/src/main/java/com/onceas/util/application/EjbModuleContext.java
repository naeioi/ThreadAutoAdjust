package com.onceas.util.application;

import com.onceas.work.WorkManagerCollection;

public class EjbModuleContext implements ApplicationContextInternal {
	private WorkManagerCollection workManagerCollection;
	private String applicationName;
	
	public EjbModuleContext() {
	}
	
	public String getApplicationName(){
		return applicationName;
	}
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public WorkManagerCollection getWorkManagerCollection() {	
		return workManagerCollection;
	}

	public void setWorkManagerCollection(WorkManagerCollection workManagerCollection) {
		this.workManagerCollection = workManagerCollection;
	}
}
