package com.onceas.work;

import com.onceas.service.lifecycle.LifeCycleService;
import com.onceas.work.constraint.ServerFailureAction;

/**
 * <p>
 * Title: WorkManagerMServiceMBean.java
 * </p>
 * 
 * <p>
 * Description: 基于OnceAS平台
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: 中国科学院软件研究所
 * </p>
 * 
 * @author 张磊
 * @version 1.0
 * 
 */
public interface WorkManagerMServiceMBean extends LifeCycleService {
	public String getJNDIName();

	public void setJNDIName(String jndiName);

	public int getCapacity();

	public void setCapacity(int capacity);

	public boolean isDisableThreadPriority();

	public void setDisableThreadPriority(boolean disableThreadPriority);

	public String getFailureAction();

	public void setFailureAction(String failureAction);

	public int getFreeMemoryPercentHighThreshold();

	public void setFreeMemoryPercentHighThreshold(
            int freeMemoryPercentHighThreshold);

	public int getFreeMemoryPercentLowThreshold();

	public void setFreeMemoryPercentLowThreshold(
            int freeMemoryPercentLowThreshold);

	public String getPanicAction();

	public void setPanicAction(String panicAction);

	public int getStuckThreadMaxTime();

	public void setStuckThreadMaxTime(int stuckThreadMaxTime);

	public ServerFailureAction getServerFailureAction();

	public int getMinPoolSize();

	public void setMinPoolSize(int minPoolSize);

	public int getMaxPoolSize();

	public void setMaxPoolSize(int maxPoolSize);
}
