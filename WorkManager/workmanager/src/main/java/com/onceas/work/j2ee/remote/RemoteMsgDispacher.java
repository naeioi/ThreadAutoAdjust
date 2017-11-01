/**
 * TODO 
 * 1 need a session pool and a connection pool
 */
package com.onceas.work.j2ee.remote;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

public class RemoteMsgDispacher {
	public static boolean sendWork(RemoteWorkAdapter work, String node,
			String qName) {
		javax.jms.ObjectMessage objectMessage;
		try {
			System.out.println("send a remote work to " + node);

			QueueConnectionFactory conFactory;

			QueueConnection con;

			QueueSession session;

			QueueSender sender;

			Queue queue;

			long t_c1 = System.currentTimeMillis();

			con = JMSFactory.getConnection(node);

			long t_c2 = System.currentTimeMillis();
			System.out.println("Get a conncetion in " + (t_c2 - t_c1) + "ms");

			long t_q1 = System.currentTimeMillis();

			queue = JMSFactory.getQueue(node, qName);

			long t_q2 = System.currentTimeMillis();
			System.out.println("Get a queue in " + (t_q2 - t_q1) + "ms");

			long t_s1 = System.currentTimeMillis();

			session = con.createQueueSession(false,
					QueueSession.DUPS_OK_ACKNOWLEDGE);

			long t_s2 = System.currentTimeMillis();
			System.out.println("Create a session in " + (t_s2 - t_s1) + "ms");

			con.start();

			long t_s3 = System.currentTimeMillis();

			sender = session.createSender(queue);

			long t_s4 = System.currentTimeMillis();
			System.out.println("Create a sender in " + (t_s4 - t_s3) + "ms");

			sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			objectMessage = session.createObjectMessage();
			objectMessage.clearBody();
			objectMessage.setObject(work);
			objectMessage.setStringProperty(
					RemoteWorkManagerConstant.REMOTE_WORK_CONTEXT_INFO, work
							.getApplicationName());

			long t1 = System.currentTimeMillis();
			System.out.println("start to send at " + t1);

			sender.send(objectMessage);

			long t2 = System.currentTimeMillis();
			System.out.println("finish send in " + (t2 - t1) + "ms");
			sender.close();
			con.stop();
			JMSFactory.recycleConncetion(node, con);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
