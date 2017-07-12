package WorkManager.ThreadAutoAdjust;

public class CalendarQueue {
	private int calendar[];

	private int size;

	private long now;

	private int freeList;

	private int next[];

	private int last[];

	private long time[];

	private Object data[];
	
	private static Boolean debug_CalendarQueue=false;

	CalendarQueue() {
		this(400);
	}

	CalendarQueue(int i) {
		calendar = new int[2048];
		freeList = 0;
		next = new int[i];
		last = new int[i];
		time = new long[i];
		data = new Object[i];
	}

	long resetVirtualTime() {
		long l = now;
		now = 0L;
		return l;
	}

	public void add(Object obj, RequestClass requestclass) {
		long vti = requestclass.getVirtualTimeIncrement(now);
		long l = now + vti;
		if (debug_CalendarQueue) {
			System.out.println("VirtualTimeIncrement = " + vti);
		}
		int i = allocNode();
		data[i] = obj;
		time[i] = l;
		int j = l > (now | 0xffL) ? l > (now | 0xffffL) ? l > (now | 0xffffffL) ? l > (now | 0xffffffffL) ? l > (now | 0xffffffffffL) ? l > (now | 0xffffffffffffL) ? l > (now | 0xffffffffffffffL) ? 1792 + ((int) (l >>> 56) & 0xff)
				: 1536 + ((int) (l >>> 48) & 0xff)
				: 1280 + ((int) (l >>> 40) & 0xff)
				: 1024 + ((int) (l >>> 32) & 0xff)
				: 768 + ((int) (l >>> 24) & 0xff)
				: 512 + ((int) (l >>> 16) & 0xff)
				: 256 + ((int) (l >>> 8) & 0xff)
				: (int) l & 0xff;
		add(j, i);
	}

	private void add(int i, int j) {
		int k = calendar[i];
		if (k == 0) {
			calendar[i] = j;
			last[j] = j;
		} else {
			next[last[k]] = j;
			last[k] = j;
		}
	}

	private int allocNode() {
		int i = ++size;
		int j = freeList;
		if (j != 0) {
			freeList = next[j];
			return j;
		}
		if (i == next.length)
			grow();
		return i;
	}

	public Object pop(Object obj, RequestClass requestclass) {
		if (obj != null)
			return obj;
		else
			return pop();
	}

	public Object pop() {
		if (size == 0)
			return null;
		size--;
		int i = 1;
		int j = (int) now & 0xff;
		do {
			if (calendar[j] != 0)
				break;
			if ((++j & 0xff) == 0) {
				j = 256 * i + ((int) (now >>> 8 * i) & 0xff);
				i++;
			}
		} while (true);
		if (j >= 256) {
			if (j >= 512) {
				if (j >= 768) {
					if (j >= 1024) {
						if (j >= 1280) {
							if (j >= 1536) {
								if (j >= 1792)
									j = promote(1536, 48, j);
								j = promote(1280, 40, j);
							}
							j = promote(1024, 32, j);
						}
						j = promote(768, 24, j);
					}
					j = promote(512, 16, j);
				}
				j = promote(256, 8, j);
			}
			int k = calendar[j];
			calendar[j] = 0;
			int i1 = last[k];
			j = (int) time[k] & 0xff;
			int k1 = j;
			last[k] = k;
			calendar[j] = k;
			do {
				if (k == i1)
					break;
				k = next[k];
				j = (int) time[k] & 0xff;
				add(j, k);
				if (j < k1)
					k1 = j;
			} while (true);
			j = k1;
		}
		int l = calendar[j];
		if (last[l] == l) {
			calendar[j] = 0;
		} else {
			int j1 = next[l];
			calendar[j] = j1;
			last[j1] = last[l];
		}
		next[l] = freeList;
		freeList = l;
		now = time[l];
		Object obj = data[l];
		data[l] = null;
		return obj;
	}

	private int promote(int i, int j, int k) {
		int l = calendar[k];
		calendar[k] = 0;
		int i1 = last[l];
		k = i + ((int) (time[l] >>> j) & 0xff);
		int j1 = k;
		last[l] = l;
		calendar[k] = l;
		do {
			if (l == i1)
				break;
			l = next[l];
			k = i + ((int) (time[l] >>> j) & 0xff);
			add(k, l);
			if (k < j1)
				j1 = k;
		} while (true);
		return j1;
	}

	private void grow() {
		int i = 2 * next.length;
		next = copy(next, new int[i]);
		last = copy(last, new int[i]);
		time = copy(time, new long[i]);
		data = copy(data, new Object[i]);
	}

	private int[] copy(int ai[], int ai1[]) {
		System.arraycopy(ai, 0, ai1, 0, ai.length);
		return ai1;
	}

	private long[] copy(long al[], long al1[]) {
		System.arraycopy(al, 0, al1, 0, al.length);
		return al1;
	}

	private Object[] copy(Object aobj[], Object aobj1[]) {
		System.arraycopy(((Object) (aobj)), 0, ((Object) (aobj1)), 0,
				aobj.length);
		return aobj1;
	}

	public int size() {
		return size;
	}

	private void dump() {
		for (int i = 0; i < calendar.length; i += 256)
			dumpPeriod(i);

	}

	private void dumpPeriod(int i) {
		int j = i + 256;
		System.out.print("{");
		String s = "";
		label0: for (int k = i; k < j; k++) {
			int l = calendar[k];
			if (l == 0)
				continue;
			int i1 = last[l];
			do {
				System.out.print(s);
				System.out.print(data[l]);
				System.out.print("@");
				System.out.print(Long.toHexString(time[l]));
				s = ", ";
				if (l == i1)
					continue label0;
				l = next[l];
			} while (true);
		}
		System.out.print("}\n");
	}
}