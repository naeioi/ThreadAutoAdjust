package com.onceas.service.lifecycle;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/*
 * Read the config.propeties for kinds of services configuration information, such as WorkManager and Timer etc. . 
 */
public class ServiceConfiguration {
	// public static String PRODUCT_HOME = System.getenv("WM_HOME") == null ?
	// System
	// .getProperty("WM__HOME")
	// : System.getenv("WM__HOME");

	private static Properties properties = null;

	private static String configFile = "config.properties";

	static Logger logger = Logger.getLogger("ServiceConfiguration");

	public static Properties getServiceProperties() {
		if (properties != null) {
			return properties;
		} else {
			InputStream in = ServiceConfiguration.class.getClassLoader()
					.getResourceAsStream(configFile);
			properties = new Properties();

			try {
				properties.load(in);
			} catch (Throwable e1) {
				throw new RuntimeException("Service configuration properties "
						+ configFile + " not found.");
			}
		}
		return properties;
	}

	/*
	 * the followings are service controllers.
	 */
	public static boolean isEnableWorkamanger() {
		return (1 == Integer.parseInt(getServiceProperties().getProperty(
				"workmanager_service", "0")));
	}

	public static boolean isEnableTimer() {
		return (1 == Integer.parseInt(getServiceProperties().getProperty(
				"timer_service", "0")));
	}

	public static boolean isEnableCoreHealth() {
		if(!isEnableTimer()){
			logger.info("Timer Service not started, CoreHealth Service must not be started either.");
			return false;
		}
		return (1 == Integer.parseInt(getServiceProperties().getProperty(
				"corehealth_service", "0")));
	}

	public static boolean isEnableHealthMonitor() {
		if(!isEnableTimer()){
			logger.info("Timer Service not started, HealthMonitor Service must not be started either.");
			return false;
		}
		return (1 == Integer.parseInt(getServiceProperties().getProperty(
				"healthmonitor_service", "0")));
	}

	/*
	 * workmanager service properties
	 */
	public static String getJndiName() {
		return getServiceProperties().getProperty("workmanager.jndiName",
				"com.onceas.work.WorManager");
	}

	public static int getCapacity() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"workmanager.capacity", "65535"));
	}

	public static boolean getDisableThreadPriority() {
		return Boolean.parseBoolean(getServiceProperties().getProperty(
				"workmanager.disableThreadPriority", "false"));
	}

	public static int getStuckThreadMaxTime() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"workmanager.stuckThreadMaxTime", "1800"));
	}

	public static String getFailureAction() {
		return getServiceProperties().getProperty("workmanager.failureAction",
				"shutdown");
	}

	public static String getPanicAction() {
		return getServiceProperties().getProperty("workmanager.panicAction",
				"system-exit");
	}

	public static int getFreeMemoryPercentLowThreshold() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"workmanager.freeMemoryPercentLowThreshold", "5"));
	}

	public static int getFreeMemoryPercentHighThreshold() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"workmanager.freeMemoryPercentHighThreshold", "60"));
	}

	public static int getStuckThreadCount() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"workmanager.stuckThreadCount", "com.onceas.work.WorManager"));
	}

	public static int getMinPoolSize() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"workmanager.minPoolSize", "30"));
	}

	public static int getMaxPoolSize() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"workmanager.maxPoolSize", "300"));
	}

	/*
	 * corehealth service properties
	 */
	public static int getCpuSampleTime() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"corehealth.cpuSampleTime", ("" + Integer.MAX_VALUE)));
	}

	public static int getJvmSampleTime() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"corehealth.jvmSampleTime", ("" + Integer.MAX_VALUE)));
	}

	public static int getStuckThreadTimeInterval() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"corehealth.stuckThreadTimeInterval", "600"));
	}

	/*
	 * healthmonitor service properties
	 */
	public static int getHealthCheckIntervalSeconds() {
		return Integer.parseInt(getServiceProperties().getProperty(
				"healthmonitor.healthCheckIntervalSeconds", "30"));
	}
}
