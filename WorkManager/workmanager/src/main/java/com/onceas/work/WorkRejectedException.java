package com.onceas.work;

import java.rmi.RemoteException;

public class WorkRejectedException extends RemoteException {

	public WorkRejectedException() {
	}

	public WorkRejectedException(String s) {
		super(s);
	}

	public WorkRejectedException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
