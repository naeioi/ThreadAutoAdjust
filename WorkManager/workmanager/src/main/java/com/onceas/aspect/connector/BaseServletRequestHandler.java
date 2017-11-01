package com.onceas.aspect.connector;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;

import com.onceas.work.WorkManager;
import com.onceas.work.WorkManagerConstant;
import com.onceas.work.j2ee.J2EEWorkManager;

public class BaseServletRequestHandler {
	public void execute(RequestContext requestContext, RequestHandleWork requestHandleWork){
	    String appName = requestContext.getApplicationName();
	    String moduleName = requestContext.getModuleName();
	    WorkManager wm = J2EEWorkManager.getModuleOnceASWorkManager(appName, moduleName);
	    String wmName = wm.getName();
		// if it is a direct policy, the work will be run in the current thread, so no
		// need to wait
        if(wmName == null || !wmName.equals(WorkManagerConstant.ONCEAS_DIRECT)){
        	requestContext.lock.lock();
    		wm.schedule(requestHandleWork);
    		try {
    			requestContext.workComplete.await();
     		} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
     			requestContext.lock.unlock();
    		}
        }else{
        	wm.schedule(requestHandleWork);
        }

 }
}
