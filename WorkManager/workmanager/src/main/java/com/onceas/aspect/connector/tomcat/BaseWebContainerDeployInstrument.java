package com.onceas.aspect.connector.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import com.onceas.descriptor.wm.dd.OnceasServletDD;
import com.onceas.descriptor.wm.dd.WmDD;
import com.onceas.descriptor.wm.dd.WorkManagerBeanDD;
import com.onceas.service.server.WMServer;
import com.onceas.util.application.ApplicationContextInternal;
import com.onceas.util.application.GlobalApplictaionContext;
import com.onceas.util.application.WebApplicationContext;
import com.onceas.work.WorkManagerCollection;

public class BaseWebContainerDeployInstrument {
	protected void doAfterDeploy(URL url, String appName){
		/*
		 * check whether the VM server has started
		 */
		if( !WMServer.isStarted()){
			synchronized (BaseWebContainerDeployInstrument.class) {
				if(!WMServer.isStarted()){
					final WMServer wmServer = new WMServer();
					wmServer.start();
					
					//register shutdown hook
					Runtime.getRuntime().addShutdownHook( new Thread(){
						public void run() {
							try {
								wmServer.stop();
							} catch (Throwable t) {
								t.printStackTrace();
							} finally {
								//Runtime.getRuntime().halt(0);
							}
						}
					});
				}
			}
		}
		// parse wm.xml according url
		WmDD wmDD = parse(url);
		if(wmDD != null){
			
			//stripping starting slash of appName
//			appName = stripStartSlash(appName);
			
			/**
			 * deploy workmanager Collection
			 */
			Iterator<WorkManagerBeanDD> wmIterator = wmDD.getWorkMangerBeanDDs();
			WorkManagerCollection workManagerCollection = null;
			if (wmIterator.hasNext()) {
				workManagerCollection = new WorkManagerCollection(appName);
				workManagerCollection.initialize();
				
				// populate each work-manager-bean
				while (wmIterator.hasNext()) {
					WorkManagerBeanDD workManagerBeanDD = wmIterator.next();;
					if(workManagerBeanDD.getName().equals("AppWorkManager")){
						workManagerCollection.populateAppWorkManager(workManagerBeanDD);
					}else{
						workManagerCollection.populate(null, workManagerBeanDD);
					}
				}
				// set the global access point to the WorkManagerCollection
				ApplicationContextInternal appContext = new WebApplicationContext();
				appContext.setApplicationName(appName);
				appContext.setWorkManagerCollection(workManagerCollection);
				GlobalApplictaionContext.setContext(appContext
						.getApplicationName(), appContext);

			}
			
			// ww workmanagement
			/**
			 * deploy schedule-policy
			 */
			Iterator<OnceasServletDD> moduleIterator = wmDD.getOnceasServlets();

			while (moduleIterator.hasNext()) {
				OnceasServletDD moduleDD = moduleIterator.next();
				String moduleName = moduleDD.getModuleName();
				String wmName = moduleDD.getSchedulePolicy();
				if(moduleName != null && wmName != null){
					if(workManagerCollection == null){
						workManagerCollection = new WorkManagerCollection(appName);
						workManagerCollection.initialize();
					}
					workManagerCollection.registModuleWorkManager(moduleName, wmName);
				}
			}
			/**
			 * register workmanager config TODO need to determine whether using
			 * copy url or original url
			 */
			if(workManagerCollection != null) {
				//统一使用WmDD
				 workManagerCollection.registerWorkManagerConfig(appName,
				 null, wmDD);
			}
	        // * end:
		}else{
			// log wm.xml does not exist
		}
	}
	
	private WmDD parse(URL url){
			File wmDDFile= null;
			// See if the warUrl is a directory
			File warDir = new File(url.getFile());
			if (url.getProtocol().equals("file")
					&& warDir.isDirectory() == true) {
				wmDDFile = new File(warDir, "WEB-INF/wm.xml");
				
			} else {
				// swallow it
				// throw new Exception("webcontainer.URLnotDirectory"
				// + url);
			}
			if(wmDDFile != null && wmDDFile.exists()){
				WmDD wmDD = new WmDD();
				try {
					wmDD.importXml(wmDDFile.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}				
				return wmDD;
			}
			return null;
	}
	
	protected String stripStartSlash(String s){
		if(s == null || !s.startsWith("/")) return s;
		
		int i = 0;
		for(; i < s.length(); i++){
			if(s.charAt(i) != '/') break;
		}
		
		return s.substring(i);
	}
}
