package com.onceas.work.management.notification;

import java.util.EventObject;

public class WMUpdateEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//the proposed objcet stands for Constraints or Request Class
	// that stored the changes need to apply  
	private Object proposed;

	public WMUpdateEvent(Object source,Object proposed) {
		super(source);
		this.proposed = proposed;
	}
	
	public Object getProposed() {
		return proposed;
	}

	public void setProposed(Object proposed) {
		this.proposed = proposed;
	}


}
