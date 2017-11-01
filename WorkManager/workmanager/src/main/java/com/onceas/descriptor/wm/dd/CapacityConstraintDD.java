package com.onceas.descriptor.wm.dd;

public class CapacityConstraintDD {
	  private String name;
	  private int count;
	  public CapacityConstraintDD(String name, int count) {
		  this.count = count;
		  this.name = name;
	  }

	  public String getName() {
	    return name;
	  }

	  public int getCount() {
	    return count;
	  }
}
