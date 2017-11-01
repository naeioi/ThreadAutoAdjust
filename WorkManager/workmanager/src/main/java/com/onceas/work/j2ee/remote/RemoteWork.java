package com.onceas.work.j2ee.remote;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import commonj.work.Work;

public class RemoteWork implements Work, Serializable {
	private String name = null;

	private int sleeptime = 0;

	private int sleepcount = 0;

	private int result = 0;

	public RemoteWork(String name, int sleeptime, int sleepcount) {
		this.name = name;
		this.sleeptime = sleeptime;
		this.sleepcount = sleepcount;
	}

	public boolean isDaemon() {
		// TODO Auto-generated method stub
		return false;
	}

	public void release() {
		// TODO Auto-generated method stub

	}

	public void run() {
		System.out.println(name + " start at Node(" + getIpAddress() + ")");
		try {
			for (int i = 0; i < sleepcount; i++) {
				result++;
				Thread.currentThread().sleep(sleeptime);
				System.out.println(name + " sleep " + (i + 1)
						+ " time(s) at Node(" + getIpAddress() + ")");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getIpAddress() {
		Enumeration enums = null;
		try {
			enums = NetworkInterface.getNetworkInterfaces();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "127.0.0.1";
		}
		while (enums.hasMoreElements()) {
			NetworkInterface net = (NetworkInterface) enums.nextElement();
			Enumeration enum2 = net.getInetAddresses();
			while (enum2.hasMoreElements()) {
				InetAddress address = (InetAddress) enum2.nextElement();
				if (address.getHostAddress() == null
						|| address.getHostAddress().equals("127.0.0.1")) {
					continue;
				}
				return address.getHostAddress();
			}
		}
		return "127.0.0.1";
	}

	public int getComputResult() {
		return result;
	}

	public String getName() {
		return name;
	}
}