package com.onceas.work.config;

import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.onceas.descriptor.wm.Capacity;
import com.onceas.descriptor.wm.ContextCase;
import com.onceas.descriptor.wm.ContextRequestClass;
import com.onceas.descriptor.wm.FairShareRequestClass;
import com.onceas.descriptor.wm.MaxThreadsConstraint;
import com.onceas.descriptor.wm.MinThreadsConstraint;
import com.onceas.descriptor.wm.ObjectFactory;
import com.onceas.descriptor.wm.ResponseTimeRequestClass;
import com.onceas.descriptor.wm.TagStringValueType;
import com.onceas.descriptor.wm.Wm;
import com.onceas.descriptor.wm.WmDescriptor;
import com.onceas.descriptor.wm.WorkManagerBean;
import com.onceas.descriptor.wm.impl.WmDescriptorImpl;
import com.onceas.util.application.ApplicationContextInternal;
import com.onceas.util.application.GlobalApplictaionContext;
import com.onceas.work.WorkManagerCollection;
import com.onceas.work.constraint.OverloadManager;
import com.onceas.work.management.notification.WMUpdateEvent;
import com.onceas.work.util.WMUpdateListenerName;

/**
 * wm配置时表征 constraints,request class 的操作都暴露在这个服务当中， request class的组织可能会是个问题
 * 
 */

public class WorkManagerConfig implements WorkManagerConfigMBean {

	private String appName = null;

	private String moduleName = null;

	// private String wmName=null;
	private ObjectFactory factory = null;

	private Wm root = null;

	private WmDescriptor descriptor = null;

	private List<WorkManagerBean> wmbs = null;

	private String path = null;

	private WorkManagerCollection workManagerCollection = null;

	// private final static int DEFAULT_MAX_COUNT = 5;
	// private final static int DEFAULT_MIN_COUNT = 5;
	// log
	private Logger log = Logger.getLogger(getClass().getName());

	public WorkManagerConfig() {
		this.factory = new ObjectFactory();
		this.descriptor = new WmDescriptorImpl();
	}

	public WorkManagerConfig(String appName, String moduleName, WmDescriptor descriptor) {
		if (appName == null) {
			throw new IllegalArgumentException("application name is null!");
		}
		this.appName = appName;
		this.moduleName = moduleName;
		// this.path = path;
		this.factory = new ObjectFactory();
		this.descriptor = descriptor;
		// to get the associated workManagerCollection in constructor
		this.workManagerCollection = getWorkManagerCollection();
	}

