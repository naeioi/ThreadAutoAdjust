package com.onceas.work;

public final class DebugCategory {

	public DebugCategory(String s) {
		enabled = false;
		name = s;
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean flag) {
		enabled = flag;
	}

	private final String name;

	private boolean enabled = true;
}
