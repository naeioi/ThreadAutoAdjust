package com.onceas.service.lifecycle;

import java.util.logging.Logger;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */


/**
 *���з���ĸ���.����Ϊ�˷���MServiceServer�����������ڹ���.һ��������������̿��Է�Ϊ
 * 4������:init(),start(),stop(),destroy().�⼸������ִ�����,����ֱ����INITIALIZED
 * ��״̬.ֻ����STARTED״̬���ܱ�����.
 * ʹ�ø����û����ش�����4������.
 * @author ������
 * @version 1.0
 */
//com.onceas.kernel
public abstract class AbstractLifeCycleService implements LifeCycleService,
		StateManageable {
	protected Logger log = Logger.getLogger(getClass().getName());

	private String objectName = null;

	protected int state = -1;

	public AbstractLifeCycleService() {
	}

	/**
	 *��ʼ������,��ʼ���������������,�����������������������Ĺ�ϵ
	 */
	public void init() throws ServiceException {
		//        log.info(getName()+" initialized");
	}

	/**
	 * ��������,����ʱ��Ҫ�������������ķ���,ֻ����Щ����������,����������
	 * ʵ�ʷ���������һЩ��Դ
	 */
	public void start() throws ServiceException {
		//        log.info(getName()+" started");
	}

	/**
	 * ֹͣ����,ֹͣʱ��Ҫ������֧�ֵķ���,��ֹͣ��֮ǰ,��Щ���������ֹͣ.
	 * ʵ�ʷ�������Ҫ�ͷ���ռ�е���Դ.
	 */
	public void stop() throws ServiceException {
		//        log.info(getName()+" stopped");
	}

	/**
	 * ɾ��������Ҫִ�еĶ���
	 */
	public void destroy() throws ServiceException {
		//        log.info(getName()+"  destroyed");
	}

	public void setName(String name) {
		this.objectName = name;
	}

	public String getName() {
		return objectName;
	}

	public static void main(String[] args) {

	}

//	public MServiceServer getServer() {
//		return MServiceServerFactory.getMServiceServer();
//	}

	public int getState() {
		return this.state;
	}

	public String getStateString() {
		return this.states[state];
	}

	protected void beforeInit() {
		this.state = this.UNINITIALIZED;
	}

	protected void afterInit() {
		this.state = this.CREATED;
	}

	protected void beforeStart() {
		this.state = this.STARTING;
	}

	protected void afterStart() {
		this.state = this.RUNNING;
	}

	protected void beforeStop() {
		this.state = this.STOPPING;
	}

	protected void afterStop() {
		this.state = this.STOPPED;
	}

	protected void beforeDestroy() {

	}

	protected void afterDestroy() {
		this.state = this.DESTROYED;
	}
	
	public void initMService(){
		this.beforeInit();
		try {
			this.init();
		} catch (ServiceException e) {
			this.state = this.FAILED;
			e.printStackTrace();
		}
		this.afterInit();
	}
	
	public void startMService(){
		this.beforeStart();
		try {
			this.start();
		} catch (ServiceException e) {
			this.state = this.FAILED;
			e.printStackTrace();
		}
		this.afterStart();
	}
	
	public void stopMService(){
		this.beforeStop();
		try {
			this.stop();
		} catch (ServiceException e) {
			this.state = this.FAILED;
			e.printStackTrace();
		}
		this.afterStop();
	}
	
	public void destroyMService(){
		this.beforeDestroy();
		try {
			this.destroy();
		} catch (ServiceException e) {
			this.state = this.FAILED;
			e.printStackTrace();
		}
		this.afterDestroy();
	}
}