package com.onceas.work.config;

import javax.xml.bind.JAXBException;


public interface WorkManagerConfigMBean {

	// String appName, String moduleName,
	public abstract void createWorkManager(String wmName);

	// String appName, String moduleName,
	public abstract void delWorkManager(String wmName);

	// get the root and do some preparation
	public abstract void open(String filePath);
	
	// 
	public abstract void save();

	public abstract void updateWorkManager(String wmName);

//	public abstract OnceasWeb getRoot();

	public abstract String getFilePath();

	public abstract String[] getWorkManagers();
	/********************************************************
	 *    fair share  request class
	 /********************************************************/
	public abstract String getFairShareRequestClass(String wmName);

	public abstract String getFairShareRequestClassName(String wmName);

	public abstract String getFairShareRequestClassFair(String wmName);

	public abstract void updateFairShareRequestClass(String wmName,
                                                     String fairShareRequestClassShare)throws JAXBException;

	public abstract void delFairShareRequestClass(String wmName);

	public abstract void createFairShareRequestClass(String wmName,
                                                     String fairShareRequestClass, String fairShareRequestClassShare)
			throws JAXBException;

	public abstract void setFairShareRequestClassFair(String wmName,
                                                      String share) throws JAXBException;
	/********************************************************
	 *    response time  request class
	 /********************************************************/
	public abstract String getResponseTimeRequestClass(String wmName);

	public abstract String getResponseTimeRequestClassName(String wmName);

	public abstract String getResponseTimeRequestClassGoalTime(String wmName);

	public abstract void updateResponseTimeRequestClass(String wmName,
                                                        String responseTimeRequestClassGoalTime)throws JAXBException;

	public abstract void delResponseTimeRequestClass(String wmName);

	public abstract void createResponseTimeRequestClass(String wmName,
                                                        String responseTimeRequestClass, String responseTimeRequestClassGoalTime)
			throws JAXBException;

	public abstract void setResponseTimeRequestClassGoalTime(String wmName,
                                                             String share) throws JAXBException;
	/********************************************************
	 *     context request class
	 /********************************************************/
	public abstract String getContextRequestClass(String wmName);

	public abstract String getContextRequestClassName(String wmName);

	public abstract String[] getContextCase(String wmName);

	public abstract String getRequestClassName(String wmName, String caseName);

	public abstract String getGroupName(String wmName, String caseName);

	public abstract String getUserName(String wmName, String caseName);

//	public abstract void delContextRequestClass(String wmName, String caseName);
	public abstract void delContextRequestClass(String wmName);
	//先用名字创建context request class，之后需要为他创建context case
	public abstract void createContextRequestClass(String wmName,
                                                   String contextName) throws JAXBException;

	//@SuppressWarnings("unchecked")
	public abstract void createContextCase(String wmName,
                                           String requestClassName, String userName, String groupName)
			throws JAXBException;

	public abstract void delContextCase(String wmName, String caseName);
	
	//constraints
	/********************************************************
	 *     capacity
	 /********************************************************/
	public abstract String getCapacityConstraint(String wmName);

	public abstract String getCapacityConstraintName(String wmName);

	public abstract String getCapacityConstraintCount(String wmName);

	public abstract void delCapacityConstraint(String wmName);

	public abstract void createCapacityConstraint(String wmName,
                                                  String capacityConstraint, String capacityCount)
			throws JAXBException;
	public abstract void updateCapacityConstraint(String wmName, String newCount)throws JAXBException;

	public abstract void setCapacityConstraintCount(String wmName,
                                                    String newCount) throws JAXBException;
	/********************************************************
	 *     maxthreads
	 /********************************************************/
	public abstract String getMaxThreadsConstraint(String wmName);

	public abstract String getMaxThreadsConstraintName(String wmName);

	public abstract String getMaxThreadsConstraintCount(String wmName);

	public abstract void updateMaxThreadsConstraint(String wmName,
                                                    String maxThreadsConstraintCount) throws JAXBException;

	public abstract void setMaxThreadsConstraintCount(String wmName,
                                                      String maxThreadsConstraintCount) throws JAXBException;

	public abstract void delMaxThreadsConstraint(String wmName);

	public abstract void createMaxThreadsConstraint(String wmName,
                                                    String maxThreadsConstraint, String maxThreadsConstraintCount)
			throws JAXBException;
	/********************************************************
	 *     minthreads
	 /********************************************************/
	public abstract String getMinThreadsConstraint(String wmName);

	public abstract String getMinThreadsConstraintName(String wmName);

	public abstract String getMinThreadsConstraintCount(String wmName);

	public abstract void updateMinThreadsConstraint(String wmName,
                                                    String minThreadsConstraintCount) throws JAXBException;

	public abstract void setMinThreadsConstraintCount(String wmName,
                                                      String minThreadsConstraintCount) throws JAXBException;

	public abstract void delMinThreadsConstraint(String wmName);

	public abstract void createMinThreadsConstraint(String wmName,
                                                    String minThreadsConstraint, String minThreadsConstraintCount)
			throws JAXBException;

}