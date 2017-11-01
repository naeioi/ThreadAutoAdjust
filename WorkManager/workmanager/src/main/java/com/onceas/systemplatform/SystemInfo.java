package com.onceas.systemplatform;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Shell;

public class SystemInfo{

	private Shell sigarShell;
	private Sigar sigar;
	private static SystemInfo instance;
	private double currentCpuUsage = 0;
	
	private SystemInfo(){
		sigarShell = new Shell();
		sigar = sigarShell.getSigar();
	}
	
	public static SystemInfo getInstance(){
		if(instance == null){
			instance = new SystemInfo();
		}
		return instance;
	}
	
	public int getCPUsage(){
		CpuPerc[] cpuPerc;
		try {
			cpuPerc = sigar.getCpuPercList();
			double avgBusy=0;
			double tmp;
			for(int i=0;i<cpuPerc.length;i++){
				tmp = 1-cpuPerc[i].getIdle();
				avgBusy+=tmp;
			}
			currentCpuUsage = avgBusy*100/cpuPerc.length;
		} catch (SigarException e) {
			e.printStackTrace();
		}		
		return (int)currentCpuUsage;
	}	
}
