package com.onceas.health;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.onceas.runtime.RuntimeAccess;
import com.onceas.runtime.server.ServerHealthRuntime;
import com.onceas.runtime.server.ServerHealthRuntimeMBean;
import com.onceas.runtime.work.ExecuteThreadRuntimeMBean;
import com.onceas.runtime.work.ThreadPoolRuntimeMBean;
import com.onceas.service.lifecycle.AbstractLifeCycleService;
import com.onceas.service.lifecycle.ServiceConfiguration;
import com.onceas.systemplatform.CpuInfo;
import com.onceas.systemplatform.GCMonitorThread;
import com.onceas.systemplatform.ServerInfo;
import com.onceas.systemplatform.SystemInfo;
import com.onceas.systemplatform.VM;
import com.onceas.timers.Timer;
import com.onceas.timers.TimerListener;
import com.onceas.timers.internal.TimerManagerImpl;
import com.onceas.work.WorkManagerFactory;

public final class CoreHealthMService extends AbstractLifeCycleService implements
		MemoryListener, CoreHealthMServiceMBean {
	private static final class ThreadMonitoringTimer implements TimerListener {
		public void timerExpired(Timer timer) {

			WorkManagerFactory.getInstance().getSystem().schedule(
					new Runnable() {
						public void run() {
							checkDeadlockedThreads();
							checkStuckThreads();
						}
					});
		}

		private void checkDeadlockedThreads() {
			if (alreadyDeadlocked)
				return;
			String s = VM.getVM().dumpDeadlockedThreads();
			if (s == null) {
				return;
			} else {
				HealthLogger.logDeadlockedThreads(s);
				HealthMonitorMService.subsystemFailed("core",
						"Thread deadlock detected");
				alreadyDeadlocked = true;
				return;
			}
		}

		private void checkStuckThreads() {
			boolean flag = true;
			ThreadPoolRuntimeMBean threadpoolruntimembean = RuntimeAccess
					.getRuntimeAccess().getThreadPoolRuntime();
			if (threadpoolruntimembean != null) {
				ExecuteThreadRuntimeMBean aexecutethread[] = threadpoolruntimembean
						.getStuckExecuteThreads();
				if(aexecutethread == null){
					// no stuck thread
					return;
				}
				int count = threadpoolruntimembean.getExecuteThreadTotalCount();
				logStuckThreads(aexecutethread, count, threadpoolruntimembean
						.getName());
				if (count == aexecutethread.length) {
					updateHealthState(HealthState.HEALTH_WARN,
							"All Threads in the pool "
									+ threadpoolruntimembean.getName()
									+ " are stuck.");
				} else {
					updateHealthState(HealthState.HEALTH_OK, "");
					flag = false;
				}
			}
			if (flag)
				HealthMonitorMService.subsystemFailed("core",
						"All threads in pool are stuck");
		}

		private void logStuckThreads(
				ExecuteThreadRuntimeMBean aexecutethread[], int i, String s) {
			long l = System.currentTimeMillis();
			for (int j = 0; j < aexecutethread.length; j++) {
				ExecuteThreadRuntimeMBean executethread = aexecutethread[j];
				long l1 = l - executethread.getCurrentRequestStartTime();
				if (logStuckThreadMessage(l1)) {
					String s1 = VM.getVM().threadDumpAsString(
							executethread.getExecuteThread());
					HealthLogger.logWarnPossibleStuckThread(executethread
							.getName(), l1 / 1000L, executethread
							.getCurrentRequest(), stuckThreadMaxTime, s1);
				}
			}
		}

		private void updateHealthState(int state, String reason) {
			CoreHealthMService.serverHealthRuntimeMBean.setHealthState(state,
					reason);
		}

		private boolean logStuckThreadMessage(long l) {
			return l > stuckThreadMaxTime
					&& l < stuckThreadMaxTime + 2L * timerInterval;
		}

		private final long stuckThreadMaxTime;

		private final long timerInterval;

		private boolean alreadyDeadlocked;

		ThreadMonitoringTimer(long l, long l1) {
			alreadyDeadlocked = false;
			stuckThreadMaxTime = l;
			timerInterval = l1;
		}
	}

	private static final class JVMMonitorTimer implements TimerListener {

		public void timerExpired(Timer timer) {
			WorkManagerFactory.getInstance().getSystem().schedule(
					new Runnable() {
						public void run() {
							if (isJvmMonitorStart) {
								sampleJVMData();
							}
						}
					});
		}

		public void sampleJVMData() {
			/**
			 * int num = 0; for (int i = 0; i < jvmSampleData.length; i++) { if
			 * (jvmSampleData[i] > 0) { num++; } else { break; } } long l =
			 * Runtime.getRuntime().freeMemory(); long l1 =
			 * Runtime.getRuntime().totalMemory(); jvmSampleData[num] = (int) (l -
			 * l1);
			 */
			long free = Runtime.getRuntime().freeMemory();
			long total = Runtime.getRuntime().totalMemory();
			if (jvmSampleData.size() == 240) {
				jvmSampleData.remove(0);
				sampleStartTime = new Date(sampleStartTime.getTime()
						+ jvmSampleTime * 1000L);
			}
			jvmSampleData.add((total - free) / 1024L / 1024L);

			/**
			 * System.out.println((total - free)/1024L/1024L);
			 * System.out.println("StartTime"+sampleStartTime.getTime());
			 */
			jvmPercent = (total - free) * 100L / total;
			// System.out.println(jvmPercent);
			// System.out.println("Max:
			// "+(Runtime.getRuntime().maxMemory())/1024L/1024L);
		}

	}

	private static final class CPUMonitorTimer implements TimerListener {

		public void timerExpired(Timer timer) {
			WorkManagerFactory.getInstance().getSystem().schedule(
					new Runnable() {
						public void run() {
							if (isCpuMonitorStart) {
								sampleCPUData();
							}
						}
					});
		}

		public void sampleCPUData() {

			if (cpuSampleData.size() == 240) {
				cpuSampleData.remove(0);
				cpuSampleStartTime = new Date(cpuSampleStartTime.getTime()
						+ cpuSampleTime * 1000L);
			}

			cpuSampleData.add(SystemInfo.getInstance().getCPUsage());
//			Cpu.getInstance().getCPUsage();

			/**
			 * System.out.println((total - free)/1024L/1024L);
			 * System.out.println("StartTime"+sampleStartTime.getTime());
			 * 
			 * System.out.println(SystemInfo.getCPUsage());
			 * System.out.println("start"); System.out.println("Max:
			 * "+(Runtime.getRuntime().maxMemory())/1024L/1024L);
			 */
		}

	}

	// private static final AuthenticatedSubject kernelId =
	// (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
	// Default stuck thread timer interval is 10 mins
	private static final int DEFAULT_STUCK_THREAD_TIMER_INTERVAL = 600;

	private Timer healthTimer;

	private static ServerHealthRuntimeMBean serverHealthRuntimeMBean;

	private int stuckThreadTimerInterval = DEFAULT_STUCK_THREAD_TIMER_INTERVAL;

	private int stuckThreadMaxTime;

	private int freeMemoryPercentHighThreshold;

	private int freeMemoryPercentLowThreshold;

	private static List jvmSampleData = new ArrayList(240);

	private static List cpuSampleData = new ArrayList(240);

	private static int jvmSampleTime;

	private static int cpuSampleTime;

	private static Date sampleStartTime;

	private static Date cpuSampleStartTime;

	private static long jvmPercent;

	private Timer jvmTimer;

	private Timer cpuTimer;

	private static boolean isJvmMonitorStart = true;

	private static boolean isCpuMonitorStart = true;

	public CoreHealthMService() {
	}

	public void init() {
		preamblePropertiesFromConfig();
		MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
				.loadOnceas();
		try {
			freeMemoryPercentHighThreshold = (Integer) server.getAttribute(
					new ObjectName("onceas.work:service=WorkManagerFactory"),
					"FreeMemoryPercentHighThreshold");
			freeMemoryPercentLowThreshold = (Integer) server.getAttribute(
					new ObjectName("onceas.work:service=WorkManagerFactory"),
					"FreeMemoryPercentLowThreshold");
			stuckThreadMaxTime = (Integer) server.getAttribute(new ObjectName(
					"onceas.work:service=WorkManagerFactory"),
					"StuckThreadMaxTime");
			try {
				InitialContext initCtx = new InitialContext();
				initCtx.bind(ServerInfo.JNDI_NAME, new ServerInfo());
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	private void preamblePropertiesFromConfig() {
		//从配置文件config.properties获取属性初始配置
		setStuckThreadTimerInterval(ServiceConfiguration.getStuckThreadTimeInterval());
		setJvmSampleTime(ServiceConfiguration.getJvmSampleTime());
		setCpuSampleTime(ServiceConfiguration.getCpuSampleTime());
	}

	public void start() {
		try {
			serverHealthRuntimeMBean = ServerHealthRuntime
					.createServerHealthRuntimeMBean("onceas", null);
			MBeanServer server = com.onceas.util.jmx.MBeanServerLoader
					.loadOnceas();
			server.registerMBean(serverHealthRuntimeMBean,
					serverHealthRuntimeMBean.getObjectName());
			HealthMonitorMService.register("ServerHealthRuntime",
					serverHealthRuntimeMBean, false);

			GCMonitorThread.init();

			// Low Memory Protection
			LowMemoryNotification.initialize(freeMemoryPercentLowThreshold,
					freeMemoryPercentHighThreshold);
			LowMemoryNotification.addMemoryListener(this);

			TimerManagerImpl timermanagerimpl = TimerManagerImpl
					.getTimerManager("onceas.health.ThreadMonitor",
							WorkManagerFactory.getInstance().getSystem());
			healthTimer = timermanagerimpl.schedule(new ThreadMonitoringTimer(
					stuckThreadMaxTime, stuckThreadTimerInterval * 1000), 0L,
					stuckThreadTimerInterval * 1000);
			TimerManagerImpl timermanagerimpl1 = TimerManagerImpl
					.getTimerManager("onceas.health.JVMMonitor",
							WorkManagerFactory.getInstance().getSystem());
			jvmTimer = timermanagerimpl1.schedule(new JVMMonitorTimer(), 0L,
					jvmSampleTime * 1000);
			sampleStartTime = new Date();
			TimerManagerImpl timermanagerimpl2 = TimerManagerImpl
					.getTimerManager("onceas.health.CPUMonitor",
							WorkManagerFactory.getInstance().getSystem());
			cpuTimer = timermanagerimpl2.schedule(new CPUMonitorTimer(), 0L,
					cpuSampleTime * 1000);
			cpuSampleStartTime = new Date();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void stop() {
		halt();

		try {
			InitialContext initCtx = new InitialContext();
			initCtx.unbind(ServerInfo.JNDI_NAME);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void halt() {
		try {
			// HealthMonitorMService.unregister("ServerRuntime");
			if (healthTimer != null)
				healthTimer.cancel();
			if (jvmTimer != null)
				jvmTimer.cancel();
			if (cpuTimer != null)
				cpuTimer.cancel();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void restartThreadHealthTimer() {
		if (healthTimer != null)
			healthTimer.cancel();
		TimerManagerImpl timermanagerimpl = TimerManagerImpl.getTimerManager(
				"onceas.health.ThreadMonitor", WorkManagerFactory.getInstance()
						.getSystem());
		healthTimer = timermanagerimpl.schedule(new ThreadMonitoringTimer(
				stuckThreadMaxTime, stuckThreadTimerInterval * 1000), 0L,
				stuckThreadTimerInterval * 1000);
	}

	private void restartJvmTimer() {
		if (jvmTimer != null)
			jvmTimer.cancel();
		clearData();
		TimerManagerImpl timermanagerimpl1 = TimerManagerImpl.getTimerManager(
				"onceas.health.JVMMonitor", WorkManagerFactory.getInstance()
						.getSystem());
		jvmTimer = timermanagerimpl1.schedule(new JVMMonitorTimer(), 0L,
				jvmSampleTime * 1000);
		sampleStartTime = new Date();
	}

	private void restartCpuTimer() {
		if (cpuTimer != null)
			cpuTimer.cancel();
		clearCpuData();
		TimerManagerImpl timermanagerimpl2 = TimerManagerImpl.getTimerManager(
				"onceas.health.CPUMonitor", WorkManagerFactory.getInstance()
						.getSystem());
		cpuTimer = timermanagerimpl2.schedule(new CPUMonitorTimer(), 0L,
				cpuSampleTime * 1000);
		cpuSampleStartTime = new Date();

	}

	public void memoryChanged(final MemoryEvent event) {
		WorkManagerFactory.getInstance().getSystem().schedule(new Runnable() {

			public void run() {
				if (event.getEventType() == 1) {
					CoreHealthMService.serverHealthRuntimeMBean.setHealthState(
							HealthState.HEALTH_OVERLOADED,
							"server is low on memory");
					HealthLogger.logLowMemory("server is low on memory");
				}
				if (event.getEventType() == 0) {
					CoreHealthMService.serverHealthRuntimeMBean.setHealthState(
							HealthState.HEALTH_OK, null);
				}
			}
		});
	}

	private void clearData() {
		jvmSampleData.clear();
	}

	private void clearCpuData() {
		cpuSampleData.clear();
	}

	private void initServerInfo() {

	}

	public int getStuckThreadTimerInterval() {
		return stuckThreadTimerInterval;
	}

	public void setStuckThreadTimerInterval(int stuckThreadTimerInterval) {
		this.stuckThreadTimerInterval = stuckThreadTimerInterval;
		if (this.state >= this.RUNNING) {
			restartThreadHealthTimer();
		}
	}

	public int getJvmSampleTime() {
		return this.jvmSampleTime;
	}

	public List getJvmSampleData() {
		return jvmSampleData;
	}

	public void setJvmSampleTime(int time) {
		jvmSampleTime = time;
		if (this.state >= this.RUNNING) {
			restartJvmTimer();
		}
	}

	public Date getSampleStartTime() {
		return this.sampleStartTime;
	}

	public long getJvmPercent() {
		return this.jvmPercent;
	}

	public boolean getIsJvmMonitorStart() {
		return this.isJvmMonitorStart;
	}

	public void setIsJvmMonitorStart(boolean isStart) {

		this.isJvmMonitorStart = isStart;
		if (this.state >= this.RUNNING) {
			restartJvmTimer();
		}

	}

	public int getCpuSampleTime() {
		return this.cpuSampleTime;
	}

	public List getCpuSampleData() {
		return cpuSampleData;
	}

	public void setCpuSampleTime(int time) {
		cpuSampleTime = time;
		if (this.state >= this.RUNNING) {
			restartCpuTimer();
		}
	}

	public Date getCpuSampleStartTime() {
		return this.cpuSampleStartTime;
	}

	public boolean getIsCpuMonitorStart() {
		return this.isCpuMonitorStart;
	}

	public void setIsCpuMonitorStart(boolean isStart) {

		this.isCpuMonitorStart = isStart;
		if (this.state >= this.RUNNING) {
			restartCpuTimer();
		}
	}
}
