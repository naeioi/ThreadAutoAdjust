package com.onceas.runtime.server;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.onceas.health.HealthState;
import com.onceas.runtime.Runtime;
import com.onceas.runtime.RuntimeMBean;

public class ServerHealthRuntime extends Runtime implements
		ServerHealthRuntimeMBean {
	public static ServerHealthRuntimeMBean createServerHealthRuntimeMBean(
			String serverName, RuntimeMBean parent) {
		ServerHealthRuntimeMBean serverHealthRuntimeMBean = new ServerHealthRuntime(
				serverName, parent);
		return serverHealthRuntimeMBean;
	}

	private HealthState healthState;

	public ServerHealthRuntime(String serverName) {
		super(serverName);
		type = Constant.SERVER_HEALTH_TYPE;
		try {
			this.objectName = new ObjectName(Constant.SERVER_RUNTIME_DOMAIN
					+ ":" + Constant.RUNTIME_TYPE + "=" + type + ","
					+ Constant.SERVER_NAME + "=" + this.getName());
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public ServerHealthRuntime(String serverName, RuntimeMBean parent) {
		super(serverName, parent);
		type = Constant.SERVER_HEALTH_TYPE;
		try {
			this.objectName = new ObjectName(Constant.SERVER_RUNTIME_DOMAIN
					+ ":" + Constant.RUNTIME_TYPE + "=" + type + ","
					+ Constant.SERVER_NAME + "=" + this.getName());
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public HealthState getHealthState() {
		return healthState;
	}

	public void setHealthState(int i, String s) {
		healthState = new HealthState(i, s);
	}
}
