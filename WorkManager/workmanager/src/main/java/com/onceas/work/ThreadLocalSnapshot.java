package com.onceas.work;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class using to take a snapshot for the threadlocals of the given
 * thread.
 * 
 * @author songyk
 * 2013.12.27
 * 
 */
@SuppressWarnings("rawtypes")
public final class ThreadLocalSnapshot {
	private static Logger log = Logger.getLogger(ThreadLocalSnapshot.class
			.toString());
	private ThreadLocalSnapshot(){}

	/**
	 * Taking snapshot for the given thread
	 * 
	 * @param t
	 * @return map with threadlocal key and its value  array that stores the threadlocals. zero length menas doing
	 *         nothing.
	 */
	public static  Map<ThreadLocal,Object> take(Thread t) {
		if (t == null) {
			return Collections.emptyMap();
		}
		
		Map<ThreadLocal,Object>  result = new HashMap<ThreadLocal,Object>();
		try{
			Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
			threadLocalsField.setAccessible(true);
			Object threadLocals = threadLocalsField.get(t); //ThreadLocal.ThreadLocalMap
	
			if (threadLocals != null) {
				Field tableField = threadLocals.getClass().getDeclaredField("table");
				tableField.setAccessible(true);
				Object[] table = (Object[]) tableField.get(threadLocals);  // ThreadLocal.ThreadLocalMap.Entry[]
				if(table != null){
					for (Object entry : table) {
						if (entry != null) {
							
							// threadlocal
							Method[] methods = entry.getClass().getMethods();
							Method getMethod = null;
							for(Method m : methods){
								if("get".equals(m.getName())){ 
									getMethod = m;
									break;
								}
							}
							
							getMethod.setAccessible(true);
							Object threadLocal = getMethod.invoke(entry);
							
							//value
							Field valueField = entry.getClass().getDeclaredField("value");
							valueField.setAccessible(true);
							Object value = valueField.get(entry);
							
							if (threadLocal == null){
								log.info("the reference of entry object is null which has the value object:"+value.toString());
								continue;
							}
							
							result.put((ThreadLocal) threadLocal, value);
							
						}
					}
				}
			}
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}

	/**
	 * Clear the given threadlcoals in the current thread.
	 * 
	 * @return true if operation succeeds, else false
	 */
	public static boolean clear( Collection<ThreadLocal> threadLocalsToRemove) {
		if(threadLocalsToRemove == null || threadLocalsToRemove.isEmpty()){
			return false;
		}
		
		for(ThreadLocal threadLocal : threadLocalsToRemove){
			threadLocal.remove();
		}
		
		return true;
	}

	/**
	 * set the threadlocals specified by the newThtreadLocals parameter in
	 * the current thread
	 * 
	 * @param targetThtreadLocals  map contains the threadlocal and its value
	 * @return true if finishes the set process
	 */
	@SuppressWarnings({ "unchecked" })
	public  static boolean set(Map<ThreadLocal,Object> targetThtreadLocals) {
		if(targetThtreadLocals == null || targetThtreadLocals.isEmpty()){
			return false;
		}
		
		for(ThreadLocal threadLocal : targetThtreadLocals.keySet()){
			if(threadLocal!=null)
			threadLocal.set(targetThtreadLocals.get(threadLocal));
		}
		
		return true;
	}

	/**
	 * Check the field threadLocals attached to the specified thread whether exists.
	 * @param t
	 * @return true if the field exists
	 */
	public static boolean hasThreadLocal(Thread t) {
		if (t == null) {
			return false;
		}
		
		try {
			Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
			threadLocalsField.setAccessible(true);
			Object threadLocals = threadLocalsField.get(t); //ThreadLocal.ThreadLocalMap

			if (threadLocals != null){
				return true;
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}		
			
		return false;
	}

}
