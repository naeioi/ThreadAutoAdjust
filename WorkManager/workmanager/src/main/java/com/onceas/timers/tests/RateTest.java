package com.onceas.timers.tests;

import java.util.Timer;
import java.util.TimerTask;

import com.onceas.timers.TimerListener;
import com.onceas.timers.internal.TimerManagerImpl;
import com.onceas.work.WorkManagerFactory;

public class RateTest extends TimerTask implements TimerListener {

	public RateTest() {
	}

	public static void main(String args[]) throws InterruptedException {
		if (args.length > 0) {
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new RateTest(), 0L, 1000L);
			System.out.println("USING JAVA UTIL TIMER ...");
		} else {
			System.out.println("USING ONCEAS TIMER ...");
			TimerManagerImpl timermanagerimpl = TimerManagerImpl
					.getTimerManager("TEST", WorkManagerFactory.getInstance()
							.getDefault());
			timermanagerimpl.scheduleAtFixedRate(new RateTest(), 0L, 1000L);
		}
		Thread.sleep(0x186a0L);
	}

	public void timerExpired(com.onceas.timers.Timer timer) {
		System.out.println("Timer fired after "
				+ (System.currentTimeMillis() - lastTime) + "ms");
		count++;
		try {
			if (count % 5 == 0)
				Thread.sleep(1800L);
		} catch (InterruptedException interruptedexception) {
			interruptedexception.printStackTrace();
		}
		lastTime = System.currentTimeMillis();
	}

	public void run() {
		timerExpired(null);
	}

	int count;

	long lastTime;
}
