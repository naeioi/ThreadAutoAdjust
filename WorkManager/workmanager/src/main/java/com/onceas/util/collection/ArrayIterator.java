package com.onceas.util.collection;



/**
 * <p>Title: Work Manager的实现</p>
 *
 * <p>Description: 基于OnceAS平台</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 中国科学院软件研究所</p>
 *
 * @author 张磊
 * @version 1.0
 */
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ArrayIterator
    implements Iterator
{

    public ArrayIterator(Object aobj[])
    {
        this(aobj, 0, aobj.length);
    }

    public ArrayIterator(Object aobj[], int i, int j)
    {
        if(i < 0)
            throw new IllegalArgumentException();
        if(i > aobj.length)
            throw new IllegalArgumentException();
        if(j > aobj.length - i)
        {
            throw new IllegalArgumentException();
        } else
        {
            array = aobj;
            index = i;
            maxIndex = j + i;
            return;
        }
    }

    public boolean hasNext()
    {
        return index < maxIndex;
    }

    public Object next()
    {
        if(index >= maxIndex)
            throw new NoSuchElementException();
        else
            return array[index++];
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    private final Object array[];
    private final int maxIndex;
    private int index;
}
