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
 *所有服务的父类.这是为了方便MServiceServer进行生命周期管理.一个服务的启动过程可以分为
 * 4个步骤:init(),start(),stop(),destroy().这几个函数执行完后,服务分别进入INITIALIZED
 * 等状态.只有在STARTED状态才能被访问.
 * 使用该类用户不必创建这4个方法.
 * @author 胡建华
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
	 *初始化方法,初始化服务自身的属性,不考虑这个服务与其他服务的关系
	 */
	public void init() throws ServiceException {
		//        log.info(getName()+" initialized");
	}

	/**
	 * 启动服务,启动时需要考虑它所依赖的服务,只有那些服务都启动了,它才能启动
	 * 实际服务中申请一些资源
	 */
	public void start() throws ServiceException {
		//        log.info(getName()+" started");
	}

	/**
	 * 停止服务,停止时需要考虑它支持的服务,在停止它之前,那些服务必须先停止.
	 * 实际服务中需要释放所占有的资源.
	 */
	public void stop() throws ServiceException {
		//        log.info(getName()+" stopped");
	}

	/**
	 * 删除服务所要执行的动作
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