package com.onceas.descriptor.wm;

public interface WmDescriptor {
	Wm importXml(String fileName);
	void exportXml(String fileName, Wm onceasWeb);
}
