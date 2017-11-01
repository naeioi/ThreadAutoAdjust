package com.onceas.descriptor.wm.dd;

public class WorkManagerShutDownTriggerDD {
	 private int max_stuckthreadtime;
	 private int stuckthread_count;
	 
	 
	 public WorkManagerShutDownTriggerDD(int max_stuckthreadtime, int stuckthread_count) {
		this.max_stuckthreadtime = max_stuckthreadtime;
		this.stuckthread_count = stuckthread_count;
	}
	public int getMaxStuckThreadTime() {
		 return max_stuckthreadtime;
	 }
	 public int getStuckThreadCount(){
		 return stuckthread_count;
	 }
}
