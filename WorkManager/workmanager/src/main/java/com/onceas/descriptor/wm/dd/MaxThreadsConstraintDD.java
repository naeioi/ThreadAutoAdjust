package com.onceas.descriptor.wm.dd;

public class MaxThreadsConstraintDD {
	  private String name;
	  private int count;
	  public MaxThreadsConstraintDD(String name, int count) {
		  this.name = name;
		  this.count = count;
	  }

	  public String getName() {
	    return name;
	  }

	  public int getCount() {
	    return count;
	  }
}
