package workmanager;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.logging.*;
import java.util.logging.Logger;

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
		public int getCompletedCount() { return completedCount; }
		public double getCurrentThroughput() {
			return requestManager.getThroughput();
		}
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
			String headers =
			  "datetime, milliseconds," +
			  "ActiveExecuteThreadCount," +
			  "CompletedCount,TotalRequestsCount," +
			  "CurrentThroughput,CurrentDropRate";

			out = new PrintWriter(new BufferedWriter(new FileWriter(pattern, append)));
			if(!exist || !append) {
				out.println(headers);
			}

			// System.out.println("Writing Workmanager log to " + pattern);
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
	public RequestManager getRequestManager() {
		return requestManager;
	}

	private String configParse(String origin) {
		/* expand 'catalina.base' */

		if(origin == null || origin.length() == 0) return null;

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

		requestManager = RequestManager.newInstance();

		String logfile = configParse(System.getProperty("workmanager.logFile"));
		if(logfile != null) {
			Boolean logfileAppend = Boolean.valueOf(System.getProperty("workmanager.logAppend"));
			int logInterval = Integer.parseInt(System.getProperty("workmanager.logInterval", String.valueOf(100)));
			final long createMillis = System.currentTimeMillis();

			stats = new ExecutorImplStats();
			logFileHandler = new CSVFileHandler(logfile, logfileAppend);
			log = Logger.getAnonymousLogger();
			log.addHandler(logFileHandler);

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
			}, 0, logInterval);
		}
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
