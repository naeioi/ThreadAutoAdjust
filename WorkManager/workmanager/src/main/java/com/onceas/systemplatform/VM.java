package com.onceas.systemplatform;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VM {
	private static final boolean DEBUG = false;

	private static final GarbageCollectionEvent MINOR_GC_EVENT = new GarbageCollectionEvent(
			1);

	private static final GarbageCollectionEvent MAJOR_GC_EVENT = new GarbageCollectionEvent(
			0);

	private static Field sockImplField;

	private static Field fileDesField;

	private static Field fdField;

	private static Field rawSocketField;

	private static final Queue<GCListener> queue = new ConcurrentLinkedQueue<GCListener>();

	private static VM vm;

	private static VM vm15Delegate;

	private static boolean isJRockit;

	public VM() {
	}

	/**
	 * public int getFD(Socket socket) throws IOException { Object obj;
	 * if(socket instanceof SSLLayeredSocket) socket =
	 * (Socket)rawSocketField.get(socket); SocketImpl socketimpl =
	 * (SocketImpl)sockImplField.get(socket); FileDescriptor filedescriptor =
	 * (FileDescriptor)fileDesField.get(socketimpl); obj =
	 * fdField.get(filedescriptor); if(obj instanceof Integer) return
	 * ((Integer)obj).intValue(); if(obj instanceof Long) return
	 * ((Long)obj).intValue(); String s; try { throw new IOException("Invalid
	 * fd"); } catch(ThreadDeath threaddeath) { throw threaddeath; }
	 * catch(Throwable throwable) { s = "Cannot get fd for sock=" + socket + ": " +
	 * throwable.getMessage(); } throw new IOException(s); }
	 */
	public void threadDump() {
		System.err.println("***** This VM does not support thread dumps *****");
	}

	public void threadDump(String s) throws IOException {
		PrintStream printstream = new PrintStream(new FileOutputStream(s));
		printstream
				.println("***** This VM does not support thread dumps *****");
		printstream.close();
	}

	public void threadDump(FileDescriptor filedescriptor) throws IOException {
		PrintStream printstream = new PrintStream(new FileOutputStream(
				filedescriptor));
		printstream
				.println("***** This VM does not support thread dumps *****");
		printstream.close();
	}

	public void threadDump(File file) throws IOException {
		PrintStream printstream = new PrintStream(new FileOutputStream(file));
		printstream
				.println("***** This VM does not support thread dumps *****");
		printstream.close();
	}

	public void threadDump(PrintWriter printwriter) {
		printwriter
				.println("***** This VM does not support thread dumps *****");
		printwriter.flush();
	}

	public String threadDumpAsString(Thread thread) {
		if (vm15Delegate != null)
			return vm15Delegate.threadDumpAsString(thread);
		else
			return null;
	}

	public String threadDumpAsString() {
		if (vm15Delegate != null)
			return vm15Delegate.threadDumpAsString();
		else
			return null;
	}

	public String dumpDeadlockedThreads() {
		if (vm15Delegate != null)
			return vm15Delegate.dumpDeadlockedThreads();
		else
			return null;
	}

	public boolean isNativeThreads() {
		return true;
	}

	public String getName() {
		return "UnknownVM";
	}

	public static synchronized VM getVM() {
		if (vm != null)
			return vm;
		String s = System.getProperty("java.vm.vendor");
		if (s == null)
			s = "";
		s = s.toLowerCase();
		String s1 = System.getProperty("java.version");
		if (s1 == null)
			s1 = "";
		s1 = s1.toLowerCase();
		String s2 = System.getProperty("os.name");
		if (s2 == null)
			s2 = "";
		s2 = s2.toLowerCase();
		String s3 = System.getProperty("os.arch");
		if (s3 == null)
			s3 = "";
		s3 = s3.toLowerCase();
		initVM15Delegate(s1);
		if (s.indexOf("sun") >= 0 || s.indexOf("apple") >= 0)
			try {
				vm = (VM) Class.forName("com.onceas.systemplatform.SunVM")
						.newInstance();
			} catch (Throwable throwable) {
			}
		else if (s.toLowerCase().indexOf("digital equi") >= 0) {
			if (s3.toLowerCase().indexOf("alpha") >= 0)
				try {
					vm = (VM) Class.forName("com.onceas.systemplatform.SunVM")
							.newInstance();
				} catch (Throwable throwable1) {
				}
		} else if (s.toLowerCase().indexOf("appeal") > -1
				|| s.toLowerCase().indexOf("bea") > -1)
			try {
				vm = (VM) Class.forName("com.onceas.systemplatform.JRockitVM")
						.newInstance();
				isJRockit = true;
			} catch (Throwable throwable2) {
			}
		if (vm == null)
			vm = new VM();
		return vm;
	}

	private static void initVM15Delegate(String s) {
		try {
			if (s.indexOf("1.5") >= 0)
				vm15Delegate = (VM) Class.forName(
						"com.onceas.systemplatform.VM15").newInstance();
		} catch (Throwable throwable) {
			System.err
					.println("***** FATAL: Unable to initialize com.onceas.systemplatform.VM15 *****");
		}
	}

	public final void addGCListener(GCListener gclistener) {
		if (gclistener != null)
			queue.add(gclistener);
	}

	protected final void sendMinorGCEvent() {
		sendGCEvent(MINOR_GC_EVENT);
	}

	protected final void sendMajorGCEvent() {
		sendGCEvent(MAJOR_GC_EVENT);
	}

	private final void sendGCEvent(GarbageCollectionEvent garbagecollectionevent) {
		for (GCListener gclistener : queue) {
			gclistener.onGarbageCollection(garbagecollectionevent);
		}
	}

	public boolean isJRockit() {
		return isJRockit;
	}
	/**
	 * static Class _mthclass$(String s) { try { return Class.forName(s); }
	 * catch(ClassNotFoundException classnotfoundexception) { throw (new
	 * NoClassDefFoundError()).initCause(classnotfoundexception); } }
	 */

	/**
	 * static { try { sockImplField =
	 * (java.net.Socket.class).getDeclaredField("impl");
	 * sockImplField.setAccessible(true); fileDesField =
	 * (java.net.SocketImpl.class).getDeclaredField("fd");
	 * fileDesField.setAccessible(true); fdField =
	 * (java.io.FileDescriptor.class).getDeclaredField("fd");
	 * fdField.setAccessible(true); rawSocketField =
	 * (javax.net.ssl.impl.SSLLayeredSocket.class).getDeclaredField("socket");
	 * rawSocketField.setAccessible(true); } catch(NoSuchFieldException
	 * nosuchfieldexception) { } catch(SecurityException securityexception) { } }
	 */
}
