package com.onceas.service.lifecycle;

public interface StateManageable {

  public static final int STARTING = 0;
  public static final int RUNNING = 1;
  public static final int STOPPING = 2;
  public static final int STOPPED = 3;
  public static final int FAILED = 4;
  public static final int CREATED = 5;
  public static final int DESTROYED = 6;
  public static final int REGISTERED = 7;
  public static final int UNREGISTERED = 8;
  public static final int UNINITIALIZED = 9;  

  public static final String STATE_STARTING = "Starting";
  public static final String STATE_RUNNING = "Running";
  public static final String STATE_STOPPING = "Stopping";
  public static final String STATE_STOPPED = "Stopped";
  public static final String STATE_FAILED = "Failed";
  public static final String OBJECT_CREATED = "Created";
  public static final String OBJECT_DELETED = "Destroyed";
  public static final String OBJECT_REGISTERED = "Registered";
  public static final String OBJECT_UNREGISTERED = "Unregistered";
  public static final String OBJECT_UNINITIALIZED = "Uninitialized";  

  public static final String[] states = {
	  STATE_STARTING, STATE_RUNNING, STATE_STOPPING, STATE_STOPPED, STATE_FAILED, OBJECT_CREATED,
	  OBJECT_DELETED, OBJECT_REGISTERED, OBJECT_UNREGISTERED, OBJECT_UNINITIALIZED,
  };

  public int getState();

  public String getStateString();

}
