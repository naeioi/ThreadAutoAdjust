package com.onceas.runtime;

import javax.management.ObjectName;

public class Runtime implements RuntimeMBean {

	protected String name = null;

	protected ObjectName objectName = null;

	protected RuntimeMBean parent;

	protected String type = "notype";

	public Runtime() {
	}

	public Runtime(String name) {
		this.name = name;
	}

	public Runtime(String name, RuntimeMBean runtimembean) {
		this.name = name;
		this.parent = runtimembean;
	}

	public String getName() {
		return this.name;
	}

	public ObjectName getObjectName() {
		return this.objectName;
	}

	public RuntimeMBean getParent() {
		return this.parent;
	}

	public String getType() {
		return type;
	}

	public RuntimeMBean getRuntimeMBean() {
		return this;
	}

	// by syk
	public void setParent(RuntimeMBean parent) {
		this.parent = parent;
	}
	// end
}
