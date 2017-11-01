// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ApplicationContextInternal.java

package com.onceas.util.application;

import com.onceas.work.WorkManagerCollection;

public interface ApplicationContextInternal
{
    public WorkManagerCollection getWorkManagerCollection();
    
    public String getApplicationName();
	
	public void setApplicationName(String applicationName);
	
	public void setWorkManagerCollection(WorkManagerCollection workManagerCollection);
}
