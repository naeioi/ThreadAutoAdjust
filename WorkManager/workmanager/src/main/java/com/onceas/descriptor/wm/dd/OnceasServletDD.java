package com.onceas.descriptor.wm.dd;

import com.onceas.descriptor.wm.OnceasServlet;
import com.onceas.descriptor.wm.TagStringValueType;

/**
 * onceas-servlet与workmanager并列
 * 
 * @author yk
 * 
 */
public class OnceasServletDD {
	private OnceasServlet onceasServlet;

	public OnceasServletDD(OnceasServlet onceasServlet) {
		this.onceasServlet = onceasServlet;
	}

	public String getModuleName() {
		return this.onceasServlet.getServletName().getValue().trim();
	}

	/*
	 * defalut processing the first schedule policy
	 * 
	 * TODO:process the list
	 */
	public String getSchedulePolicy() {
		return ((TagStringValueType) (onceasServlet.getSchedulePolicy().get(0)))
				.getValue().trim();
	}

	public void setSchedulePolicy(String schedulePolicy) {
		// can not set ;
	}
}
