package com.onceas.work.constraint;

import java.security.Principal;
import java.security.acl.Group;

import javax.security.auth.Subject;

import com.onceas.util.collection.ConcurrentHashMap;
import com.onceas.util.security.SecurityContext;

public final class ContextRequestClass extends ServiceClassSupport {

	public ContextRequestClass(String s) {
		super(s);
		groupMap = new ConcurrentHashMap();
		usernameMap = new ConcurrentHashMap();
		name = s;
		defaultRequestClass = new FairShareRequestClass(s);
	}

	public void addUser(String s, RequestClass requestclass) {
		usernameMap.put(s, requestclass);
	}

	public void addGroup(String s, RequestClass requestclass) {
		groupMap.put(s, requestclass);
	}

	public RequestClass getEffective() {
		RequestClass requestclass = null;
		Principal userPrincipal = SecurityContext.getPrincipal();
		if (userPrincipal == null)
			return defaultRequestClass;
		requestclass = (RequestClass) usernameMap.get(userPrincipal.getName());
		if (requestclass != null)
			return requestclass;
		Subject subject = SecurityContext.getSubject();
		if (subject == null)
			return defaultRequestClass;
		Group group = SecurityContext.getGroup(subject);
		if (group == null)
			return defaultRequestClass;
		requestclass = (RequestClass) groupMap.get(group.getName());
		/**
		 * if(requestclass != null) return requestclass;
		 * 
		 * requestclass = (RequestClass)groupMap.get("everyone");
		 */
		if (requestclass != null)
			return requestclass;
		else
			return defaultRequestClass;
	}

	public String getName() {
		return name;
	}

	public void timeElapsed(long l, ServiceClassesStats serviceclassesstats) {
	}

	public void cleanup() {
		super.cleanup();
		defaultRequestClass.cleanup();
	}

	// private static final AbstractSubject kernelId =
	// (AbstractSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
	private String name;

	private RequestClass defaultRequestClass;

	private ConcurrentHashMap groupMap;

	private ConcurrentHashMap usernameMap;
	// private final SubjectManager subjectManager =
	// SubjectManager.getSubjectManager();
}
