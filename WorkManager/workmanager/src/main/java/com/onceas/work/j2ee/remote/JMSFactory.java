package com.onceas.work.j2ee.remote;

import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSFactory {
	private static Map connectionMap = new ConcurrentHashMap();

	private static Map queueMap = new ConcurrentHashMap();

	public static QueueConnection getConnection(String node) {
		QueueConnection con;
		if (connectionMap.containsKey(node)) {
			Queue queue = (Queue) connectionMap.get(node);
			if (queue.size() != 0) {
				con = (QueueConnection) queue.remove();
				if (con == null) {
					con = createNewConnection(node);
					queue.add(con);
				}
				return con;
			} else {
				con = createNewConnection(node);
				queue.add(con);
				return con;
			}
		} else {
			con = createNewConnection(node);
			Queue queue = new ConcurrentLinkedQueue();
			queue.add(con);
			if (connectionMap.containsKey(node)) {
				Queue queue1 = (Queue) connectionMap.get(node);
				if (queue.size() != 0) {
					con = (QueueConnection) queue1.remove();
					if (con == null) {
						con = createNewConnection(node);
						queue1.add(con);
					}
					return con;
				} else {
					con = createNewConnection(node);
					queue1.add(con);
					return con;
				}
			}
			connectionMap.put(node, queue);
			return con;
		}
	}

	public static void recycleConncetion(String node, QueueConnection con) {
		Queue queue = (Queue) connectionMap.get(node);
		queue.add(con);
	}

	private static QueueConnection createNewConnection(String node) {
		Hashtable env = new Hashtable();
		InitialContext ctx;
		QueueConnectionFactory conFactory;
		QueueConnection con;
		env.put("java.naming.factory.initial",
				"com.onceas.ons.spi.ONSContextFactory");
		env.put("java.naming.factory.url.pkgs",
				"com.onceas.naming:com.onceas.ons.spi.url");
		env.put("java.naming.provider.url", node + ":10999");
		try {
			ctx = new InitialContext(env);
			conFactory = (QueueConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = conFactory.createQueueConnection();
			return con;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static javax.jms.Queue getQueue(String node, String qName) {
		javax.jms.Queue queue;
		Map map;
		if (queueMap.containsKey(node)) {
			map = (Map) queueMap.get(node);
			queue = (javax.jms.Queue) map.get(qName);
			if (queue == null) {
				queue = findQueue(node, qName);
				map.put(qName, queue);
			}
		} else {
			queue = findQueue(node, qName);
			map = new ConcurrentHashMap();
			map.put(qName, queue);
			queueMap.put(node, map);
		}
		return queue;
	}

	private static javax.jms.Queue findQueue(String node, String qName) {
		Hashtable env = new Hashtable();
		InitialContext ctx;
		QueueConnectionFactory conFactory;
		QueueConnection con;
		env.put("java.naming.factory.initial",
				"com.onceas.ons.spi.ONSContextFactory");
		env.put("java.naming.factory.url.pkgs",
				"com.onceas.naming:com.onceas.ons.spi.url");
		env.put("java.naming.provider.url", node + ":10999");
		try {
			ctx = new InitialContext(env);
			javax.jms.Queue queue = (javax.jms.Queue) ctx.lookup(qName);
			return queue;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
