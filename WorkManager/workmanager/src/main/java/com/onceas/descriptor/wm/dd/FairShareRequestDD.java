package com.onceas.descriptor.wm.dd;

public class FairShareRequestDD {
	 private String name;
	 private int fair_share;
	 
	 public FairShareRequestDD(String name, int fairshare) {
		this.name = name;
		this.fair_share = fairshare;
	}
	public String getName() {
		 return name;
	 }
	 public int getFairShare(){
		 return fair_share;
	 }
}
