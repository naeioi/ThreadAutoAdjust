package com.onceas.health;

import java.util.Date;
import java.util.List;

import com.onceas.service.lifecycle.LifeCycleService;

public interface CoreHealthMServiceMBean extends LifeCycleService {
	public int getStuckThreadTimerInterval();

	public void setStuckThreadTimerInterval(int stuckThreadTimerInterval);

	public void setJvmSampleTime(int jvmSampleTime);

	public int getJvmSampleTime();

	public List getJvmSampleData();

	public long getJvmPercent();

	public Date getSampleStartTime();

	public boolean getIsJvmMonitorStart();

	public void setIsJvmMonitorStart(boolean isStart);

	public void setCpuSampleTime(int cpuSampleTime);

	public int getCpuSampleTime();

	public List getCpuSampleData();

	public Date getCpuSampleStartTime();

	public boolean getIsCpuMonitorStart();

	public void setIsCpuMonitorStart(boolean isStart);
}