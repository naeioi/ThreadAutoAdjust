package com.onceas.util.collection;

public class UnsyncCircularQueue {
	public static class FullQueueException extends RuntimeException {

		public String getMessage() {
			return "Queue exceed maximum capacity of: '" + capacity + "' elements";
		}

		private static final long serialVersionUID = 0x3cc68c1770eefd0L;
		int capacity;

		private FullQueueException(int i) {
			capacity = i;
		}

	}

	public UnsyncCircularQueue() {
		this(256);
	}

	public UnsyncCircularQueue(int i) {
		this(i, 0x10000);
	}

	public UnsyncCircularQueue(int i, int j) {
		size = 0;
		producerIndex = 0;
		consumerIndex = 0;
		capacity = 1;
		maxCapacity = 1;
		if (i > j)
			throw new IllegalArgumentException("Capacity greater than maximum");
		if (j > 0x40000000)
			throw new IllegalArgumentException("Capacity: '" + j + "' greater than maximum: '" + 0x40000000 + "'");
		for (capacity = 1; capacity < i; capacity <<= 1)
			;
		for (maxCapacity = 1; maxCapacity < j; maxCapacity <<= 1)
			;
		bitmask = capacity - 1;
		q = new Object[capacity];
	}

	private void expandQueue() {
		if (capacity == maxCapacity)
			throw new FullQueueException(maxCapacity);
		int i = capacity;
		Object aobj[] = q;
		capacity += capacity;
		bitmask = capacity - 1;
		q = new Object[capacity];
		System.arraycopy(((Object) (aobj)), consumerIndex, ((Object) (q)), 0, i - consumerIndex);
		if (consumerIndex != 0)
			System.arraycopy(((Object) (aobj)), 0, ((Object) (q)), i - consumerIndex, consumerIndex);
		consumerIndex = 0;
		producerIndex = size;
	}

	public final void put(Object obj) {
		if (size == capacity)
			expandQueue();
		size++;
		q[producerIndex] = obj;
		producerIndex = producerIndex + 1 & bitmask;
	}

	public final Object get() {
		if (size == 0) {
			return null;
		} else {
			size--;
			Object obj = q[consumerIndex];
			q[consumerIndex] = null;
			consumerIndex = consumerIndex + 1 & bitmask;
			return obj;
		}
	}

	public boolean empty() {
		return size == 0;
	}

	public int size() {
		return size;
	}

	public int capacity() {
		return capacity;
	}

	public Object peek() {
		if (size == 0)
			return null;
		else
			return q[consumerIndex];
	}

	public String toString() {
		StringBuffer stringbuffer = new StringBuffer(
				super.toString() + " - capacity: '" + capacity() + "' size: '" + size() + "'");
		if (size > 0) {
			stringbuffer.append(" elements:");
			for (int i = 0; i < size; i++) {
				stringbuffer.append('\n');
				stringbuffer.append('\t');
				stringbuffer.append(q[consumerIndex + i & bitmask].toString());
			}

		}
		return stringbuffer.toString();
	}

	private static final int DEFAULT_CAPACITY = 256;
	private static final int DEFAULT_MAX_CAPACITY = 0x10000;
	public static final int MAX_CAPACITY = 0x40000000;
	private int size;
	private int producerIndex;
	private int consumerIndex;
	private int capacity;
	private int maxCapacity;
	private int bitmask;
	private Object q[];
}
