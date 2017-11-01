package com.onceas.util.security;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;


/**
 * This class is used for storing security info of the user.
 * Referenced com.onceas.security.SecurityContext
 */
public final class SecurityContext {



  /**the scope is global or thread */
  private static boolean isGlobal;

  private static Subject subject;
  private static Principal principal;
  private static Object auth_data;

  //private static Boolean isClient;

  private static ThreadLocal sessionSubject;
  private static ThreadLocal sessionPrincipal;
  private static ThreadLocal sessionAuth_data;
  private static ThreadLocal sessionOwnCredentials;       //be set commonly
  private static ThreadLocal sessionReceivedCredentials;  //be set commonly
  private static ThreadLocal sessionTargetCredentials;    //be get commonly, and set in client
  private static ThreadLocal sessionSecRealm;  //be set commonly

//  private static ThreadLocal sessionIsClient;
//  private static RunAsThreadLocalStack sessionRunAsStacks = new RunAsThreadLocalStack();

  static{
    //here should be modified and promoted
    sessionPrincipal = new InheritableThreadLocal();
    sessionAuth_data = new InheritableThreadLocal();
    sessionSubject = new InheritableThreadLocal();
   // sessionTargetCredentials = new InheritableThreadLocal();
  //  sessionReceivedCredentials = new InheritableThreadLocal();
   // sessionOwnCredentials = new InheritableThreadLocal();
   // sessionSecRealm = new InheritableThreadLocal();
    //sessionIsClient = new InheritableThreadLocal();
  }
  /** get principal */
  public static Principal getPrincipal() {
    if (isGlobal)
      return principal;
    else
      return (Principal) sessionPrincipal.get();
  }
  /** get auth_data */
  public static Object getAuth_data(){
    if(isGlobal)
      return auth_data;
    else
      return sessionAuth_data.get();
  }
  /** get subject */
  public static Subject getSubject(){
    if(isGlobal)
      return subject;
    else
      return (Subject)sessionSubject.get();
  }
  /** set principal */
  public static void setPrincipal(Principal principal){
    if (isGlobal)
      SecurityContext.principal = principal;
    else
      sessionPrincipal.set(principal);
  }
  /** set auth_data */
  public static void setAuth_data(Object credential){
    if (isGlobal)
      SecurityContext.auth_data = credential;
    else
      sessionAuth_data.set(credential);
  }
  /** set subject */
  public static void setSubject(Subject subject){
    if (isGlobal)
      SecurityContext.subject = subject;
    else
      sessionSubject.set(subject);
  }

  public static void setIsGlobal(boolean isGlobal){
    SecurityContext.isGlobal = isGlobal;
  }
  /** clean */
  public static void clear(){
    if( isGlobal == false ){
      sessionPrincipal.set(null);
      sessionAuth_data.set(null);
      sessionSubject.set(null);
      sessionOwnCredentials.set(null);
      sessionReceivedCredentials.set(null);
      sessionTargetCredentials.set(null);
      sessionSecRealm.set(null);
      //sessionIsClient.set(null);
    }else{
      SecurityContext.principal = null;
      SecurityContext.auth_data = null;
      SecurityContext.subject = null;
//      SecurityContext.ownCredentials = null;
//      SecurityContext.receivedCredentials = null;
//      SecurityContext.targetCredentials = null;
//      SecurityContext.secRealm = null;
      //SecurityContext.isClient = null;
    }
  }
 /**
  *  get group from subject
  */
  public static Group getGroup(Subject subject) {
	    Set principals = subject.getPrincipals();
	    Iterator it = principals.iterator();
	    Group group = null;
	    while (it.hasNext()) {
	      Principal principal = (Principal) it.next();
	      if (principal instanceof Group)
	    	  group = (Group)principal;
	    }
	    return group;
	  }
}
