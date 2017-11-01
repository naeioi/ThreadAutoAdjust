package com.onceas.service.lifecycle;

/**
 * <p>Title: </p>
 * <p>Description: 关于服务的意外信息</p>
 */

public class ServiceException
   extends Exception
{
   // Attributes ----------------------------------------------------

   /** The root cause of this exception */
   protected Throwable cause;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   public ServiceException(String message)
   {
      super(message);
   }

   public ServiceException(String message, Throwable cause)
   {
      super(message);

      this.cause = cause;
   }

   public ServiceException(Throwable nested) {
      super(nested.getMessage());
   }

   // Public --------------------------------------------------------

   public Throwable getCause() { return cause; }

   public String toString()
   {
      return cause == null ? super.toString()
         : super.toString() + ", Cause: " + cause;
   }
}

