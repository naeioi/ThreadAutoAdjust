package com.onceas.aspect.connector;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * hold context infomation about the monitored request
 * 
 * @author yk
 * 
 */
public abstract class RequestContext {
	public final Lock lock = new ReentrantLock();

	public final Condition workComplete = lock.newCondition();

	/** template method: get information from the context */

	/**
	 * get the application name
	 */
	public abstract String getApplicationName();

	/**
	 * get the component name within the application
	 */
	public abstract String getModuleName();

}
