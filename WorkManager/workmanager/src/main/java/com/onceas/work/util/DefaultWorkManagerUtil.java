package com.onceas.work.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.onceas.work.ServerWorkManagerFactory;
import com.onceas.work.WorkManagerConstant;

public class DefaultWorkManagerUtil {
	private static final Logger log = Logger.getLogger(DefaultWorkManagerUtil.class.toString());
	
	private static Map<String,com.onceas.work.WorkManager> defaultAppWorkManagerMap = new ConcurrentHashMap<String,com.onceas.work.WorkManager>();
	
	public static com.onceas.work.WorkManager createDefaultWorkManager(String appName, String moduleName) {
		String key = buildKey(appName,moduleName);
		
		com.onceas.work.WorkManager defalutWm = defaultAppWorkManagerMap.get(key);
		if(defalutWm == null){
			log.info("Create DEFAULT WORKMANAGER for the application[" + appName + "], because of no preconfiguration found.");
			defalutWm = ServerWorkManagerFactory.create(WorkManagerConstant.ONCEAS_KERNEL_DEFAULT, -1, -1);;
			defaultAppWorkManagerMap.put(appName, defalutWm);
		}
		return defalutWm;
	}
	
	private static String buildKey(String appName, String moduleName){
		return appName + "@" + moduleName;
	}
}
