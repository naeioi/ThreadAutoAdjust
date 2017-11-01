package com.onceas.work;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import com.onceas.work.j2ee.J2EEWorkManager;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class WorkManagerFactoryForJNDI implements Referenceable, ObjectFactory {
	private static final String TYPE = "WorkManagerFacotry.type";

	private static final String PADDING = "@";

	private static final String DEFAULT = "default";

	private String workManagerName;

	private String appName;

	private String refAdd;

	public WorkManagerFactoryForJNDI() {
		workManagerName = null;
		appName = null;
		refAdd = DEFAULT;
	}

	public WorkManagerFactoryForJNDI(String appName, String workManagerName) {
		this.workManagerName = workManagerName;
		this.appName = appName;
		refAdd = workManagerName + PADDING + appName;
	}

	public Reference getReference() throws NamingException {
		Reference ref = new Reference(commonj.work.WorkManager.class.getName(),
				new StringRefAddr(TYPE, refAdd), getClass().getName(), null);
		return ref;
	}

	// ObjectFactory implementation ----------------------------------

	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable environment) {
		if (obj instanceof Reference) {
			Reference ref = (Reference) obj;
			String refAdd = (String) ref.get(TYPE).getContent();
			if (refAdd.equals(DEFAULT)) {
				return J2EEWorkManager.getDefault();
			}
			String[] str = refAdd.split(PADDING);
			return J2EEWorkManager.get(str[1], null, str[0]);
		}
		return null;
	}
}
