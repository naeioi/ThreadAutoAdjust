// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GCListener.java

package com.onceas.systemplatform;

public interface GCListener {

	public abstract void onGarbageCollection(
            GarbageCollectionEvent garbagecollectionevent);
}
