package com.onceas.work;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import com.onceas.work.constraint.RequestManager;
import com.onceas.work.constraint.ServerWorkManagerImpl;

public class WorkManagerImageSource
// implements ImageSource
{

	private WorkManagerImageSource() {
	}

	public static WorkManagerImageSource getInstance() {
		return THE_ONE;
	}

	public synchronized void register(
			ServerWorkManagerImpl serverworkmanagerimpl) {
		workManagers.add(serverworkmanagerimpl);
	}

	public synchronized void deregister(
			ServerWorkManagerImpl serverworkmanagerimpl) {
		workManagers.remove(serverworkmanagerimpl);
	}

	public synchronized void createDiagnosticImage(OutputStream outputstream)
	// throws ImageSourceCreationException
	{
		RequestManager requestmanager = RequestManager.getInstance();
		PrintWriter printwriter = new PrintWriter(outputstream);
		printwriter.println("Total thread count  : "
				+ requestmanager.getExecuteThreadCount());
		printwriter.println("Idle thread count   : "
				+ requestmanager.getIdleThreadCount());
		printwriter.println("Standby thread count: "
				+ requestmanager.getStandbyCount());
		printwriter.println("Queue depth         : "
				+ requestmanager.getQueueDepth());
		printwriter.println("Queue departures    : "
				+ requestmanager.getQueueDepartures());
		printwriter.println("Mean throughput     : "
				+ requestmanager.getThroughput());
		printwriter.println("Total requests      : "
				+ requestmanager.getTotalRequestsCount());
		ServerWorkManagerImpl serverworkmanagerimpl;
		for (Iterator iterator = workManagers.iterator(); iterator.hasNext(); serverworkmanagerimpl
				.dumpInformation(printwriter))
			serverworkmanagerimpl = (ServerWorkManagerImpl) iterator.next();

		printwriter.flush();
	}

	public void timeoutImageCreation() {
	}

	private static final WorkManagerImageSource THE_ONE = new WorkManagerImageSource();

	final ArrayList workManagers = new ArrayList();

}
