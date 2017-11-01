package com.onceas.service.server;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.management.ObjectName;

import com.onceas.health.CoreHealthMService;
import com.onceas.health.HealthMonitorMService;
import com.onceas.service.lifecycle.LifeCycleService;
import com.onceas.service.lifecycle.ServiceConfiguration;
import com.onceas.service.lifecycle.ServiceException;
import com.onceas.timers.internal.TimerMService;
import com.onceas.util.jmx.MBeanServerLoader;
import com.onceas.work.WorkManagerMService;

/**
 * start services according to the config.properties
 * 
 * @author yk
 * 
 */
public class WMServer {
	
	private static final Logger log = Logger.getLogger(WMServer.class.toString());
	
	private static AtomicBoolean started = new AtomicBoolean(false);

	private LifeCycleService wmService;

	private LifeCycleService timerService;

	private LifeCycleService coreHealthService;

	private LifeCycleService healthMonitorService;

	public void start() {
		wmService = new WorkManagerMService();

		if (ServiceConfiguration.isEnableTimer()) {
			timerService = new TimerMService();
		}
		if (ServiceConfiguration.isEnableCoreHealth() && timerService != null) {
			coreHealthService = new CoreHealthMService();
		}
		if (ServiceConfiguration.isEnableHealthMonitor()
				&& timerService != null) {
			healthMonitorService = new HealthMonitorMService();
		}
		initAndStart();
		started.set(true);
	}

	private void initAndStart() {
		try {
			wmService.init();
			log.info("WorkManagerService has inited.");
			wmService.start();
			log.info("WorkManagerService has started.");
			registerJMXService(wmService, ServiceConstants.WM_SERVICE_NAME);
			log.info("WorkManagerService has registered.");
			if (timerService != null) {
				timerService.init();
				timerService.start();
				registerJMXService(timerService,
						ServiceConstants.TIMER_SERVICE_NAME);

				if (coreHealthService != null) {
					coreHealthService.init();
					coreHealthService.start();
					registerJMXService(coreHealthService,
							ServiceConstants.COREHEALTH_SERVICE_NAME);
				}

				if (healthMonitorService != null) {
					healthMonitorService.init();
					healthMonitorService.start();
					registerJMXService(healthMonitorService,
							ServiceConstants.HEALTHMONITOR_SERVICE_NAME);
				}
			}
		} catch (ServiceException e) {
			log.severe("Exceptions occurs when INIT AND START WMServer."+"\n"+ e);
		}
	}

	private void registerJMXService(LifeCycleService service, String name) {
		try {
			MBeanServerLoader.loadDefault().registerMBean(service,
					new ObjectName(name));
		} catch (Exception e) {
			log.severe("Exceptions occurs when register Service[" + service + "],using name[" + name + "]"+"\n"+e);
		} 

	}

	public void stop() {
		started.set(false);
		try {
			if (wmService != null) {
				wmService.stop();
			}
			if (timerService != null) {
				timerService.stop();

				if (coreHealthService != null) {
					coreHealthService.stop();
				}

				if (healthMonitorService != null) {
					healthMonitorService.stop();
				}
			}
		} catch (ServiceException e) {
			log.severe("Exceptions occurs when stopping WMServer."+"\n"+e);
		}
	}

	public static boolean isStarted() {
		return started.get();
	}
}
