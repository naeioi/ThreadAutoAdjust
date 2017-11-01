package com.onceas.descriptor.wm.dd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.onceas.descriptor.wm.OnceasServlet;
import com.onceas.descriptor.wm.Wm;
import com.onceas.descriptor.wm.WmDescriptor;
import com.onceas.descriptor.wm.WorkManagerBean;
import com.onceas.descriptor.wm.impl.WmDescriptorImpl;

/*
 * root dd
 */
public class WmDD {
	private Wm root = null;

	private WmDescriptor wmDescriptor = null;
	
	private String path = null;

	private ArrayList<WorkManagerBeanDD> workManagerBeanDDs = new ArrayList<WorkManagerBeanDD>();

	private ArrayList<OnceasServletDD> onceasServletDDs = new ArrayList<OnceasServletDD>();

	public void importXml(String xmlFile) {
		// parse the xml
		this.path = xmlFile;
		wmDescriptor = new WmDescriptorImpl();
		root = wmDescriptor.importXml(xmlFile);
		if (root != null) {
			// parse onceas servlet dd
			List<OnceasServlet> servlets = (List<OnceasServlet>) root
					.getOnceasServlet();
			for (OnceasServlet s : servlets) {
				onceasServletDDs.add(new OnceasServletDD(s));
			}

			// parse workmanager beans
			if (root.getWorkManager() != null) {
				List<WorkManagerBean> wmBeans = (List<WorkManagerBean>) root
						.getWorkManager().getWorkManagerBean();
				for (WorkManagerBean wmb : wmBeans) {
					workManagerBeanDDs.add(new WorkManagerBeanDD(wmb));
				}
			}
		}
	}

	/**
	 * Return an iterator of the work-manager-bean. return iterator of
	 * workmanagerbeanDDs
	 */
	public Iterator<WorkManagerBeanDD> getWorkMangerBeanDDs() {
		return workManagerBeanDDs.iterator();
	}

	/**
	 * Return an iterator of the onceas-servlet. return iterator of
	 * schedule-policy
	 */
	public Iterator<OnceasServletDD> getOnceasServlets() {
		return onceasServletDDs.iterator();
	}

	public Wm getRoot() {
		return root;
	}

	public WmDescriptor getWmDescriptor() {
		return wmDescriptor;
	}

	public String getPath() {
		return path;
	}

}