	/*
	 * 
	 * preCondition: must invoke open() method to get a legal content tree of
	 * xml file i.e. root has existed
	 */
	public void createWorkManager(String wmName) {
		// first element of workamanager, need to create <work-manager> and
		// attach it to root then get the wmbs.
		if (wmName == null)
			throw new IllegalArgumentException(
					"the workmanager name must not be null!");
		wmName = wmName.trim();
		if (root.getWorkManager() == null) {
			try {
				com.onceas.descriptor.wm.WorkManager wm = factory
						.createWorkManager();
				root.setWorkManager(wm);
				wmbs = root.getWorkManager().getWorkManagerBean();
				// never null , maybe size is zero
			} catch (JAXBException e) {
				System.out
						.println("Cannot create <work-manager> element for :appName "
								+ appName + " workmanager name: " + wmName);
				e.printStackTrace();
			}
		}
		if (!isDuplicate(wmName)) {
			WorkManagerBean wmb;
			try {
				wmb = factory.createWorkManagerBean();
				TagStringValueType tsvt = factory.createTagStringValueType();
				tsvt.setValue(wmName);
				wmb.setName(tsvt);
				wmbs.add(wmb);

				// populate the new workmanager
				try {
					workManagerCollection.populate(wmName, appName, moduleName,
							null, null, null, null, null);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (JAXBException e) {
				e.printStackTrace();
			}
		} else {
			// duplicate
			throw new IllegalArgumentException("Duplicated workmanager name :"
					+ wmName + " in application :" + appName);
		}
	}

	private boolean isDuplicate(String name) {
		if (wmbs != null && wmbs.size() != 0) {
			for (WorkManagerBean wm : wmbs) {
				if (wm.getName().getValue().trim().equals(name)) {
					// duplicate
					return true;
				}
			}
		}
		return false;
	}

	public void delWorkManager(String wmName) {
		if (wmName == null)
			throw new IllegalArgumentException(
					"the workmanager name must not be null!");
		wmName = wmName.trim();
		if (wmbs != null && wmbs.size() != 0) {
			for (WorkManagerBean wm : wmbs) {
				if (wm.getName().getValue().trim().equals(wmName)) {
					wmbs.remove(wm);
					// 对于WORKMANAGER_TYPE，componentName参数没有任何意义，此处设为null
					workManagerCollection.unPopulate(moduleName, null, wmName,
							WorkManagerCollection.WORKMANAGER_TYPE);// unpopulate
					// workmanager
					return;
				}
			}
			// does not exist the wmName
		}

	}

	// get the root and do some preparation
	public void open(String filePath) {
		this.path = filePath;
		root = descriptor.importXml(filePath);
		if (root != null) {
			if (root.getWorkManager() != null) {
				// size of wmbs is 0 when no <work-manager-bean> element
				// contained by <work-manager>
				wmbs = root.getWorkManager().getWorkManagerBean();
			}
		} else {
			throw new IllegalArgumentException(
					"Cannot gain root element by parsing the file: " + filePath);
		}
	}
	
	public void open(Wm wmRoot ,String path){
		if( wmRoot == null || path == null){
			throw new IllegalArgumentException(
					"the argument must not be null in open(Wm, String) method " );
		}
		this.path = path;
		this.root = wmRoot;
		if (root.getWorkManager() != null) {
			// size of wmbs is 0 when no <work-manager-bean> element
			// contained by <work-manager>
			wmbs = root.getWorkManager().getWorkManagerBean();
		}

	}

	// 
	public void save() {
		descriptor.exportXml(path, root);
	}

	public void updateWorkManager(String wmName) {
		// just save the whole content tree, do nothing
		save();
	}

	public Wm getRoot() {
		return root;
	}

	public String getFilePath() {
		return path;
	}

	public String[] getWorkManagers() {
		if (wmbs == null)
			return null;
		String[] wms = new String[wmbs.size()];
		for (int i = 0; i < wmbs.size(); i++) {
			wms[i] = ((WorkManagerBean) (wmbs.get(i))).getName().getValue()
					.trim();
			// System.out.println(i+"th workmanager bean: "+wms[i]);
		}
		return wms;
	}

	/***************************************************************************
	 * request class
	 **************************************************************************/
	public String getFairShareRequestClass(String wmName) {
		return getFairShareRequestClassName(wmName);
	}

	public String getFairShareRequestClassName(String wmName) {

		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getFairShareRequestClass() != null)
				return wmb.getFairShareRequestClass().getName().getValue()
						.trim();
		}
		return null;
	}

