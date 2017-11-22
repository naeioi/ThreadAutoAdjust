package workmanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Main {

	private static Object lock;

	/* simulate dynamic workload change */
	static long startMilliseconds = System.currentTimeMillis();
	private static int getSelection() {
		long elapsedSeconds = (System.currentTimeMillis() - startMilliseconds) / 1000;
		int sel = (elapsedSeconds / 100) % 2 == 0 ? 0 : 1;
		return sel;

	}

	private static int getDelay() {
		return 10;
	}

	private static void loadConfig() throws IOException {
		/* Only for module test
		 * when integrated into Tomcat
		 * properties are stored in catalina.properties
		 * and can be retrieved from System.getProperty()
		 */
		InputStream in = Main.class.getClassLoader().getResourceAsStream("config.properties");
		Properties props = new Properties();
		props.load(in);
		for(Enumeration names = props.propertyNames(); names.hasMoreElements();) {
			String key = String.valueOf(names.nextElement());
			System.setProperty(key, String.valueOf(props.getProperty(key)));
		}
	}

	public static void main(String[] args) throws IOException {
		loadConfig();
		final ExecutorImpl executor = new ExecutorImpl();
		final RequestManager rm = executor.getRequestManager();
		final long createdMillis = System.currentTimeMillis();
		(new Timer(true)).schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println(
				  System.currentTimeMillis() - createdMillis + "," +
				  executor.stats.getActiveExecuteThreadCount() + "," +
				  executor.stats.getCompletedCount() + "," +
				  executor.stats.getTotalRequestsCount() + "," +
				  executor.stats.getCurrentThroughput());

			}
		}, 0, 500);
		final Random rand = new Random();
		lock = new Object();
		while(true) {
			executor.execute(new Runnable() {
				public void run() {
					int sel = getSelection();
					if(sel == 0) {
						try {
							Thread.sleep(rand.nextInt(300));
						} catch (InterruptedException e) {
						/* no-op */
						}
					}
					else if(sel == 1) {
						try {
							final int over = executor.stats.getActiveExecuteThreadCount() - 20;
							Thread.sleep(rand.nextInt((int) (300 + (over > 0 ? Math.pow(over, 2)  * 5 : 0))));
						} catch (InterruptedException e) {
						/* no-op */
						}
					}
					else if(sel == 3) {
						synchronized (lock) {
							/* simulate waiting on free lock */
							try {
								Thread.sleep(rand.nextInt(30));
							} catch (InterruptedException e) {
								/* no-op */
							}
						}
					}
					else if(sel == 4) {
						synchronized (lock) {
							/* simulate waiting on free lock */
							try {
								final int activeThreads = executor.stats.getActiveExecuteThreadCount() - 20;
								Thread.sleep(rand.nextInt(20) + activeThreads <= 0 ? 0 : (int)Math.pow(activeThreads, 2) * 1);
							} catch (InterruptedException e) {
								/* no-op */
							}
						}
					}
				}
			});
			try {
				Thread.sleep(rand.nextInt(getDelay()));
			} catch (InterruptedException e) {
				/* no-op */
			}
		}
	}
}
