package com.onceas.health;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.onceas.runtime.RuntimeMBean;
import com.onceas.service.lifecycle.AbstractLifeCycleService;
import com.onceas.service.lifecycle.ServiceConfiguration;
import com.onceas.service.lifecycle.ServiceException;
import com.onceas.timers.Timer;
import com.onceas.timers.TimerListener;
import com.onceas.timers.TimerManager;
import com.onceas.timers.TimerManagerFactory;
import com.onceas.work.WorkManagerFactory;

public final class HealthMonitorMService extends AbstractLifeCycleService
		implements TimerListener, HealthMonitorMServiceMBean {

	private static class SubsystemFailedHandler implements Runnable {
		private String name;

		private String reason;

		public SubsystemFailedHandler(String name, String reason) {
			this.name = name;
			this.reason = reason;
		}

		public void run() {
			HealthLogger.logErrorSubsystemFailedWithReason(name, reason);
			MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
					.loadOnceas();
			try {
				String failureAction = (String) server
						.getAttribute(new ObjectName(
								"onceas.work:service=WorkManagerFactory"),
								"FailureAction");
				if ("shutdown".equals(failureAction)) {
					server.invoke(new ObjectName(
							"default:service=ShutdownService"),
							"shutdownserver", null, null);
				}
			} catch (AttributeNotFoundException e) {
				e.printStackTrace();
			} catch (InstanceNotFoundException e) {
				e.printStackTrace();
			} catch (MBeanException e) {
				e.printStackTrace();
			} catch (ReflectionException e) {
				e.printStackTrace();
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	private static final Map<String, MonitoredSystemTableEntry> monSysTbl = new ConcurrentHashMap<String, MonitoredSystemTableEntry>();

	private TimerManager timerManager;

	private int checkIntervalSeconds = 30;

	public HealthMonitorMService() {
	}

	@Override
	public void init() throws ServiceException {
		preamblePropertiesFromConfig();
	}


	private void preamblePropertiesFromConfig() {
		//从配置文件config.properties获取属性初始配置
		setHealthCheckIntervalSeconds(ServiceConfiguration.getHealthCheckIntervalSeconds());
	}

	public void start() {
		timerManager = TimerManagerFactory.getTimerManagerFactory()
				.getTimerManager("HealthMonitorTask",
						WorkManagerFactory.getInstance().getSystem());
		timerManager.scheduleAtFixedRate(this, checkIntervalSeconds * 1000,
				checkIntervalSeconds * 1000);
	}

	public void stop() {
		shutdown();
	}

	public void halt() {
		shutdown();
	}

	private void shutdown() {
		if (timerManager != null)
			timerManager.stop();
	}

	private void restart() {
		this.halt();
		this.start();
	}

	public static void register(String s, RuntimeMBean runtimembean,
			boolean flag) {
		HealthDebug.log("> HealthMonitorService::register keyArg = " + s);
		if (s != null && runtimembean != null
				&& (runtimembean instanceof HealthFeedback)) {
			return;
		}
		monSysTbl.put(s.trim(), new MonitoredSystemTableEntry(s, runtimembean,
				flag));
	}

	public static void unregister(String s) {
		HealthDebug.log("> HealthMonitorService::unregister keyArg = " + s);
		// Debug.assertion(s != null);
		if (s == null) {
			return;
		}
		monSysTbl.remove(s);
	}

	public static synchronized void subsystemFailed(String name, String reason) {
		WorkManagerFactory.getInstance().getSystem().schedule(
				new SubsystemFailedHandler(name, reason));
	}

	public static synchronized void panic(Throwable throwable) {
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		try {
			String panicAction = (String) server.getAttribute(new ObjectName(
					"onceas.work:service=WorkManagerFactory"), "PanicAction");
			if ("system-exit".equals(panicAction)) {
				Object[] params = { throwable };
				server.invoke(
						new ObjectName("default:service=ShutdownService"),
						"exitImmediately", params, null);
			}
		} catch (AttributeNotFoundException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (MBeanException e) {
			e.printStackTrace();
		} catch (ReflectionException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void timerExpired(Timer timer) {
		HealthDebug.log("> HealthMonitorTask::run (10)");
		if (monSysTbl.size() != 0)
			return;
		for (MonitoredSystemTableEntry monitoredsystemtableentry : monSysTbl
				.values()) {
			HealthDebug
					.log("Health state of "
							+ monitoredsystemtableentry.getKey()
							+ " is "
							+ HealthState
									.mapToString(((HealthFeedback) monitoredsystemtableentry
											.getMBeanRef()).getHealthState()
											.getState()));
			if (!monitoredsystemtableentry.getIsCritical()
					|| ((HealthFeedback) monitoredsystemtableentry
							.getMBeanRef()).getHealthState().getState() != HealthState.HEALTH_FAILED)
				continue;
			HealthLogger.logErrorSubsystemFailed(monitoredsystemtableentry
					.getKey());
			HealthMonitorMService.subsystemFailed(monitoredsystemtableentry
					.getKey(), "health of critical service '"
					+ monitoredsystemtableentry.getKey() + "' failed");
			// T3Srvr.getT3Srvr().failed("health of critical service '" +
			// monitoredsystemtableentry.getKey() + "' failed");
		}
		HealthDebug.log("< HealthMonitorTask::run (20)");
		return;
	}

	public static HealthState[] getHealthStates() {
		if (monSysTbl.size() == 0)
			return null;
		HealthState ahealthstate[];
		ahealthstate = new HealthState[monSysTbl.size()];
		int i = 0;
		for (Iterator iterator = monSysTbl.values().iterator(); iterator
				.hasNext();) {
			MonitoredSystemTableEntry monitoredsystemtableentry = (MonitoredSystemTableEntry) iterator
					.next();
			HealthState healthstate = ((HealthFeedback) monitoredsystemtableentry
					.getMBeanRef()).getHealthState();
			ahealthstate[i] = new HealthState(healthstate.getState(),
					healthstate.getReasonCode());
			ahealthstate[i]
					.setSubsystemName(monitoredsystemtableentry.getKey());
			ahealthstate[i].setCritical(monitoredsystemtableentry
					.getIsCritical());
			ahealthstate[i].setMBeanName(monitoredsystemtableentry
					.getMBeanRef().getName());
			ahealthstate[i].setMBeanType(monitoredsystemtableentry
					.getMBeanRef().getType());
			i++;
		}
		return ahealthstate;
	}

	public int getHealthCheckIntervalSeconds() {
		return this.checkIntervalSeconds;
	}

	public void setHealthCheckIntervalSeconds(int interval) {
		this.checkIntervalSeconds = interval;
		if (this.state >= this.RUNNING) {
			this.restart();
		}
	}

}
