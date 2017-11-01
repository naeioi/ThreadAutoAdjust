/**
 * Service used to configure thread pool.
 * @author yk
 *
 */
package com.onceas.work;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import com.onceas.health.LowMemoryNotification;
import com.onceas.service.lifecycle.AbstractLifeCycleService;
import com.onceas.service.lifecycle.ServiceConfiguration;
import com.onceas.service.lifecycle.ServiceException;
import com.onceas.work.constraint.ServerFailureAction;
import com.onceas.work.constraint.ServerWorkManagerImpl;
import com.onceas.work.constraint.ThreadPriorityManager;

public class WorkManagerMService extends AbstractLifeCycleService implements
		WorkManagerMServiceMBean {
	private final static int DEFAULT_CAPACITY = 65235;

	private final static boolean DEFAULT_DISABLE_THREAD_PRIORITY = false;

	private static final String DEFAULT_JNDIName = "com.onceas.work.WorkManager";

	private String jndiName = DEFAULT_JNDIName;

	// Default stuck thread max time is 10 mins
	private static final int DEFAULT_STUCK_THREAD_MAX_TIME = 600 * 1000;

	// Default stuck thread timer interval is 10 mins
	private static final int DEFAULT_STUCK_THREAD_TIMER_INTERVAL = 600 * 1000;

	private int minPoolSize;

	private int maxPoolSize;

	private boolean disableThreadPriority = DEFAULT_DISABLE_THREAD_PRIORITY;

	private int capacity = DEFAULT_CAPACITY;

	private WorkManagerFactoryForJNDI wmFactory;

	private int stuckThreadTimerInterval = DEFAULT_STUCK_THREAD_TIMER_INTERVAL;

	private int stuckThreadMaxTime = DEFAULT_STUCK_THREAD_MAX_TIME;

	private int stuckThreadCount;

	private String failureAction;

	private int freeMemoryPercentHighThreshold;

	private int freeMemoryPercentLowThreshold;

	private String panicAction;

	private ServerFailureAction serverFailureAction;

	public void init() throws ServiceException {
		try {
			preamblePropertiesFromConfig();
			ThreadPriorityManager.getInstance().DISABLE_THREAD_PRIORITY = disableThreadPriority;
			serverFailureAction = new ServerFailureAction(stuckThreadMaxTime,
					stuckThreadCount);
			ServerWorkManagerFactory.initialize(capacity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		wmFactory = new WorkManagerFactoryForJNDI();
	}

	private void preamblePropertiesFromConfig() {
		//从配置文件config.properties获取属性初始配置
		setJNDIName(ServiceConfiguration.getJndiName());
		setCapacity(ServiceConfiguration.getCapacity());
		setDisableThreadPriority(ServiceConfiguration.getDisableThreadPriority());
		setFailureAction(ServiceConfiguration.getFailureAction());
		setFreeMemoryPercentHighThreshold(ServiceConfiguration.getFreeMemoryPercentHighThreshold());
		setFreeMemoryPercentLowThreshold(ServiceConfiguration.getFreeMemoryPercentLowThreshold());
		setMaxPoolSize(ServiceConfiguration.getMaxPoolSize());
		setMinPoolSize(ServiceConfiguration.getMinPoolSize());
		setPanicAction(ServiceConfiguration.getPanicAction());
		setStuckThreadCount(ServiceConfiguration.getStuckThreadCount());
		setStuckThreadMaxTime(ServiceConfiguration.getStuckThreadMaxTime());
		
		//NOTICE: stuckThreadTimeInterval is from corehealth service configuration
		setStuckThreadTimerInterval(ServiceConfiguration.getStuckThreadTimeInterval());	
	}

	public void start() throws ServiceException {
		try {
			InitialContext initCtx = new InitialContext();
			initCtx.bind(getJNDIName(), wmFactory);
			// workManager.setJNDIName(defaultJNDIName);
		} catch (NameNotFoundException e) {
			throw new ServiceException(e.getMessage());
		} catch (NamingException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1.getMessage());
		}

	}

	public void stop() throws ServiceException {
		try {
			InitialContext initCtx = new InitialContext();
			initCtx.unbind(getJNDIName());
		} catch (NamingException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void destroy() throws ServiceException {
		// this.workManager.clearWorkItemMap();
	}

	public String getJNDIName() {
		return this.jndiName;
	}

	public void setJNDIName(String jndiName) {
		this.jndiName = jndiName;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
		if (this.state >= this.RUNNING) {
			ServerWorkManagerImpl.SHARED_OVERLOAD_MANAGER.setCapacity(capacity);
		}
	}

	public boolean isDisableThreadPriority() {
		return disableThreadPriority;
	}

	public void setDisableThreadPriority(boolean disableThreadPriority) {
		this.disableThreadPriority = disableThreadPriority;
		ThreadPriorityManager.getInstance().DISABLE_THREAD_PRIORITY = disableThreadPriority;
	}

	public String getFailureAction() {
		return failureAction;
	}

	public void setFailureAction(String failureAction) {
		this.failureAction = failureAction;
	}

	public int getFreeMemoryPercentHighThreshold() {
		return freeMemoryPercentHighThreshold;
	}

	public void setFreeMemoryPercentHighThreshold(
			int freeMemoryPercentHighThreshold) {
		this.freeMemoryPercentHighThreshold = freeMemoryPercentHighThreshold;
		LowMemoryNotification.setHighThreshold(freeMemoryPercentHighThreshold);
	}

	public int getFreeMemoryPercentLowThreshold() {
		return freeMemoryPercentLowThreshold;
	}

	public void setFreeMemoryPercentLowThreshold(
			int freeMemoryPercentLowThreshold) {
		this.freeMemoryPercentLowThreshold = freeMemoryPercentLowThreshold;
		LowMemoryNotification.setLowThreshold(freeMemoryPercentLowThreshold);
	}

	public String getPanicAction() {
		return panicAction;
	}

	public void setPanicAction(String panicAction) {
		this.panicAction = panicAction;
	}

	public int getStuckThreadMaxTime() {
		return stuckThreadMaxTime;
	}

	public void setStuckThreadMaxTime(int stuckThreadMaxTime) {
		this.stuckThreadMaxTime = stuckThreadMaxTime;
	}

	public ServerFailureAction getServerFailureAction() {
		return serverFailureAction;
	}

	public int getStuckThreadCount() {
		return stuckThreadCount;
	}

	public void setStuckThreadCount(int stuckThreadCount) {
		this.stuckThreadCount = stuckThreadCount;
	}

	public int getStuckThreadTimerInterval() {
		return stuckThreadTimerInterval;
	}

	public void setStuckThreadTimerInterval(int stuckThreadTimerInterval) {
		this.stuckThreadTimerInterval = stuckThreadTimerInterval;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
		System.setProperty(WorkManagerConstant.minPoolSizeProp, Integer
				.toString(minPoolSize));
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		System.setProperty(WorkManagerConstant.maxPoolSizeProp, Integer
				.toString(maxPoolSize));
	}
}
