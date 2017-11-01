package com.onceas.descriptor.wm.dd;

public class ResponseTimeRequestDD {
	 private String name;
	 private int response_time;
	 
	 public ResponseTimeRequestDD(String name, int responsetime) {
		this.name = name;
		this.response_time = responsetime;
	}
	public String getName() {
		 return name;
	 }
	 public int getResponseTime(){
		 return response_time;
	 }
}
