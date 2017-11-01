package com.onceas.work.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.onceas.descriptor.wm.dd.WmDD;
import com.onceas.util.jmx.MBeanServerLoader;

public class WorkManagerConfigRegister {
	
	private String appName = null;
	private String moduleName = null;
	private WmDD wmDD = null;
	
	private  ArrayList<ObjectName> workManagerConfigs = new ArrayList<ObjectName>();
	
	public WorkManagerConfigRegister(String appName,String moduleName,WmDD wmDD){
		this.appName = appName;
		this.moduleName = moduleName;
		this.wmDD = wmDD;		
	}
	/**
	 * 创建、配置并注册WorkManagerConfigMBean
	 *
	 */
	public void createAndRegister(){
		WorkManagerConfigMBean wmcBean = new WorkManagerConfig(appName,moduleName, wmDD.getWmDescriptor());
		// ugly: for uniform with deployment descriptor parser. 
		((WorkManagerConfig)wmcBean).open(wmDD.getRoot(), wmDD.getPath());
		ObjectName objName = null;
		try {
			String name = "onceas.work.config:type=WorkManagerConfig,ApplicationName="+
							appName+",ModuleName="+moduleName;
			objName = new ObjectName(name);		
			workManagerConfigs.add(objName);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		MBeanServer server = MBeanServerLoader.loadOnceas();
		try {
			server.registerMBean(wmcBean, objName);
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}
	}
	
	
	public void unregist(){
		MBeanServer server = MBeanServerLoader.loadOnceas();
		for(ObjectName objName : workManagerConfigs){
			try {
				server.unregisterMBean(objName);
			} catch (InstanceNotFoundException e) {
				e.printStackTrace();
			} catch (MBeanRegistrationException e) {
				e.printStackTrace();
			}
		}
		
	}
	private String getFilePath(URL url){
		String path = null;
		File warDir = new File(url.getFile());
		if(url.getProtocol().equals("file")&& warDir.isDirectory()){
			File onceasWebFile = new File(warDir, "WEB-INF/wm.xml");
			if(onceasWebFile.exists()){
				 try {
					path = onceasWebFile.getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return path;
			}
		}		
		return path;		
	}
}
