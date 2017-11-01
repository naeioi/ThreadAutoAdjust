// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GarbageCollectionEvent.java

package com.onceas.systemplatform;

public final class GarbageCollectionEvent {

	GarbageCollectionEvent(int i) {
		eventType = i;
	}

	public int getEventType() {
		return eventType;
	}

	public static final int GC_COLLECTION_MAJOR = 0;

	public static final int GC_COLLECTION_MINOR = 1;

	private int eventType;
}
