package com.onceas.work.util;

/**
 * 充当存储WMUpdateListener Map的key name的完整形式是： wmName:moduleName@componentName ,if
 * moduleName != null wmName:componentName ,if moduleName == null
 * 
 * 监听器是监听对某个workmanager组件（maxthreadsconstraint，minthreadsconstraint，capacity，
 * fairshare request class，responsetime request class）的更改 wmName - workmanager名字
 * componentName - wmName属下的workmanager组件名字
 * 
 */
public class WMUpdateListenerName {
	private String wmName;

	private String moduleName;

	private String componentName;

	private String canonicalName;

	private final String spec1 = ":";

	private final String spec2 = "@";

	// precondition: all arguments cannot be null
	public WMUpdateListenerName(String wmName, String moduleName,
			String componentName) {
		if (wmName == null)
			throw new IllegalArgumentException(
					"workmanager name must be not null when creating WMUpdateListenerName!");
		this.wmName = wmName;
		this.moduleName = moduleName;
		this.componentName = componentName;
		if (moduleName == null) {
			this.canonicalName = wmName + spec1 + componentName;
		} else {
			this.canonicalName = wmName + spec1 + moduleName + spec2
					+ componentName;
		}
	}

	public WMUpdateListenerName(String wmName, String componentName) {
		if (wmName == null)
			throw new IllegalArgumentException(
					"workmanager name must be not null when creating WMUpdateListenerName!");
		this.wmName = wmName;
		this.componentName = componentName;
		this.canonicalName = wmName + spec1 + componentName;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getWmName() {
		return wmName;
	}

	public void setWmName(String wmName) {
		this.wmName = wmName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof WMUpdateListenerName))
			return false;
		if (canonicalName.equals(((WMUpdateListenerName) obj)
				.getCanonicalName()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return canonicalName.hashCode();
	}

	@Override
	public String toString() {
		return "[" + getClass().getName() + "@"
				+ Integer.toHexString(hashCode()) + "] " + canonicalName;
	}

}