	public String getFairShareRequestClassFair(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getFairShareRequestClass() != null)
				return wmb.getFairShareRequestClass().getFairShare().getValue()
						.trim();
		}
		return null;
	}

	public void updateFairShareRequestClass(String wmName, 
			String fairShareRequestClassShare) throws JAXBException {
		setFairShareRequestClassFair(wmName, fairShareRequestClassShare);
	}

	public void delFairShareRequestClass(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getFairShareRequestClass() != null) {
				String name = wmb.getFairShareRequestClass().getName()
						.getValue().trim();
				wmb.setFairShareRequestClass(null);
				workManagerCollection.unPopulate(moduleName, name, wmName,
						WorkManagerCollection.REQUEST_CLASS_TYPE);
			}
		}
	}

	public void createFairShareRequestClass(String wmName,
			String fairShareRequestClass, String fairShareRequestClassShare)
			throws JAXBException {
		if(!isValidNumberFormat(fairShareRequestClassShare))
			return;

		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getFairShareRequestClass() == null && wmb.getResponseTimeRequestClass() == null) {
				// create only neither fairshare nor responsetiem exists
				FairShareRequestClass fsrc = factory
						.createFairShareRequestClass();
				TagStringValueType fname = factory.createTagStringValueType();
				fname.setValue(fairShareRequestClass);
				TagStringValueType fshare = factory.createTagStringValueType();
				fshare.setValue(fairShareRequestClassShare);
				fsrc.setFairShare(fshare);
				fsrc.setName(fname);
				wmb.setFairShareRequestClass(fsrc);

				// populate new component and add it to its belonging wm
				com.onceas.work.constraint.FairShareRequestClass fsRequestClass = new com.onceas.work.constraint.FairShareRequestClass(
						fairShareRequestClass, Integer
								.parseInt(fairShareRequestClassShare));
				try {
					workManagerCollection.populate(fsRequestClass, moduleName);
					// register child runtime
					workManagerCollection.registChildWorkManagerRuntime(
							moduleName, fairShareRequestClass, wmName);

					// add component listener
					workManagerCollection.addListener(fsRequestClass, wmName,
							moduleName);
					workManagerCollection.addWorkManagerComponent(moduleName,
							wmName, fsRequestClass);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setFairShareRequestClassFair(String wmName, String share)
			throws JAXBException {
		
		if(!isValidNumberFormat(share))
			return;
		
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getFairShareRequestClass() != null) {
				TagStringValueType fs = factory.createTagStringValueType();
				fs.setValue(share);
				wmb.getFairShareRequestClass().setFairShare(fs);

				// send update notification
				String fairshareName = wmb.getFairShareRequestClass().getName()
						.getValue().trim();
				com.onceas.work.constraint.FairShareRequestClass fsRequestClass = new com.onceas.work.constraint.FairShareRequestClass(
						fairshareName, Integer.parseInt(share));
				WMUpdateListenerName listenerName = new WMUpdateListenerName(
						wmName, moduleName, fairshareName);
				WMUpdateEvent event = new WMUpdateEvent(this, fsRequestClass);
				workManagerCollection.sendNotification(listenerName, event);
			}

		}
	}

	// response time request class

	 public String getResponseTimeRequestClass(String wmName){
		 return getResponseTimeRequestClassName(wmName);
	 }
	//
	  public String getResponseTimeRequestClassName(String wmName){
			WorkManagerBean wmb = getWorkManagerBean(wmName);
			if (wmb != null) {
				if (wmb.getResponseTimeRequestClass() != null)
					return wmb.getResponseTimeRequestClass().getName().getValue()
							.trim();
			}
			return null;
	 }
	
	
	 public String getResponseTimeRequestClassGoalTime(String wmName){
			WorkManagerBean wmb = getWorkManagerBean(wmName);
			if (wmb != null) {
				if (wmb.getResponseTimeRequestClass() != null)
					return wmb.getResponseTimeRequestClass().getGoalMs().getValue()
							.trim();
			}
			return null;
	 }
	//
	 public void updateResponseTimeRequestClass(String wmName,String responseTimeRequestClassGoalTime) throws JAXBException{
			 setResponseTimeRequestClassGoalTime(wmName, responseTimeRequestClassGoalTime);
	 }
	//
	 public void delResponseTimeRequestClass(String wmName){
			WorkManagerBean wmb = getWorkManagerBean(wmName);
			if (wmb != null) {
				if (wmb.getResponseTimeRequestClass() != null) {
					String name = wmb.getResponseTimeRequestClass().getName()
							.getValue().trim();
					wmb.setResponseTimeRequestClass(null);
					workManagerCollection.unPopulate(moduleName, name, wmName,
							WorkManagerCollection.REQUEST_CLASS_TYPE);
				}
			}
			 
	 }
	//
	 public void createResponseTimeRequestClass(String wmName,String responseTimeRequestClass,String
	 responseTimeRequestClassGoalTime)throws JAXBException{
			if(!isValidNumberFormat(responseTimeRequestClassGoalTime))
				return;

			WorkManagerBean wmb = getWorkManagerBean(wmName);
			if (wmb != null) {
				if (wmb.getResponseTimeRequestClass() == null && wmb.getFairShareRequestClass() == null) {
					// create only neither fairshare nor responsetiem exists
					ResponseTimeRequestClass rtrc = factory
							.createResponseTimeRequestClass();
					TagStringValueType rname = factory.createTagStringValueType();
					rname.setValue(responseTimeRequestClass);
					TagStringValueType rgoal = factory.createTagStringValueType();
					rgoal.setValue(responseTimeRequestClassGoalTime);
					rtrc.setName(rname);
					rtrc.setGoalMs(rgoal);
					wmb.setResponseTimeRequestClass(rtrc);

					// populate new component and add it to its belonging wm
					com.onceas.work.constraint.ResponseTimeRequestClass rtRequestClass = new com.onceas.work.constraint.ResponseTimeRequestClass(
							responseTimeRequestClass, Integer
									.parseInt(responseTimeRequestClassGoalTime));
					try {
						workManagerCollection.populate(rtRequestClass, moduleName);
						// register child runtime
						workManagerCollection.registChildWorkManagerRuntime(
								moduleName, responseTimeRequestClass, wmName);

						// add component listener
						workManagerCollection.addListener(rtRequestClass, wmName,
								moduleName);
						workManagerCollection.addWorkManagerComponent(moduleName,
								wmName, rtRequestClass);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					// some info?
				}
			}
			 
	 }
	 public void setResponseTimeRequestClassGoalTime(String wmName, String goal)
		throws JAXBException {
		 WorkManagerBean wmb = getWorkManagerBean(wmName);
		 if(wmb != null){
			 if(wmb.getResponseTimeRequestClass() != null){
					TagStringValueType gt = factory.createTagStringValueType();
					gt.setValue(goal);
					wmb.getResponseTimeRequestClass().setGoalMs(gt);

					// send update notification
					String responseTimeName = wmb.getResponseTimeRequestClass().getName()
							.getValue().trim();
					com.onceas.work.constraint.ResponseTimeRequestClass rtRequestClass = new com.onceas.work.constraint.ResponseTimeRequestClass(
							responseTimeName, Integer.parseInt(goal));
					WMUpdateListenerName listenerName = new WMUpdateListenerName(
							wmName, moduleName, responseTimeName);
					WMUpdateEvent event = new WMUpdateEvent(this, rtRequestClass);
					workManagerCollection.sendNotification(listenerName, event);
				 
			 }
		 }
		 
	 }

	 //context case
	public String getContextRequestClass(String wmName) {
		return getContextRequestClassName(wmName);
	}

	public String getContextRequestClassName(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getContextRequestClass() != null) {
				return wmb.getContextRequestClass().getName().getValue().trim();
			}
		}
		return null;
	}

	public String[] getContextCase(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getContextRequestClass() != null) {
				List cases = wmb.getContextRequestClass().getContextCase();
				String[] result = new String[cases.size()];
				for (int i = 0; i < cases.size(); i++) {
					result[i] = ((ContextCase) (cases.get(i)))
							.getRequestClassName().getValue().trim();
				}
				return result;
			}
		}
		return null;
	}

	// case name is identical with requset class name
	public String getRequestClassName(String wmName, String caseName) {
		return caseName;
	}

	public String getGroupName(String wmName, String caseName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (getContextCase(wmb, caseName) != null)
				if (getContextCase(wmb, caseName).getGroupName() != null)
					return getContextCase(wmb, caseName).getGroupName()
							.getValue().trim();
		}
		return null;
	}

	public String getUserName(String wmName, String caseName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (getContextCase(wmb, caseName) != null)
				if (getContextCase(wmb, caseName).getUserName() != null)
					return getContextCase(wmb, caseName).getUserName()
							.getValue().trim();
		}
		return null;
	}

	// require:wmb != null
	private ContextCase getContextCase(WorkManagerBean wmb, String caseName) {
		if (wmb.getContextRequestClass() != null) {
			List cases = wmb.getContextRequestClass().getContextCase();
			for (int i = 0; i < cases.size(); i++) {
				if (((ContextCase) (cases.get(i))).getRequestClassName()
						.getValue().trim().equals(caseName)) {
					return (ContextCase) (cases.get(i));
				}
			}
		}
		return null;
	}

	public void delContextRequestClass(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getContextRequestClass() != null)
				wmb.setContextRequestClass(null);
		}
	}

	// 先用名字创建context request class，之后需要为他创建context case
	public void createContextRequestClass(String wmName, String contextName)
			throws JAXBException {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getContextRequestClass() == null) {
				ContextRequestClass crc = factory.createContextRequestClass();
				TagStringValueType name = factory.createTagStringValueType();
				name.setValue(contextName);
				crc.setName(name);
				wmb.setContextRequestClass(crc);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void createContextCase(String wmName, String requestClassName,
			String userName, String groupName) throws JAXBException {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getContextRequestClass() != null) {
				ContextCase contextCase = constructContextCase(
						requestClassName, userName, groupName);
				if (contextCase != null) {
					List<ContextCase> list = wmb.getContextRequestClass()
							.getContextCase();
					list.add(contextCase);
				}
			}
		}
	}

	public void delContextCase(String wmName, String caseName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getContextRequestClass() != null) {
				List list = wmb.getContextRequestClass().getContextCase();
				for (int i = 0; i < list.size(); i++) {
					if (((ContextCase) (list.get(i))).getRequestClassName()
							.getValue().trim().equals(caseName))
						list.remove(i);
				}
			}
		}
	}

	private ContextCase constructContextCase(String requestClassName,
			String userName, String groupName) throws JAXBException {

		if (requestClassName == null)
			return null;
		ContextCase contextCase = factory.createContextCase();
		TagStringValueType name = factory.createTagStringValueType();
		name.setValue(requestClassName);
		contextCase.setRequestClassName(name);
		if (userName != null) {
			TagStringValueType uname = factory.createTagStringValueType();
			uname.setValue(userName);
			contextCase.setUserName(uname);
		}
		if (groupName != null) {
			TagStringValueType gname = factory.createTagStringValueType();
			gname.setValue(groupName);
			contextCase.setGroupName(gname);
		}

		return contextCase;

	}

	/***************************************************************************
	 * constraints
	 **************************************************************************/

	public String getCapacityConstraint(String wmName) {
		return getCapacityConstraintName(wmName);
	}

	public String getCapacityConstraintName(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getCapacity() != null)
				return wmb.getCapacity().getName().getValue().trim();
		}
		return null;
	}

	public String getCapacityConstraintCount(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getCapacity() != null)
				return wmb.getCapacity().getCount().getValue().trim();
		}
		return null;
	}

	public void delCapacityConstraint(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getCapacity() != null) {
				String name = wmb.getCapacity().getName().getValue().trim();
				wmb.setCapacity(null);
				workManagerCollection.unPopulate(moduleName, name, wmName,
						WorkManagerCollection.OVERLOAD_MANAGER_TYPE);
			}
		}
	}

	public void createCapacityConstraint(String wmName,
			String capacityConstraint, String capacityCount)
			throws JAXBException {
		if(!isValidNumberFormat(capacityCount))
			return;

		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getCapacity() == null) {
				Capacity c = factory.createCapacity();
				TagStringValueType name = factory.createTagStringValueType();
				name.setValue(capacityConstraint);
				TagStringValueType count = factory.createTagStringValueType();
				count.setValue(capacityCount);
				c.setName(name);
				c.setCount(count);
				wmb.setCapacity(c);

				// populate the created "Capacity" and add it to the WorkManager
				// specified by name "wmName"
				OverloadManager overloadManager = createOverloadManager(
						capacityConstraint, capacityCount);
				workManagerCollection.populate(overloadManager, moduleName);

				// unlike max,min,request class,overloadmanager does not need
				// register child runtime ??
				// but need to add component listener
				workManagerCollection.addListener(overloadManager, wmName,
						moduleName);

				workManagerCollection.addWorkManagerComponent(moduleName,
						wmName, overloadManager);
			}
		}
	}

	private OverloadManager createOverloadManager(String capacityConstraint,
			String capacityCount) {
		OverloadManager overloadManager = new OverloadManager(
				capacityConstraint, Integer.parseInt(capacityCount));
		return overloadManager;
	}
	public void updateCapacityConstraint(String wmName,
			String capacityCount) throws JAXBException {
		setCapacityConstraintCount(wmName, capacityCount);
	}

	public void setCapacityConstraintCount(String wmName, String newCount)
			throws JAXBException {
		
		if(!isValidNumberFormat(newCount))
			return;
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getCapacity() != null) {
				TagStringValueType count = factory.createTagStringValueType();
				count.setValue(newCount);
				wmb.getCapacity().setCount(count);

				// send updating notification
				String capacityName = wmb.getCapacity().getName().getValue()
						.trim();
				OverloadManager overloadManager = createOverloadManager(
						capacityName, newCount);
				WMUpdateEvent event = new WMUpdateEvent(this, overloadManager);
				WMUpdateListenerName listenerName = new WMUpdateListenerName(
						wmName, moduleName, capacityName);
				workManagerCollection.sendNotification(listenerName, event);
			}
		}
	}

	public String getMaxThreadsConstraint(String wmName) {
		return getMaxThreadsConstraintName(wmName);
	}

	public String getMaxThreadsConstraintName(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMaxThreadsConstraint() != null) {
				return wmb.getMaxThreadsConstraint().getName().getValue()
						.trim();
			}
		}
		return null;
	}

	public String getMaxThreadsConstraintCount(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMaxThreadsConstraint() != null) {
				return wmb.getMaxThreadsConstraint().getCount().getValue()
						.trim();
			}
		}
		return null;
	}

	public void updateMaxThreadsConstraint(String wmName,
			String maxThreadsConstraintCount) throws JAXBException {
		setMaxThreadsConstraintCount(wmName, maxThreadsConstraintCount);
	}

	public void setMaxThreadsConstraintCount(String wmName,
			String maxThreadsConstraintCount) throws JAXBException {
		
		if(!isValidNumberFormat(maxThreadsConstraintCount))
			return;
		
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMaxThreadsConstraint() != null) {
				// ensure max.count >= min.count 
				if(!isValidMaxCount(wmb, maxThreadsConstraintCount))
					return;
				TagStringValueType count = factory.createTagStringValueType();
				count.setValue(maxThreadsConstraintCount);
				wmb.getMaxThreadsConstraint().setCount(count);

				// send updating notification
				String maxName = wmb.getMaxThreadsConstraint().getName()
						.getValue().trim();
				com.onceas.work.constraint.MaxThreadsConstraint maxConstraint = new com.onceas.work.constraint.MaxThreadsConstraint(
						maxName, Integer.parseInt(maxThreadsConstraintCount));
				WMUpdateEvent event = new WMUpdateEvent(this, maxConstraint);
				WMUpdateListenerName listenerName = new WMUpdateListenerName(
						wmName, moduleName, maxName);
				workManagerCollection.sendNotification(listenerName, event);

			}
		}
	}

	/***************************************************************************
	 * 如果wmName指定的workmanager包含的max组件，则删除该max组件
	 * 因为每个wm包含的组件个数最多为1，故不用指定componentName
	 */
	public void delMaxThreadsConstraint(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMaxThreadsConstraint() != null) {
				String name = wmb.getMaxThreadsConstraint().getName()
						.getValue().trim();
				wmb.setMaxThreadsConstraint(null);
				workManagerCollection.unPopulate(moduleName, name, wmName,
						WorkManagerCollection.MAX_THTREADS_CONSTRAINT_TYPE);
			}
		}
	}

	public void createMaxThreadsConstraint(String wmName,
			String maxThreadsConstraint, String maxThreadsConstraintCount)
			throws JAXBException {
		
		if(!isValidNumberFormat(maxThreadsConstraintCount))
			return;
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMaxThreadsConstraint() == null) {
				// ensure max.count >= min.count 
				if(!isValidMaxCount(wmb, maxThreadsConstraintCount))
					return;
				MaxThreadsConstraint mtc = factory.createMaxThreadsConstraint();
				TagStringValueType name = factory.createTagStringValueType();
				name.setValue(maxThreadsConstraint);
				mtc.setName(name);
				TagStringValueType count = factory.createTagStringValueType();
				count.setValue(maxThreadsConstraintCount);
				mtc.setCount(count);
				wmb.setMaxThreadsConstraint(mtc);

				// populate
				com.onceas.work.constraint.MaxThreadsConstraint maxConstraint = new com.onceas.work.constraint.MaxThreadsConstraint(
						maxThreadsConstraint, Integer
								.parseInt(maxThreadsConstraintCount));
				try {
					// comparing to wm's populate method ,population method of
					// component just does partial work
					workManagerCollection.populate(maxConstraint, moduleName);

					// need register child runtime
					workManagerCollection.registChildWorkManagerRuntime(
							moduleName, maxThreadsConstraint, wmName);

					// need to add component listener
					workManagerCollection.addListener(maxConstraint, wmName,
							moduleName);

					workManagerCollection.addWorkManagerComponent(moduleName,
							wmName, maxConstraint);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}else{
			throw new IllegalArgumentException("Don't exist workmanager named "+wmName);
		}
	}

	public String getMinThreadsConstraint(String wmName) {
		return getMinThreadsConstraintName(wmName);
	}

	public String getMinThreadsConstraintName(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMinThreadsConstraint() != null) {
				return wmb.getMinThreadsConstraint().getName().getValue()
						.trim();
			}
		}
		return null;
	}

	public String getMinThreadsConstraintCount(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMinThreadsConstraint() != null)
				return wmb.getMinThreadsConstraint().getCount().getValue()
						.trim();
		}
		return null;
	}

	public void updateMinThreadsConstraint(String wmName,
			String minThreadsConstraintCount) throws JAXBException {
		setMinThreadsConstraintCount(wmName, minThreadsConstraintCount);
	}

	public void setMinThreadsConstraintCount(String wmName,
			String minThreadsConstraintCount) throws JAXBException {
		
		if(!isValidNumberFormat(minThreadsConstraintCount))
			return;
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMinThreadsConstraint() != null) {
				// ensure min.count <= max.count  
				if(!isValidMinCount(wmb, minThreadsConstraintCount))
					return;
				
				TagStringValueType count = factory.createTagStringValueType();
				count.setValue(minThreadsConstraintCount);
				wmb.getMinThreadsConstraint().setCount(count);

				// send updating notification
				String minName = wmb.getMinThreadsConstraint().getName()
						.getValue().trim();
				com.onceas.work.constraint.MinThreadsConstraint minConstraint = new com.onceas.work.constraint.MinThreadsConstraint(
						minName, Integer.parseInt(minThreadsConstraintCount));
				WMUpdateEvent event = new WMUpdateEvent(this, minConstraint);
				WMUpdateListenerName listenerName = new WMUpdateListenerName(
						wmName, moduleName, minName);
				workManagerCollection.sendNotification(listenerName, event);

				// save to the file
				// save();
			}
		}
	}

	public void delMinThreadsConstraint(String wmName) {
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMinThreadsConstraint() != null) {
				String name = wmb.getMaxThreadsConstraint().getName()
						.getValue().trim();
				wmb.setMinThreadsConstraint(null);
				
				workManagerCollection.unPopulate(moduleName, name, wmName,
						WorkManagerCollection.MIN_THTREADS_CONSTRAINT_TYPE);

			}
		}
	}

	public void createMinThreadsConstraint(String wmName,
			String minThreadsConstraint, String minThreadsConstraintCount)
			throws JAXBException {
		if(!isValidNumberFormat(minThreadsConstraintCount))
			return;
		WorkManagerBean wmb = getWorkManagerBean(wmName);
		if (wmb != null) {
			if (wmb.getMinThreadsConstraint() == null) {
				// ensure min.count <= max.count  
				if(!isValidMinCount(wmb, minThreadsConstraintCount))
					return;
				
				MinThreadsConstraint mtc = factory.createMinThreadsConstraint();
				TagStringValueType name = factory.createTagStringValueType();
				name.setValue(minThreadsConstraint);
				mtc.setName(name);
				TagStringValueType count = factory.createTagStringValueType();
				count.setValue(minThreadsConstraintCount);
				mtc.setCount(count);
				wmb.setMinThreadsConstraint(mtc);

				// populate the created "min" ,regist runtime ,add listener,and
				// add it to the WorkManager
				com.onceas.work.constraint.MinThreadsConstraint minConstraint = new com.onceas.work.constraint.MinThreadsConstraint(
						minThreadsConstraint, Integer
								.parseInt(minThreadsConstraintCount));
				try {
					workManagerCollection.populate(minConstraint, moduleName);

					// need register child runtime
					workManagerCollection.registChildWorkManagerRuntime(
							moduleName, minThreadsConstraint, wmName);

					// need to add component listener
					workManagerCollection.addListener(minConstraint, wmName,
							moduleName);

					workManagerCollection.addWorkManagerComponent(moduleName,
							wmName, minConstraint);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			throw new IllegalArgumentException("Don't exist workmanager named "+wmName);
		}
	}

	/***************************************************************************
	 * utils
	 **************************************************************************/
	private WorkManagerBean getWorkManagerBean(String wmName) {
		if (wmbs != null) {
			// System.out.println("wmbs does not null,its length is:
			// "+wmbs.size());
			for (int i = 0; i < wmbs.size(); i++) {
				WorkManagerBean wmb = (WorkManagerBean) (wmbs.get(i));
				if (wmb.getName().getValue().trim().equals(wmName.trim())) {
					return wmb;
				}
			}
		}
		// not find or null
		return null;
	}

	// send notificaiton when intersted events happen
	private void sendNotification(WMUpdateListenerName listenerName,
			WMUpdateEvent e) {
		workManagerCollection.sendNotification(listenerName, e);
	}

	/**
	 * 返回当前app对应的workmanagerCollection
	 * 
	 * @return
	 */
	private WorkManagerCollection getWorkManagerCollection() {
		return getWorkManagerCollection(appName);
	}

	private WorkManagerCollection getWorkManagerCollection(String appName) {
		ApplicationContextInternal appContext = GlobalApplictaionContext
				.getContext(appName);
		if (appContext == null)
			throw new IllegalStateException(
					"No application context associated with the application: "
							+ appName);
		WorkManagerCollection tmpWMCollection = appContext
				.getWorkManagerCollection();
		if (tmpWMCollection == null)
			throw new IllegalStateException(
					"No WorkManagerCollection associated with the application: "
							+ appName);
		return tmpWMCollection;
	}

	/**
	 * 检查传入的串参数是否代表合法的整数值
	 * 
	 * @param s
	 * 
	 */
	private boolean isValidNumberFormat(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			 System.err.println(s + " cannot represent a valid Integer! Please input a illegal Integer Number.");
			return false;
		}
		return true;
	}

	private void stringTrim(String s) {
		if (s != null)
			s = s.trim();
	}
	/***
	 * validate whether max.count>= min.count
	 * precondition: wmb != null
	 */
	private boolean isValidMinCount(WorkManagerBean wmb, String minCount){
			if(wmb.getMaxThreadsConstraint() == null)
				return true;
			
			int max = Integer.parseInt(wmb.getMaxThreadsConstraint().getCount().getValue().trim());
			int min = Integer.parseInt(minCount);
			
			if( max >= min )
				return true;
			else{
				System.err.println("minThreadsConstraintCount " + minCount + " is greater than maxThreadsConstraintCount " + max);
				return false;
			}
	}
	private boolean isValidMaxCount(WorkManagerBean wmb, String maxCount){
			if(wmb.getMinThreadsConstraint() == null)
				return true;
			
			int min = Integer.parseInt(wmb.getMinThreadsConstraint().getCount().getValue().trim());
			int max = Integer.parseInt(maxCount);
			
			if( max >= min )
				return true;
			else{
				System.err.println("maxThreadsConstraintCount " + maxCount + " is less than minThreadsConstraintCount " + min);
				return false;
			}
	}

}
