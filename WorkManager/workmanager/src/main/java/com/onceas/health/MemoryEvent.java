package com.onceas.health;

public final class MemoryEvent {

	MemoryEvent(int i) {
		eventType = i;
	}

	public int getEventType() {
		return eventType;
	}

	public static final int MEMORY_OK = 0;

	public static final int MEMORY_LOW = 1;

	private int eventType;
}
