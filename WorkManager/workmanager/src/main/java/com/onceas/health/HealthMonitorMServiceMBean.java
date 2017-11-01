package com.onceas.health;

import com.onceas.service.lifecycle.LifeCycleService;


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
public interface HealthMonitorMServiceMBean extends LifeCycleService {
	public int getHealthCheckIntervalSeconds();

	public void setHealthCheckIntervalSeconds(int interval);
}
