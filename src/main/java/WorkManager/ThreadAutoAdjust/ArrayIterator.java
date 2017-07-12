package WorkManager.ThreadAutoAdjust;



/**
 * <p>Title: Work Manager��ʵ��</p>
 *
 * <p>Description: ����OnceASƽ̨</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: �й���ѧԺ����о���</p>
 *
 * @author ����
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
