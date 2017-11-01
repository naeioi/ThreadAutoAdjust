package com.onceas.descriptor.wm.dd;


public class ContextCaseDD {
	  private String username=null;
	  private String groupname=null;
	  private String requestclassname=null;
	  private String contextClassName=null;

	  
	  public ContextCaseDD(String contextClassName,String requestclassname, String username, String groupname) {
		this.requestclassname = requestclassname;
		this.username = username;
		this.groupname = groupname;
		this.contextClassName = contextClassName;
	}
	public String getUserName() {
	    return username;
	  }
	  public String getGroupName() {
		    return groupname;
		}

	  public String getRequestClassName() {
		    return requestclassname;
		  }
	  
	  public void setContextClassName(String contextClassName){
		  this.contextClassName = contextClassName;
	  }
	  
	  public String getContextClassName(){
		  return contextClassName;
	  }
}
