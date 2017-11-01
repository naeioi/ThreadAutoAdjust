package com.onceas.util.application;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.onceas.service.server.WMServer;
import com.onceas.work.j2ee.J2EEWorkManager;

import commonj.work.WorkManager;

public class GlobalApplictaionContext {

	private static final Logger log = Logger.getLogger(GlobalApplictaionContext.class.toString());
	
	private static ConcurrentHashMap<String, ApplicationContextInternal> contextMap = new ConcurrentHashMap<String, ApplicationContextInternal>();
	
	private static Context context;
	
	private  static void initContext(){
		if(context != null){
			return ;
		}
		synchronized (GlobalApplictaionContext.class) {
			try {
				context = new InitialContext();
			} catch (NamingException e) {
				log.severe("NamingException occurs when creating InitialContext."+"\n"+e);
			}
		}
	}
	
	//private static ThreadLocal contextMap = new ThreadLocal();
	private GlobalApplictaionContext(){
	}
	
	public static void setContext(String appName, ApplicationContextInternal context){
		contextMap.put(appName, context);
	}
	
	public static ApplicationContextInternal getContext(String appName){
		return contextMap.get(appName);
	}
	
	public static ApplicationContextInternal removeContext(String appName){
		return contextMap.remove(appName);
	}
	
	/**
	 * Gain the commonj.work.WorkManager through the [applicationName] and [wmName] for Web Application.
	 * This temporary solution is not flexibel.
	 * //TODO need to improve. 
	 * @param appName
	 * @param wmName
	 * @return
	 */
	public static WorkManager getCommonJWorkManager(String appName, String wmName){
		ApplicationContextInternal  appContext = getContext(appName);
		if(appContext == null){
			return null;
		}
		
		return J2EEWorkManager.get(appName, null, wmName);
//		if(context == null){
//			initContext();
//		}
//		
//		WorkManager result = null;
//		try {
//			result = (WorkManager)context.lookup(appName + "@" + wmName);
//		} catch (NamingException e) {
//			log.error(String.format("NamingException occurs when lookup name[%s] using Context[%s]", appName + "@" + wmName,context)  , e);
//			return null;
//		}
//		return result;
	}
}
