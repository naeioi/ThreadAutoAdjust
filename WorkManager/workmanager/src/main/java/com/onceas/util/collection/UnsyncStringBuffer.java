// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   UnsyncStringBuffer.java

package com.onceas.util.collection;

import java.io.Serializable;

public final class UnsyncStringBuffer
    implements Serializable
{

    public UnsyncStringBuffer()
    {
        this(16);
    }

    public UnsyncStringBuffer(int i)
    {
        value = new char[i];
        shared = false;
    }

    public UnsyncStringBuffer(String s)
    {
        this(s.length() + 16);
        append(s);
    }

    public int length()
    {
        return count;
    }

    public int capacity()
    {
        return value.length;
    }

    private final void copyWhenShared()
    {
        if(shared)
        {
            char ac[] = new char[value.length];
            System.arraycopy(value, 0, ac, 0, count);
            value = ac;
            shared = false;
        }
    }

    public void ensureCapacity(int i)
    {
        int j = value.length;
        if(i > j)
        {
            int k = (j + 1) * 2;
            if(i > k)
                k = i;
            char ac[] = new char[k];
            System.arraycopy(value, 0, ac, 0, count);
            value = ac;
            shared = false;
        }
    }

    public void setLength(int i)
    {
        if(i < 0)
            throw new StringIndexOutOfBoundsException(i);
        ensureCapacity(i);
        if(count < i)
        {
            copyWhenShared();
            for(; count < i; count++)
                value[count] = '\0';

        }
        count = i;
    }

    public char charAt(int i)
    {
        if(i < 0 || i >= count)
            throw new StringIndexOutOfBoundsException(i);
        else
            return value[i];
    }

    public void getChars(int i, int j, char ac[], int k)
    {
        if(i < 0 || i >= count)
            throw new StringIndexOutOfBoundsException(i);
        if(j < 0 || j > count)
            throw new StringIndexOutOfBoundsException(j);
        if(i < j)
            System.arraycopy(value, i, ac, k, j - i);
    }

    public void setCharAt(int i, char c)
    {
        if(i < 0 || i >= count)
        {
            throw new StringIndexOutOfBoundsException(i);
        } else
        {
            copyWhenShared();
            value[i] = c;
            return;
        }
    }

    public UnsyncStringBuffer append(Object obj)
    {
        return append(String.valueOf(obj));
    }

    public UnsyncStringBuffer append(String s)
    {
        if(s == null)
            s = String.valueOf(s);
        int i = s.length();
        ensureCapacity(count + i);
        copyWhenShared();
        s.getChars(0, i, value, count);
        count += i;
        return this;
    }

    public UnsyncStringBuffer append(char ac[])
    {
        int i = ac.length;
        ensureCapacity(count + i);
        copyWhenShared();
        System.arraycopy(ac, 0, value, count, i);
        count += i;
        return this;
    }

    public UnsyncStringBuffer append(char ac[], int i, int j)
    {
        ensureCapacity(count + j);
        copyWhenShared();
        System.arraycopy(ac, i, value, count, j);
        count += j;
        return this;
    }

    public UnsyncStringBuffer append(boolean flag)
    {
        return append(String.valueOf(flag));
    }

    public UnsyncStringBuffer append(char c)
    {
        ensureCapacity(count + 1);
        copyWhenShared();
        value[count++] = c;
        return this;
    }

    public UnsyncStringBuffer append(int i)
    {
        return append(String.valueOf(i));
    }

    public UnsyncStringBuffer append(long l)
    {
        return append(String.valueOf(l));
    }

    public UnsyncStringBuffer append(float f)
    {
        return append(String.valueOf(f));
    }

    public UnsyncStringBuffer append(double d)
    {
        return append(String.valueOf(d));
    }

    public UnsyncStringBuffer insert(int i, Object obj)
    {
        return insert(i, String.valueOf(obj));
    }

    public UnsyncStringBuffer insert(int i, String s)
    {
        if(i < 0 || i > count)
        {
            throw new StringIndexOutOfBoundsException();
        } else
        {
            int j = s.length();
            ensureCapacity(count + j);
            copyWhenShared();
            System.arraycopy(value, i, value, i + j, count - i);
            s.getChars(0, j, value, i);
            count += j;
            return this;
        }
    }

    public UnsyncStringBuffer insert(int i, char ac[])
    {
        if(i < 0 || i > count)
        {
            throw new StringIndexOutOfBoundsException();
        } else
        {
            int j = ac.length;
            ensureCapacity(count + j);
            copyWhenShared();
            System.arraycopy(value, i, value, i + j, count - i);
            System.arraycopy(ac, 0, value, i, j);
            count += j;
            return this;
        }
    }

    public UnsyncStringBuffer insert(int i, boolean flag)
    {
        return insert(i, String.valueOf(flag));
    }

    public UnsyncStringBuffer insert(int i, char c)
    {
        ensureCapacity(count + 1);
        copyWhenShared();
        System.arraycopy(value, i, value, i + 1, count - i);
        value[i] = c;
        count++;
        return this;
    }

    public UnsyncStringBuffer insert(int i, int j)
    {
        return insert(i, String.valueOf(j));
    }

    public UnsyncStringBuffer insert(int i, long l)
    {
        return insert(i, String.valueOf(l));
    }

    public UnsyncStringBuffer insert(int i, float f)
    {
        return insert(i, String.valueOf(f));
    }

    public UnsyncStringBuffer insert(int i, double d)
    {
        return insert(i, String.valueOf(d));
    }

    public UnsyncStringBuffer reverse()
    {
        copyWhenShared();
        int i = count - 1;
        for(int j = i - 1 >> 1; j >= 0; j--)
        {
            char c = value[j];
            value[j] = value[i - j];
            value[i - j] = c;
        }

        return this;
    }

    public String toString()
    {
        shared = true;
        return new String(value, 0, count);
    }

    private char value[];
    private int count;
    private boolean shared;
    static final long serialVersionUID = 0x2f0707d9eac8ead3L;
}
