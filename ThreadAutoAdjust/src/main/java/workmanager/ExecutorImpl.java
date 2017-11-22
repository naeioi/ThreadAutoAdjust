package workmanager;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.logging.*;

final public class ExecutorImpl implements Executor {
	protected class ExecutorImplStats {
		private int lastReceivedCount = -1;
		private int lastAcceptedCount = -1;
		private double currentDropRate = 0;

		public int getActiveExecuteThreadCount() {
			return requestManager.getActiveExecuteThreadCount();
		}
		public int getTotalRequestsCount() {
			return requestManager.getTotalRequestsCount();
		}
		public int getCompletedCount() {
			return completedCount;
		}
		public double getCurrentThroughput() {
			return requestManager.getThroughput();
		}
		public int getReceivedCount() { return receivedCount; }
		public int getAcceptedCount() { return acceptedCount; }
		private void updateCurrentDropRate() {
			currentDropRate =
			  lastAcceptedCount == -1 ? 0 : 1 - (double)(acceptedCount - lastAcceptedCount) / (receivedCount - lastReceivedCount);
			lastAcceptedCount = acceptedCount;
			lastReceivedCount = receivedCount;
		}
		public double getCurrentDropRate() {
			return currentDropRate;
		}
	}

	protected class CSVFileHandler extends FileHandler {
		private PrintWriter out;
		final private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		public CSVFileHandler(String pattern, boolean append) throws IOException {
			File file = new File(pattern);
			boolean exist = file.exists();

			out = new PrintWriter(new BufferedWriter(new FileWriter(pattern, append)));
			if(!exist || !append) {
				out.println(
				  "datetime, milliseconds," +
					"ActiveExecuteThreadCount," +
					"CompletedCount,TotalRequestsCount," +
					"CurrentThroughput,CurrentDropRate");
			}
			System.out.println("Writing Workmanager log to " + pattern);
		}

		@Override
		public void publish(LogRecord record) {
			out.format("%s,%s\n", dateFormatter.format(Calendar.getInstance().getTime()), record.getMessage());
			out.flush();
		}

		@Override
		public void close() {
			out.close();
		}
	}

	private FileHandler logFileHandler;
	private Logger log;
	private int receivedCount = 0;
	private int acceptedCount = 0;
	private int completedCount = 0;
	private RequestManager requestManager;
	public ExecutorImplStats stats;

	private String getCatalinaBase() {
		return System.getProperty("catalina.base");
	}

	private String configParse(String origin) {
		System.out.println(origin);
		final String CATALINA_BASE_TOKEN = "${catalina.base}";
		String replaced = origin;
		int i;
		while ((i=replaced.indexOf(CATALINA_BASE_TOKEN))>=0) {
			if (i>0) {
				replaced = replaced.substring(0,i) + getCatalinaBase()
				  + replaced.substring(i+CATALINA_BASE_TOKEN.length());
			} else {
				replaced = getCatalinaBase()
				  + replaced.substring(CATALINA_BASE_TOKEN.length());
			}
		}
		return replaced;
	}

	public ExecutorImpl() throws IOException {
		requestManager = RequestManager.getInstance();
		stats = new ExecutorImplStats();
		logFileHandler = new CSVFileHandler(configParse(System.getProperty("workmanager.logFile")), Boolean.valueOf(System.getProperty("workmanager.logAppend")));
		log = Logger.getAnonymousLogger();
		log.addHandler(logFileHandler);
		// log.addHandler(new ConsoleHandler());

		final long createMillis = System.currentTimeMillis();
		(new Timer(true)).schedule(new TimerTask() {
			@Override
			public void run() {
				stats.updateCurrentDropRate();
				log.info(
				  	System.currentTimeMillis() - createMillis + "," +
					  stats.getActiveExecuteThreadCount() + "," +
					stats.getCompletedCount() + "," +
					stats.getTotalRequestsCount() + "," +
					stats.getCurrentThroughput() + "," +
					stats.getCurrentDropRate());

			}
		}, 0, Integer.parseInt(System.getProperty("workmanager.logInterval", String.valueOf(100))));

	}

	public RequestManager getRequestManager() {
		return requestManager;
	}

	public void execute(final Runnable command) {
		boolean success = requestManager.executeIt(new WorkAdapter(new Runnable() {
			public void run() {
				completedCount++;
				command.run();
			}
		}));
		receivedCount++;
		if(success)
			acceptedCount++;
	}
}
