/*
 * 与om.onceas.management.util.MBeanServer功能相同，获得JVM内的一个可用MBeanServer
 * 
 */

package com.onceas.util.jmx;

import java.util.Iterator;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;


public class MBeanServerLoader
{
   public static MBeanServer load(final String agentID)
   {
      MBeanServer server = (MBeanServer)
         MBeanServerFactory.findMBeanServer(agentID).iterator().next();

      return server;
   }

   public static MBeanServer load()
   {
      return load(null);
   }

   public static MBeanServer loadDefault() {
     return loadOnceas();
   }

   public static MBeanServer loadOnceas()
   {
      for (Iterator i = MBeanServerFactory.findMBeanServer(null).iterator(); i.hasNext(); )
      {
        MBeanServer server = (MBeanServer) i.next();
        if (server!=null)
           return server;

      }
      throw new IllegalStateException("No 'onceas' MBeanServer found!");
   }
}
