package WorkManager.ThreadAutoAdjust;

import java.util.Random;
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
			// WorkAdapter workAdapter=new WorkAdapter();
			rm.executeIt();
			
		}
//		Thread.currentThread().suspend();

	}

	// private static class Task implements Work{
	// @Override
	// public void run() {
	// Random Random=new Random();
	// long l=System.currentTimeMillis();
	// int index=(int)(Random.nextFloat()*1000000);
	// for(int i=0;i<index;i++){
	// Math.pow(Math.pow(Random.nextDouble(),Random.nextDouble()),Math.pow(Random.nextDouble(),Random.nextDouble()));
	// }
	// System.out.println(System.currentTimeMillis()-l);
	//
	// }
	//
	// @Override
	// public Runnable overloadAction(String s) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public Runnable cancel(String s) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// }
}
