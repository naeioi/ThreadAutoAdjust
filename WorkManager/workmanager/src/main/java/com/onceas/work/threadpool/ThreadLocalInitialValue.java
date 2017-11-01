package com.onceas.work.threadpool;

public class ThreadLocalInitialValue {

	public ThreadLocalInitialValue() {
	}

	public ThreadLocalInitialValue(boolean flag) {
		inherit = flag;
	}

	protected Object childValue(Object obj) {
		if (inherit)
			return obj;
		else
			return initialValue();
	}

	protected Object initialValue() {
		return null;
	}

	protected Object resetValue(Object obj) {
		return initialValue();
	}

	private boolean inherit;
}
