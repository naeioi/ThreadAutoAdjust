package com.onceas.descriptor.wm.dd;

import java.util.List;

public class ContextRequestDD {
	private String name;

	private List<ContextCaseDD> contextcasedds;

	public ContextRequestDD(String name, List<ContextCaseDD> contextcasedds) {
		this.name = name;
		this.contextcasedds = contextcasedds;
	}

	public String getName() {
		return name;
	}

	public List<ContextCaseDD> getContextCaseDDs() {
		return contextcasedds;
	}
}
