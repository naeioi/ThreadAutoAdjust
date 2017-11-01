package com.onceas.descriptor.wm.dd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.onceas.descriptor.wm.ContextCase;
import com.onceas.descriptor.wm.WorkManagerBean;

public class WorkManagerBeanDD {
	private String workmanagerBeanName;

	private MaxThreadsConstraintDD maxThreadsConstraintDD = null;
	private MinThreadsConstraintDD minThreadsConstraintDD = null;
	private FairShareRequestDD fairShareRequestDD=null;
	private ResponseTimeRequestDD responseTimeRequestDD=null;
	private CapacityConstraintDD capacityConstraintDD = null;	
	private WorkManagerShutDownTriggerDD workManagerShutDownTriggerDD=null;
	private ContextRequestDD contextRequestDD=null;
	
	
	private WorkManagerBean wmBean;

	/**
	 * REQUIRE: 
	 *  parameter<code>wmBean</code> must be not null
	 * @param wmConfig
	 */
	public WorkManagerBeanDD(WorkManagerBean wmBean) {
		this.wmBean = wmBean;
	}

	public String getName() {
		if(workmanagerBeanName == null){
			workmanagerBeanName = wmBean.getName().getValue().trim();
		}
		return workmanagerBeanName;
	}

	public MaxThreadsConstraintDD getMaxThreadsConstrain() {
		if(maxThreadsConstraintDD == null){
			maxThreadsConstraintDD = createMaxThreadsConstraintDD();
		}
		return maxThreadsConstraintDD;
	}
	
	private MaxThreadsConstraintDD createMaxThreadsConstraintDD() {
		MaxThreadsConstraintDD maxDd = null;
		if(wmBean.getMaxThreadsConstraint() != null){
			String name = this.wmBean.getMaxThreadsConstraint().getName().getValue().trim();
			int count = Integer.parseInt(this.wmBean.getMaxThreadsConstraint().getCount().getValue().trim());
			maxDd = new MaxThreadsConstraintDD(name, count);
		}	
		return maxDd;
	}

	public MinThreadsConstraintDD getMinThreadsConstrain() {
		if(minThreadsConstraintDD == null){
			minThreadsConstraintDD = createMinThreadsConstraintDD();
		}
		return minThreadsConstraintDD;
	}
	
	private MinThreadsConstraintDD createMinThreadsConstraintDD() {
		MinThreadsConstraintDD minDd = null;
		if(wmBean.getMinThreadsConstraint() != null){
			String name = this.wmBean.getMinThreadsConstraint().getName().getValue().trim();
			int count = Integer.parseInt(this.wmBean.getMinThreadsConstraint().getCount().getValue().trim());
			minDd = new MinThreadsConstraintDD(name, count);
		}	
		return minDd;
	}

	public CapacityConstraintDD getCapacityConstraint() {
		if(capacityConstraintDD == null){
			capacityConstraintDD = createCapacityConstraintDD();
		}
		return capacityConstraintDD;
	}
	
	private CapacityConstraintDD createCapacityConstraintDD() {
		CapacityConstraintDD capcityDd = null;
		if(wmBean.getCapacity() != null){
			String name = this.wmBean.getCapacity().getName().getValue().trim();
			int count = Integer.parseInt(this.wmBean.getCapacity().getCount().getValue().trim());
			capcityDd = new CapacityConstraintDD(name, count);
		}	
		return capcityDd;
	}

	public FairShareRequestDD getFairShareRequest()
	{
		if(fairShareRequestDD == null){
			fairShareRequestDD = createFairShareRequestDD();
		}
		return fairShareRequestDD;
	}
	
	private FairShareRequestDD createFairShareRequestDD() {
		FairShareRequestDD fairshareDd = null;
		if(wmBean.getFairShareRequestClass() != null){
			String name = this.wmBean.getFairShareRequestClass().getName().getValue().trim();
			int count = Integer.parseInt(this.wmBean.getFairShareRequestClass().getFairShare().getValue().trim());
			fairshareDd = new FairShareRequestDD(name, count);
		}	
		return fairshareDd;
	}

	public ResponseTimeRequestDD getResponseTimeRequest()
	{
		if(responseTimeRequestDD == null){
			responseTimeRequestDD = createResponseTimeRequestDD();
		}
		return responseTimeRequestDD;
	}

	private ResponseTimeRequestDD createResponseTimeRequestDD() {
		ResponseTimeRequestDD responseTimeDd = null;
		if(wmBean.getResponseTimeRequestClass() != null){
			String name = this.wmBean.getResponseTimeRequestClass().getName().getValue().trim();
			int count = Integer.parseInt(this.wmBean.getResponseTimeRequestClass().getGoalMs().getValue().trim());
			responseTimeDd = new ResponseTimeRequestDD(name, count);
		}	
		return responseTimeDd;
	}

	public WorkManagerShutDownTriggerDD getWorkManagerShutDownTriggerDD()
	{
		if(workManagerShutDownTriggerDD == null){
			workManagerShutDownTriggerDD = createWorkManagerShutDownTriggerDD();
		}
		return workManagerShutDownTriggerDD;
	}
	
	private WorkManagerShutDownTriggerDD createWorkManagerShutDownTriggerDD() {
		WorkManagerShutDownTriggerDD wmShutdownTriggerDd = null;
		if(wmBean.getWorkManagerShutdownTrigger() != null){
			int maxStuckTime = Integer.parseInt(this.wmBean.getWorkManagerShutdownTrigger().getMaxStuckThreadTime().getValue().trim());
			int stuckThreadCount = Integer.parseInt(this.wmBean.getWorkManagerShutdownTrigger().getStuckThreadCount().getValue().trim());
			wmShutdownTriggerDd = new WorkManagerShutDownTriggerDD(maxStuckTime, stuckThreadCount);
		}	
		return wmShutdownTriggerDd;
	}

	public ContextRequestDD getContextRequestClass() {
		if(contextRequestDD == null){
			contextRequestDD = createContextRequestDD();
		}
		return contextRequestDD;
	}

	private ContextRequestDD createContextRequestDD() {
		ContextRequestDD wmShutdownTriggerDd = null;
		if(wmBean.getContextRequestClass() != null){
			String name = this.wmBean.getContextRequestClass().getName().getValue().trim();
			List<ContextCaseDD> contextCaseList = getAllContextCases(this.wmBean.getContextRequestClass().getContextCase(), name);
			wmShutdownTriggerDd = new ContextRequestDD(name, contextCaseList);
		}	
		return wmShutdownTriggerDd;
	}

	private List<ContextCaseDD> getAllContextCases(List contextCaseList, String contextClassName) {
		List<ContextCaseDD> contextCaseDdList = new ArrayList<ContextCaseDD>();
		if(contextCaseList != null){
			ContextCaseDD ccDd = null;
			for(Iterator it = contextCaseList.iterator(); it.hasNext(); ){
				ContextCase cc = (ContextCase)it.next();
				String userName = cc.getUserName().getValue().trim();
				String groupName = cc.getGroupName().getValue().trim();
				String requestClassName = cc.getRequestClassName().getValue().trim();
				ccDd =  new ContextCaseDD(contextClassName,requestClassName,userName,groupName);
				contextCaseDdList.add(ccDd);
			}
		}		
		return contextCaseDdList;
	}
}
