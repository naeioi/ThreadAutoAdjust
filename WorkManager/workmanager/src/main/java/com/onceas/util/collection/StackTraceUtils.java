// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   StackTraceUtils.java

package com.onceas.util.collection;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class StackTraceUtils
{

    public StackTraceUtils()
    {
    }

    public static String throwable2StackTrace(Throwable throwable)
    {
        if(throwable == null)
            throwable = new Throwable("[Null exception passed, creating stack trace for offending caller]");
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(bytearrayoutputstream));
        return bytearrayoutputstream.toString();
    }

    public static String throwable2StackTraceTruncated(Throwable throwable, int i)
    {
        if(throwable == null)
            throwable = new Throwable("[Null exception passed, creating stack trace for offending caller]");
        if(i == -1)
        {
            return throwable2StackTrace(throwable);
        } else
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            PrintStream printstream = new PrintStream(bytearrayoutputstream);
            printStackTrace(throwable, printstream, i);
            return bytearrayoutputstream.toString();
        }
    }

    private static void printStackTrace(Throwable throwable, PrintStream printstream, int i)
    {
        printstream.println(throwable);
        StackTraceElement astacktraceelement[] = throwable.getStackTrace();
        int j = i;
        if(j > astacktraceelement.length)
            j = astacktraceelement.length;
        for(int k = 0; k < j; k++)
            printstream.println("\tat " + astacktraceelement[k]);

        if(j < astacktraceelement.length)
            printstream.println("\tTruncated. see log file for complete stacktrace");
        Throwable throwable1 = throwable.getCause();
        if(throwable1 != null)
            printStackTrace(throwable1, printstream, i);
    }

    public static Throwable getThrowableWithCause(Throwable throwable)
    {
        try
        {
            Class aclass[] = {
                String.class
            };
            Constructor constructor = throwable.getClass().getConstructor(aclass);
            Object aobj[] = {
                throwable.getMessage()
            };
            Throwable throwable1 = (Throwable)constructor.newInstance(aobj);
            throwable1.setStackTrace(throwable.getStackTrace());
            setThrowableCause(throwable1, throwable.getCause());
            setThrowableCause(throwable, throwable1);
        }
        catch(IllegalAccessException illegalaccessexception) { }
        catch(InstantiationException instantiationexception) { }
        catch(InvocationTargetException invocationtargetexception) { }
        catch(NoSuchMethodException nosuchmethodexception) { }
        throwable.setStackTrace(trimStackTrace((new Throwable()).getStackTrace(), 1));
        return throwable;
    }

    private static StackTraceElement[] trimStackTrace(StackTraceElement astacktraceelement[], int i)
    {
        if(astacktraceelement.length - i <= 0)
            return astacktraceelement;
        StackTraceElement astacktraceelement1[] = new StackTraceElement[astacktraceelement.length - i];
        for(int j = 0; j < astacktraceelement1.length; j++)
            astacktraceelement1[j] = astacktraceelement[j + i];

        return astacktraceelement1;
    }

    private static void setThrowableCause(Throwable throwable, Throwable throwable1)
        throws IllegalAccessException
    {
        try
        {
            throwable.initCause(throwable1);
        }
        catch(IllegalStateException illegalstateexception)
        {
            try
            {
                Field field = (Throwable.class).getDeclaredField("cause");
                field.setAccessible(true);
                field.set(throwable, throwable1);
                return;
            }
            catch(NoSuchFieldException nosuchfieldexception) { }
            catch(SecurityException securityexception) { }
            catch(IllegalArgumentException illegalargumentexception) { }
            throw new IllegalAccessException("Error setting cause");
        }
    }
}
