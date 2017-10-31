package workmanager;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
	private static final int INCREMENT_ADVISOR_PERIOD = 2000;

	private static final int INCREMENT_ADVISOR_START_DELAY = 10000;

	private static int USER_NUMBER = 200;

	public static void main(String[] args) {
		final RequestManager rm = RequestManager.getInstance();
		(new Timer(true)).schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println(rm.getActiveExecuteThreadCount());

			}
		}, 0, 2000);
		for (int i = 0;; i++) {
			WorkAdapter workAdapter=new WorkAdapter();
			rm.executeIt(workAdapter);
			
		}
//		Thread.currentThread().suspend();

	}
}
