package com.onceas.service.lifecycle;


public interface LifeCycleService {
    /**
     * ׼������
     */
    void init() throws ServiceException;
    /**
     * ���׼��,����
     */
    void start()throws ServiceException;
    /**
     * �ͷ�startʱ�������Դ,��������״̬
     */
    void stop()throws ServiceException;
     /**
      * �ͷ�������Դ
      */
    void destroy()throws ServiceException;

    /**
     * ���ظ÷��������
     * @return ��
     */
    String getName();

    void setName(String name);
}
