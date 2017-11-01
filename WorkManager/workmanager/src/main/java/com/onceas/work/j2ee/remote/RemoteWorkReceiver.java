package com.onceas.work.j2ee.remote;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.onceas.work.InheritableThreadContext;
import com.onceas.work.j2ee.J2EEWorkManager;

public class RemoteWorkReceiver

{
	public class WorkArriveListener implements MessageListener {
		public void onMessage(Message msg) {
			ObjectMessage workMsg = (ObjectMessage) msg;
			try {
				System.out.println("receive a remote work");

				String appName = workMsg
						.getStringProperty(RemoteWorkManagerConstant.REMOTE_WORK_CONTEXT_INFO);

				RemoteThreadContext remoteThreadContext = RemoteThreadContext
						.getContext(appName);
				if (remoteThreadContext != null)
					remoteThreadContext.push();

				RemoteWorkAdapter work = (RemoteWorkAdapter) workMsg
						.getObject();
				J2EEWorkManager j2eeWorkManager = (J2EEWorkManager) J2EEWorkManager
						.get(work.getApplicationName(), work.getModuleName(),
								work.getWorkManager());
				J2EEWorkManager.saveConctextInfo(appName,
						InheritableThreadContext.getContext());
				j2eeWorkManager.scheduleRemoteWork(work);
				/**
				 * if (remoteThreadContext != null) remoteThreadContext.pop();
				 */

			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private static Hashtable env = new Hashtable();

	private static InitialContext ctx;

	private static QueueConnectionFactory conFactory;

	private static QueueConnection con;

	private static Queue queue;

	private static QueueSession session;

	private static QueueReceiver receiver;

	static {
		env.put("java.naming.factory.initial",
				"com.onceas.ons.spi.ONSContextFactory");
		env.put("java.naming.factory.url.pkgs",
				"com.onceas.naming:com.onceas.ons.spi.url");
		env.put("java.naming.provider.url", "localhost:10999");
	}

	private static RemoteWorkReceiver remoteWorkReceiver = new RemoteWorkReceiver();

	public static RemoteWorkReceiver getRemoteWorkReceiver() {
		return remoteWorkReceiver;
	}

	private RemoteWorkReceiver() {
		init();
	}

	private void init() {
		try {
			ctx = new InitialContext(env);

			conFactory = (QueueConnectionFactory) ctx
					.lookup("ConnectionFactory");

			con = conFactory.createQueueConnection();

			queue = (Queue) ctx
					.lookup(RemoteWorkManagerConstant.REMOTE_WORK_QUEUE);

			session = con.createQueueSession(false,
					QueueSession.DUPS_OK_ACKNOWLEDGE);

			con.start();

			receiver = session.createReceiver(queue);

			receiver.setMessageListener(new WorkArriveListener());

		} catch (NamingException ex) {
			ex.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void stop() {
		try {
			receiver.close();
			con.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
