package com.onceas.systemplatform;

import java.io.SerializablePermission;

public final class JDK {

	protected JDK() {
		String s;
		majorVersion = -1;
		minorVersion = -1;
		microVersion = -1;
		additionalVersion = "";
		s = System.getProperty("java.version");
		int i;
		int j;
		for (i = 0; i < s.length() && !Character.isDigit(s.charAt(i)); i++)
			;
		j = s.indexOf('.', i);
		if (j == -1)
			return;
		try {
			majorVersion = Integer.parseInt(s.substring(i, j));
		} catch (NumberFormatException numberformatexception) {
			throw numberformatexception;
		}
		i = ++j;
		for (; j < s.length() && Character.isDigit(s.charAt(j)); j++)
			;
		if (i == j)
			return;
		try {
			try {
				minorVersion = Integer.parseInt(s.substring(i, j));
			} catch (NumberFormatException numberformatexception1) {
				throw numberformatexception1;
			}
			i = j + 1;
			j = s.length();
			do {
				if (j <= i)
					break;
				try {
					microVersion = Integer.parseInt(s.substring(i, j));
					break;
				} catch (NumberFormatException numberformatexception2) {
					j--;
				}
			} while (true);
			additionalVersion = s.substring(j, s.length());
		} catch (Exception exception) {
			System.err.println("error parsing java.version: " + s);
		}
		return;
	}

	protected JDK(int i, int j, int k, String s) {
		majorVersion = -1;
		minorVersion = -1;
		microVersion = -1;
		additionalVersion = "";
		majorVersion = i;
		minorVersion = j;
		microVersion = k;
		additionalVersion = s;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getMicroVersion() {
		return microVersion;
	}

	public String getAdditionalVersion() {
		return additionalVersion;
	}

	public static synchronized JDK getJDK() {
		if (jdk != null) {
			return jdk;
		} else {
			jdk = new JDK();
			String s = System.getProperty("java.vendor");
			noMaxMem = s.startsWith("Microsoft") || s.startsWith("SuperCede")
					|| s.startsWith("Tower");
			return jdk;
		}
	}

	public boolean checkMemory(long l, long l1) {
		if (noMaxMem)
			return true;
		else
			return l > l1 / 4L;
	}

	public boolean isEnableReplaceObject(Class class1) {
		try {
			SecurityManager securitymanager = System.getSecurityManager();
			if (securitymanager != null)
				securitymanager.checkPermission(new SerializablePermission(
						"enableSubstitution"));
			return true;
		} catch (SecurityException securityexception) {
			return false;
		}
	}

	private static JDK jdk = null;

	private static boolean noMaxMem;

	private int majorVersion;

	private int minorVersion;

	private int microVersion;

	private String additionalVersion;

}
