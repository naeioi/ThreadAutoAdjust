package com.onceas.systemplatform;

public class CpuInfo {
	private static CpuInfo instance;
	private double currentCpuUsage = 0;
	
	public static CpuInfo getInstance(){
		if(instance == null){
			instance = new CpuInfo();
		}
		return instance;
	}
	
	public int getCPUsage(){
		String osName = System.getProperty("os.name");	
		double cpuRatio = 0;        
		if (osName.toLowerCase().startsWith("windows")) {
			cpuRatio = WindowsCpuUsage.getInstance().getCpuRatio();}        
		else {
			cpuRatio = LinuxCpuUsage.getInstance().getCpuRatio();
			}
		return (int)cpuRatio;
	}
}
