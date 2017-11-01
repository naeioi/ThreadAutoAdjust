package com.onceas.work.util;

/**
 * ³äµ±´æ´¢WMUpdateListener MapµÄkey
 * 
 */
public class WorkManagerName {
	private String appName;

	private String moduleName;

	private String wmName;

	private String type;

	// form : appName= ,moduleName= ,wmName= ,type= .
	private String canonicalName;

	private String SPEC1 = WorkManagerConstant.WMNAME_SPEC1;

	private String SPEC2 = WorkManagerConstant.WMNAME_SPEC2;

	public WorkManagerName(String appName, String moduleName, String wmName,
			String type) {
		this.appName = appName;
		this.moduleName = moduleName;
		this.wmName = wmName;
		this.type = type;
		this.canonicalName = WorkManagerConstant.APPLICATION_NAME + SPEC1
				+ appName + SPEC2 + WorkManagerConstant.MODULE_NAME + SPEC1
				+ moduleName + SPEC2 + WorkManagerConstant.WORKMANAGER_NAME
				+ SPEC1 + wmName + SPEC2
				+ WorkManagerConstant.WORKMANAGER_COMPONENT_TYPE + SPEC1 + type
				+ SPEC2;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getWmName() {
		return wmName;
	}

	public void setWmName(String wmName) {
		this.wmName = wmName;
	}

	@Override
	public String toString() {
		return "WorkManagerName[" + getClass().getName() + "@"
				+ Integer.toHexString(hashCode()) + "]:(appName:" + appName
				+ " moduleName:" + moduleName + " wmName:" + wmName + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof WorkManagerName))
			return false;
		if (canonicalName.equals(((WorkManagerName) obj).getCanonicalName()))
			return true;
		return false;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

}
