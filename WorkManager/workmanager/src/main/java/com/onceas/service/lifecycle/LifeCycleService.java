package com.onceas.service.lifecycle;


public interface LifeCycleService {
    /**
     * 准备启动
     */
    void init() throws ServiceException;
    /**
     * 完成准备,启动
     */
    void start()throws ServiceException;
    /**
     * 释放start时申请的资源,进入休眠状态
     */
    void stop()throws ServiceException;
     /**
      * 释放所有资源
      */
    void destroy()throws ServiceException;

    /**
     * 返回该服务的名字
     * @return 名
     */
    String getName();

    void setName(String name);
}
