package com.onceas.systemplatform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class LinuxCpuUsage extends CpuUsage{
	private static Logger log = Logger.getLogger(LinuxCpuUsage.class.toString());
	private static LinuxCpuUsage INSTANCE;

	private LinuxCpuUsage() {

	}

	public static LinuxCpuUsage getInstance() {
		if (INSTANCE != null)
			return INSTANCE;
		else {
			INSTANCE = new LinuxCpuUsage();
			return INSTANCE;
		}
	}

	/**
	 * Purpose:采集CPU使用率
	 * 
	 * @param args
	 * @return double,CPU使用率,小于1
	 */
	@Override
	public double getCpuRatio() {
//		log.info("开始收集cpu使用率");
		float cpuUsage = 0;
		Process pro1, pro2;
		Runtime r = Runtime.getRuntime();
		try {
			String command = "cat /proc/stat";
			// 第一次采集CPU时间
			long startTime = System.currentTimeMillis();
			pro1 = r.exec(command);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(
					pro1.getInputStream()));
			String line = null;
			long idleCpuTime1 = 0, totalCpuTime1 = 0; // 分别为系统启动后空闲的CPU时间和总的CPU时间
			while ((line = in1.readLine()) != null) {
				if (line.startsWith("cpu")) {
					line = line.trim();
//					log.info(line);
					String[] temp = line.split("\\s+");
					idleCpuTime1 = Long.parseLong(temp[4]);
					for (String s : temp) {
						if (!s.equals("cpu")) {
							totalCpuTime1 += Long.parseLong(s);
						}
					}
//					log.info("IdleCpuTime: " + idleCpuTime1 + ", "
//							+ "TotalCpuTime" + totalCpuTime1);
					break;
				}
			}
			in1.close();
			pro1.destroy();
			try {
				Thread.sleep(CPUTIME);
			} catch (InterruptedException e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				log.severe("CpuUsage休眠时发生InterruptedException. "
						+ e.getMessage());
				log.severe(sw.toString());
			}
			// 第二次采集CPU时间
			long endTime = System.currentTimeMillis();
			pro2 = r.exec(command);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(
					pro2.getInputStream()));
			long idleCpuTime2 = 0, totalCpuTime2 = 0; // 分别为系统启动后空闲的CPU时间和总的CPU时间
			while ((line = in2.readLine()) != null) {
				if (line.startsWith("cpu")) {
					line = line.trim();
//					log.info(line);
					String[] temp = line.split("\\s+");
					idleCpuTime2 = Long.parseLong(temp[4]);
					for (String s : temp) {
						if (!s.equals("cpu")) {
							totalCpuTime2 += Long.parseLong(s);
						}
					}
//					log.info("IdleCpuTime: " + idleCpuTime2 + ", "
//							+ "TotalCpuTime" + totalCpuTime2);
					break;
				}
			}
			if (idleCpuTime1 != 0 && totalCpuTime1 != 0 && idleCpuTime2 != 0
					&& totalCpuTime2 != 0) {
				cpuUsage = 1 - (float) (idleCpuTime2 - idleCpuTime1)
						/ (float) (totalCpuTime2 - totalCpuTime1);
//				log.info("本节点CPU使用率为: " + cpuUsage);
			}
			in2.close();
			pro2.destroy();
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			log.severe("CpuUsage发生InstantiationException. " + e.getMessage());
			log.severe(sw.toString());
		}
		return cpuUsage*PERCENT;
	}
}
