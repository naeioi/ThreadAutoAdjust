package com.onceas.systemplatform;

import java.io.Serializable;

public class ServerInfo implements Serializable {
	public static final String JNDI_NAME = "ServerInfo";

	private static String props[] = { "java.vm.info", "java.vm.name",
			"java.vm.vendor", "java.vm.version", "java.runtime.name",
			"java.runtime.version", "os.arch", "os.name", "os.version",
			"sun.os.patch.level", "user.country", "user.language" };

	private String[] propsValues = new String[props.length];

	public ServerInfo() {
		init();
	}

	private void init() {
		for (int i = 0; i < props.length; i++) {
			propsValues[i] = System.getProperty(props[i]);
		}
	}

	public String[] getPropsValues() {
		//to avoid concurrent modification exception 
		return this.propsValues.clone();
	}
}
