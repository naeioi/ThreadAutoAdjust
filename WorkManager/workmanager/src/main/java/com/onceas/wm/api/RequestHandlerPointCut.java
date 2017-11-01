package com.onceas.wm.api;

import com.onceas.aspect.connector.RequestContext;
import com.onceas.aspect.connector.RequestHandleWork;
import com.onceas.work.WorkManager;
import com.onceas.work.WorkManagerConstant;
import com.onceas.work.j2ee.J2EEWorkManager;

/**
 * wm����ʱ����Ĺ�����
 * �����÷���
 * 		1������appName��moduleName����RequestContext
 * 		eg:
 * 	  	2����Runnable�����װ��ReqeustHandleWork
 * 		3) ����RequestHandlerPointCut.execute(requestContext,requestHandleWork);
 * @author yk
 *
 */
public class RequestHandlerPointCut {

	 public static void execute(RequestContext requestContext, RequestHandleWork requestHandleWork){
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
