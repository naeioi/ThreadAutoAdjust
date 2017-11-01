// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ConcurrentHashMap.java

package com.onceas.util.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ConcurrentHashMap extends AbstractMap
    implements Map, Cloneable, Serializable
{
    private final class EntrySet extends AbstractSet
    {

        public Iterator iterator()
        {
            return new EntryIterator();
        }

        public boolean contains(Object obj)
        {
            if(!(obj instanceof Map.Entry))
            {
                return false;
            } else
            {
                Map.Entry entry = (Map.Entry)obj;
                Entry entry1 = getEntry(entry.getKey());
                return entry1 != null && entry1.equals(entry);
            }
        }

        public boolean remove(Object obj)
        {
            return removeMapping(obj) != null;
        }

        public int size()
        {
            return ConcurrentHashMap.this.size;
        }

        public void clear()
        {
            ConcurrentHashMap.this.clear();
        }

        private EntrySet()
        {
            super();
        }

    }

    private final class Values extends AbstractCollection
    {

        public Iterator iterator()
        {
            return new ValueIterator();
        }

        public int size()
        {
            return ConcurrentHashMap.this.size;
        }

        public boolean contains(Object obj)
        {
            return containsValue(obj);
        }

        public void clear()
        {
            ConcurrentHashMap.this.clear();
        }

        private Values()
        {
            super();
        }

    }

    private final class KeySet extends AbstractSet
    {

        public Iterator iterator()
        {
            return new KeyIterator();
        }

        public int size()
        {
            return ConcurrentHashMap.this.size;
        }

        public boolean contains(Object obj)
        {
            return containsKey(obj);
        }

        public boolean remove(Object obj)
        {
            return removeEntryForKey(obj) != null;
        }

        public void clear()
        {
            ConcurrentHashMap.this.clear();
        }

        private KeySet()
        {
            super();
        }

    }

    private final class EntryIterator extends HashIterator
    {

        public Object next()
        {
            return nextEntry();
        }

        private EntryIterator()
        {
            super();
        }

    }

    private final class KeyIterator extends HashIterator
    {

        public Object next()
        {
            return nextEntry().getKey();
        }

        private KeyIterator()
        {
            super();
        }

    }

    private final class ValueIterator extends HashIterator
    {

        public Object next()
        {
            return nextEntry().value;
        }

        private ValueIterator()
        {
            super();
        }

    }

    private abstract class HashIterator
        implements Iterator
    {

        public final boolean hasNext()
        {
            return next != null;
        }

        final Entry nextEntry()
        {
            Entry entry = next;
            if(entry == null)
                throw new NoSuchElementException();
            Entry entry1 = entry.next;
            Entry aentry[] = table;
            int i;
            for(i = index; entry1 == null && i > 0; entry1 = aentry[--i]);
            index = i;
            next = entry1;
            return current = entry;
        }

        public final void remove()
        {
            if(current == null)
            {
                throw new IllegalStateException();
            } else
            {
                Object obj = current.key;
                current = null;
                removeEntryForKey(obj);
                return;
            }
        }

        final Entry table[];
        Entry next;
        int index;
        Entry current;

        HashIterator()
        {
            super();
            table = ConcurrentHashMap.this.table;
            if(size == 0)
                return;
            Entry aentry[] = table;
            int i = aentry.length - 1;
            Entry entry;
            for(entry = aentry[i]; entry == null && i > 0; entry = aentry[--i]);
            index = i;
            next = entry;
        }
    }

    protected static class Entry
        implements Map.Entry
    {

        public Object getKey()
        {
            return ConcurrentHashMap.unmaskNull(key);
        }

        public Object getValue()
        {
            return value;
        }

        public Object setValue(Object obj)
        {
            Object obj1 = value;
            value = obj;
            return obj1;
        }

        public boolean equals(Object obj)
        {
            if(!(obj instanceof Map.Entry))
                return false;
            Map.Entry entry = (Map.Entry)obj;
            Object obj1 = getKey();
            Object obj2 = entry.getKey();
            if(obj1 == obj2 || obj1 != null && obj1.equals(obj2))
            {
                Object obj3 = getValue();
                Object obj4 = entry.getValue();
                if(obj3 == obj4 || obj3 != null && obj3.equals(obj4))
                    return true;
            }
            return false;
        }

        public int hashCode()
        {
            return (key != ConcurrentHashMap.NULL_KEY ? key.hashCode() : 0) ^ (value != null ? value.hashCode() : 0);
        }

        public String toString()
        {
            return getKey() + "=" + getValue();
        }

        protected Object clone()
        {
            return new Entry(hash, key, value, null);
        }

        final Object key;
        Object value;
        final int hash;
        Entry next;

        protected Entry(int i, Object obj, Object obj1, Entry entry)
        {
            value = obj1;
            next = entry;
            key = obj;
            hash = i;
        }
    }


    public ConcurrentHashMap(int i, float f)
    {
        entrySet = null;
        keySet = null;
        values = null;
        if(i < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + i);
        if(i > 0x40000000)
            i = 0x40000000;
        if(f <= 0.0F || f > 1.0F)
            throw new IllegalArgumentException("Illegal load factor: " + f);
        int j;
        for(j = 1; j < i; j <<= 1);
        loadFactor = f;
        threshold = (int)((float)j * f);
        table = new Entry[j];
    }

    public ConcurrentHashMap(int i)
    {
        this(i, 0.75F);
    }

    public ConcurrentHashMap()
    {
        this(16, 0.75F);
    }

    public ConcurrentHashMap(Map map)
    {
        this(Math.max((int)((float)map.size() / 0.75F) + 1, 16), 0.75F);
        putAll(map);
    }

    private static Object maskNull(Object obj)
    {
        return obj != null ? obj : NULL_KEY;
    }

    private static Object unmaskNull(Object obj)
    {
        return obj != NULL_KEY ? obj : null;
    }

    private static int hash(Object obj)
    {
        int i = obj.hashCode();
        return i - (i << 7);
    }

    private static boolean eq(Object obj, Object obj1)
    {
        return obj == obj1 || obj.equals(obj1);
    }

    public final int size()
    {
        return size;
    }

    public final boolean isEmpty()
    {
        return size == 0;
    }

    public Object get(Object obj)
    {
        Entry entry = getEntry(obj);
        return entry != null ? entry.value : null;
    }

    public final boolean containsKey(Object obj)
    {
        return getEntry(obj) != null;
    }

    protected Entry getEntry(Object obj)
    {
        Object obj1 = maskNull(obj);
        int i = hash(obj1);
        Entry aentry[] = table;
        for(Entry entry = aentry[i & aentry.length - 1]; entry != null; entry = entry.next)
            if(entry.hash == i && eq(obj1, entry.key))
                return entry;

        return null;
    }

    public Object put(Object obj, Object obj1)
    {
        Object obj2 = maskNull(obj);
        int i = hash(obj2);
        ConcurrentHashMap concurrenthashmap = this;
         synchronized(this)
         {
             int j;
             Entry entry;
             j = i & table.length - 1;
             entry = table[j];
             while(entry!=null)
             {
                   if(entry.hash == i && eq(obj2, entry.key))
                   {
                       Object obj3 = entry.value;
                       entry.value = obj1;
                       return obj3;
                   }
                   entry = entry.next;
             }
             table[j] = createEntry(i, obj2, obj1, table[j]);
             if(size++ >= threshold)
                 resize(2 * table.length);
             return null;
         }

/*        JVM INSTR monitorenter ;
        int j;
        Entry entry;
        j = i & table.length - 1;
        entry = table[j];
_L1:
        if(entry == null)
            break MISSING_BLOCK_LABEL_93;
        if(entry.hash == i && eq(obj2, entry.key))
        {
            Object obj3 = entry.value;
            entry.value = obj1;
            return obj3;
        }
        entry = entry.next;
          goto _L1
        table[j] = createEntry(i, obj2, obj1, table[j]);
        if(size++ >= threshold)
            resize(2 * table.length);
        concurrenthashmap;
        JVM INSTR monitorexit ;
          goto _L2
        Exception exception;
        exception;
        throw exception;
_L2:
        return null;*/
    }

    public Object putIfAbsent(Object obj, Object obj1)
    {
        Object obj2 = maskNull(obj);
        int i = hash(obj2);
        ConcurrentHashMap concurrenthashmap = this;
        synchronized(this)
        {
            int j;
            Entry entry;
            j = i & table.length - 1;
            entry = table[j];
            while(entry!=null)
            {
                if(entry.hash == i && eq(obj2, entry.key))
                    return entry.value;
                entry = entry.next;
            }
            table[j] = createEntry(i, obj2, obj1, table[j]);
            if(size++ >= threshold)
                resize(2 * table.length);
            return null;
        }
      /*  JVM INSTR monitorenter ;
        int j;
        Entry entry;
        j = i & table.length - 1;
        entry = table[j];
_L1:
        if(entry == null)
            break MISSING_BLOCK_LABEL_83;
        if(entry.hash == i && eq(obj2, entry.key))
            return entry.value;
        entry = entry.next;
          goto _L1
        table[j] = createEntry(i, obj2, obj1, table[j]);
        if(size++ >= threshold)
            resize(2 * table.length);
        concurrenthashmap;
        JVM INSTR monitorexit ;
          goto _L2
        Exception exception;
        exception;
        throw exception;
_L2:
        return null;*/
    }

    private void resize(int i)
    {
        Entry aentry[] = table;
        int j = aentry.length;
        if(size < threshold || j > i)
            return;
        Entry aentry1[] = new Entry[i];
        int k = i - 1;
        for(int l = j; l-- > 0;)
        {
            Entry entry = aentry[l];
            while(entry != null)
            {
                Entry entry1 = (Entry)entry.clone();
                int i1 = entry1.hash & k;
                entry1.next = aentry1[i1];
                aentry1[i1] = entry1;
                entry = entry.next;
            }
        }

        table = aentry1;
        threshold = (int)((float)i * loadFactor);
    }

    public final synchronized void putAll(Map map)
    {
        int i = map.size();
        if(i == 0)
            return;
        if(i >= threshold)
        {
            i = (int)((float)i / loadFactor + 1.0F);
            if(i > 0x40000000)
                i = 0x40000000;
            int j;
            for(j = table.length; j < i; j <<= 1);
            resize(j);
        }
        Map.Entry entry;
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); put(entry.getKey(), entry.getValue()))
            entry = (Map.Entry)iterator.next();

    }

    public Object remove(Object obj)
    {
        Entry entry = removeEntryForKey(obj);
        return entry != null ? entry.value : entry;
    }

    /**
     * 通过比较Key值删除entry
     * @param obj Object
     * @return Entry
     */
    private Entry removeEntryForKey(Object obj)
    {
        Object obj1 = maskNull(obj);
        int i = hash(obj1);
        ConcurrentHashMap concurrenthashmap = this;
        synchronized(this)
        {
            int j;
            Entry entry;
            j = i & table.length - 1;
            entry = table[j];
            if(entry == null)
                return null;
             Entry entry1;
            if(entry.hash != i || !eq(obj1, entry.key))
            {

                entry1 = entry;
                entry = entry.next;
            }
            else
            {
                size--;
                table[j] = entry.next;
                return entry;

            }
            while(entry!=null)
            {
                if(entry.hash != i || !eq(obj1, entry.key))
                {
                    entry1 = entry;
                    entry = entry.next;

                }
                else
                {
                    size--;
                    entry1.next = entry.next;
                    return entry;

                }

            }
            return null;


        }
/*        JVM INSTR monitorenter ;
        int j;
        Entry entry;
        j = i & table.length - 1;
        entry = table[j];
        if(entry == null)
            return null;
        if(entry.hash != i || !eq(obj1, entry.key)) goto _L2; else goto _L1
_L1:
        size--;
        table[j] = entry.next;
        entry;
        concurrenthashmap;
        JVM INSTR monitorexit ;
        return;
_L2:
        Entry entry1;
        entry1 = entry;
        entry = entry.next;
_L5:
        if(entry == null)
            break MISSING_BLOCK_LABEL_171;
        if(entry.hash != i || !eq(obj1, entry.key)) goto _L4; else goto _L3
_L3:
        size--;
        entry1.next = entry.next;
        entry;
        concurrenthashmap;
        JVM INSTR monitorexit ;
        return;
_L4:
        entry1 = entry;
        entry = entry.next;
          goto _L5
        concurrenthashmap;
        JVM INSTR monitorexit ;
          goto _L6
        Exception exception;
        exception;
        throw exception;
_L6:
        return null;*/
    }

    private Entry removeMapping(Object obj)
    {
        Map.Entry entry;
        Object obj1;
        if(!(obj instanceof Map.Entry))
            return null;
        entry = (Map.Entry)obj;
        obj1 = maskNull(entry.getKey());
        int i = hash(obj1);
        ConcurrentHashMap concurrenthashmap = this;
         synchronized(this)
         {
             int j;
             Entry entry1;
             j = i & table.length - 1;
             entry1 = table[j];
             if(entry1 == null)
                 return null;
              Entry entry2;
             if(entry1.hash != i || !entry1.equals(entry))
             {

                 entry2 = entry1;
                 entry1 = entry1.next;
             }
             else
             {
                 size--;
                 table[j] = entry1.next;
                 return entry1;
             }
             while(entry!=null)
             {
                  if(entry1.hash != i || !entry1.equals(entry))
                  {
                      entry2 = entry1;
                      entry1 = entry1.next;
                  }
                  else
                  {
                      size--;
                      entry2.next = entry1.next;
                      return entry1;

                  }

             }
             return null;


         }
/*        JVM INSTR monitorenter ;
        int j;
        Entry entry1;
        j = i & table.length - 1;
        entry1 = table[j];
        if(entry1 == null)
            return null;
        if(entry1.hash != i || !entry1.equals(entry)) goto _L2; else goto _L1
_L1:
        size--;
        table[j] = entry1.next;
        entry1;
        concurrenthashmap;
        JVM INSTR monitorexit ;
        return;
_L2:
        Entry entry2;
        entry2 = entry1;
        entry1 = entry1.next;
_L5:
        if(entry1 == null)
            break MISSING_BLOCK_LABEL_188;
        if(entry1.hash != i || !entry1.equals(entry)) goto _L4; else goto _L3
_L3:
        size--;
        entry2.next = entry1.next;
        entry1;
        concurrenthashmap;
        JVM INSTR monitorexit ;
        return;
_L4:
        entry2 = entry1;
        entry1 = entry1.next;
          goto _L5
        concurrenthashmap;
        JVM INSTR monitorexit ;
          goto _L6
        Exception exception;
        exception;
        throw exception;
_L6:
        return null;*/
    }

    public synchronized void clear()
    {
        table = new Entry[table.length];
        size = 0;
    }

    public final boolean containsValue(Object obj)
    {
        if(obj == null)
            return containsNullValue();
        Entry aentry[] = table;
        for(int i = 0; i < aentry.length; i++)
        {
            for(Entry entry = aentry[i]; entry != null; entry = entry.next)
                if(obj.equals(entry.value))
                    return true;

        }

        return false;
    }

    private boolean containsNullValue()
    {
        Entry aentry[] = table;
        for(int i = 0; i < aentry.length; i++)
        {
            for(Entry entry = aentry[i]; entry != null; entry = entry.next)
                if(entry.value == null)
                    return true;

        }

        return false;
    }

    public final Object clone()
    {
        return new ConcurrentHashMap(this);
    }

    protected Entry createEntry(int i, Object obj, Object obj1, Entry entry)
    {
        return new Entry(i, obj, obj1, entry);
    }

    public final String toPrettyString()
    {
        return toPrettyString(0);
    }

    public final String toPrettyString(int i)
    {
        UnsyncStringBuffer unsyncstringbuffer = new UnsyncStringBuffer();
        for(int j = 0; j < i; j++)
            unsyncstringbuffer.append("  ");

        UnsyncStringBuffer unsyncstringbuffer1 = new UnsyncStringBuffer();
        unsyncstringbuffer1.append(unsyncstringbuffer.toString() + "Size: [" + size() + ", " + entrySet().size() + "] {\n");
        Set set = entrySet();
        Iterator iterator = set.iterator();
        Object obj = null;
        while(iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            if(entry.getValue() instanceof ConcurrentHashMap)
                unsyncstringbuffer1.append(unsyncstringbuffer.toString() + "  " + entry.getKey() + "=" + ((ConcurrentHashMap)entry.getValue()).toPrettyString(i + 1) + "\n");
            else
                unsyncstringbuffer1.append(unsyncstringbuffer.toString() + "  " + entry.getKey() + "=" + entry.getValue() + "\n");
        }
        unsyncstringbuffer1.append(unsyncstringbuffer.toString() + "}\n");
        return unsyncstringbuffer1.toString();
    }

    public final Set keySet()
    {
        Set set = keySet;
        return set == null ? (keySet = new KeySet()) : set;
    }

    public final Collection values()
    {
        Collection collection = values;
        return collection == null ? (values = new Values()) : collection;
    }

    public final Set entrySet()
    {
        Set set = entrySet;
        return set == null ? (entrySet = new EntrySet()) : set;
    }

    private void writeObject(ObjectOutputStream objectoutputstream)
        throws IOException
    {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeInt(table.length);
        objectoutputstream.writeInt(size);
        Map.Entry entry;
        for(Iterator iterator = entrySet().iterator(); iterator.hasNext(); objectoutputstream.writeObject(entry.getValue()))
        {
            entry = (Map.Entry)iterator.next();
            objectoutputstream.writeObject(entry.getKey());
        }

    }

    private void readObject(ObjectInputStream objectinputstream)
        throws IOException, ClassNotFoundException
    {
        objectinputstream.defaultReadObject();
        int i = objectinputstream.readInt();
        table = new Entry[i];
        int j = objectinputstream.readInt();
        for(int k = 0; k < j; k++)
        {
            Object obj = objectinputstream.readObject();
            Object obj1 = objectinputstream.readObject();
            put(obj, obj1);
        }

    }

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 0x40000000;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private transient Entry table[];
    private transient int size;
    private int threshold;
    private final float loadFactor;
    private static final Object NULL_KEY = new Object();
    private transient Set entrySet;
    private transient Set keySet;
    private transient Collection values;
    private static final long serialVersionUID = 0xa6736193169fdfaeL;







}
