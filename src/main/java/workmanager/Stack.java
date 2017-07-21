package workmanager;

/*import weblogic.utils.collections.Stack;
import weblogic.utils.collections.ArrayIterator;
*/
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
import java.util.AbstractCollection;
import java.util.EmptyStackException;
import java.util.Iterator;

// Referenced classes of package weblogic.utils.collections:
//            ArrayIterator

public final class Stack extends AbstractCollection
{

    public Stack()
    {
        this(15);
    }

    public Stack(int i)
    {
        if(i < 0)
        {
            throw new IllegalArgumentException();
        } else
        {
            values = new Object[i];
            pointer = 0;
            return;
        }
    }

    private Stack(Object aobj[], int i)
    {
        values = aobj;
        pointer = i;
    }

    private void resize()
    {
        if(pointer == 0)
        {
            values = new Object[1];
            return;
        } else
        {
            Object aobj[] = new Object[pointer * 2];
            System.arraycopy(((Object) (values)), 0, ((Object) (aobj)), 0, pointer);
            values = aobj;
            return;
        }
    }

    public boolean add(Object obj)
    {
        push(obj);
        return true;
    }

    public void clear()
    {
        Object aobj[] = values;
        while(pointer > 0)
            aobj[--pointer] = null;
    }

    public boolean isEmpty()
    {
        return pointer == 0;
    }

    public Iterator iterator()
    {
        Object aobj[] = new Object[pointer];
        System.arraycopy(((Object) (values)), 0, ((Object) (aobj)), 0, pointer);
        return new ArrayIterator(aobj);
    }

    public Object clone()
    {
        Object aobj[] = new Object[pointer];
        System.arraycopy(((Object) (values)), 0, ((Object) (aobj)), 0, pointer);
        return new Stack(aobj, pointer);
    }

    public int size()
    {
        return pointer;
    }

    public void push(Object obj)
    {
        if(pointer == values.length)
            resize();
        values[pointer++] = obj;
    }

    public Object pop()
    {
        try
        {
            Object obj = values[--pointer];
            values[pointer] = null;
            return obj;
        }
        catch(ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) { }
        if(pointer < 0)
            pointer = 0;
        throw new EmptyStackException();
    }

    public Object peek()
    {
        try
        {
            return values[pointer - 1];
        }
        catch(ArrayIndexOutOfBoundsException arrayindexoutofboundsexception)
        {
            throw new EmptyStackException();
        }
    }

    private Object values[];
    private int pointer;
}
