package com.onceas.work.threadpool;

import java.util.Arrays;

public class ResettableThreadLocal implements AuditableThreadLocal {
	static final class ThreadStorage {
		public static int newSlot(ResettableThreadLocal resettablethreadlocal) {
			synchronized (varList) {
				int i = varList.length;
				ResettableThreadLocal aresettablethreadlocal[] = new ResettableThreadLocal[i + 1];
				System.arraycopy(varList, 0, aresettablethreadlocal, 0, i);
				aresettablethreadlocal[i] = resettablethreadlocal;
				varList = aresettablethreadlocal;
				return i;
			}
		}

		public void set(int i, Object obj) {
			if (i >= storage.length)
				expand(i + 1);
			storage[i] = obj;
		}

		public Object get(int i) {
			if (i >= storage.length)
				expand(i + 1);
			Object obj = storage[i];
			if (obj == UNINITIALIZED) {
				obj = varList[i].initialValue();
				set(i, obj);
			}
			return obj;
		}

		public ThreadStorage createChildCopy() {
			ThreadStorage threadstorage = new ThreadStorage();
			Object aobj[] = threadstorage.storage;
			Object aobj1[] = storage;
			ResettableThreadLocal aresettablethreadlocal[] = varList;
			int i = aobj1.length;
			for (int j = 0; j < i; j++) {
				Object obj = aobj1[j];
				if (obj != UNINITIALIZED)
					aobj[j] = aresettablethreadlocal[j].childValue(obj);
			}

			return threadstorage;
		}

		final void reset() {
			Object aobj[] = storage;
			ResettableThreadLocal aresettablethreadlocal[] = varList;
			int i = aobj.length;
			for (int j = 0; j < i; j++) {
				Object obj = aobj[j];
				if (obj != UNINITIALIZED)
					aobj[j] = aresettablethreadlocal[j].resetValue(obj);
			}
		}

		private void expand(int i) {
			int j = storage.length;
			Object aobj[] = new Object[i];
			System.arraycopy(((Object) (storage)), 0, ((Object) (aobj)), 0, j);
			for (int k = j; k < i; k++)
				aobj[k] = UNINITIALIZED;

			storage = aobj;
		}

		public static final Object UNINITIALIZED = new Object();

		private static ResettableThreadLocal varList[] = new ResettableThreadLocal[0];

		private Object storage[];

		public ThreadStorage() {
			storage = new Object[varList.length];
			Arrays.fill(storage, UNINITIALIZED);
		}
	}

	public ResettableThreadLocal() {
		this(new ThreadLocalInitialValue());
	}

	public ResettableThreadLocal(ThreadLocalInitialValue threadlocalinitialvalue) {
		index = ThreadStorage.newSlot(this);
		initial = threadlocalinitialvalue;
	}

	public ResettableThreadLocal(boolean flag) {
		this(new ThreadLocalInitialValue(flag));
	}

	public Object get() {
		return currentStorage().get(index);
	}

	public Object get(AuditableThread auditablethread) {
		if (auditablethread == null)
			return null;
		ThreadStorage threadstorage = auditablethread.getThreadStorage();
		if (threadstorage == null)
			return null;
		else
			return threadstorage.get(index);
	}

	public void set(Object obj) {
		currentStorage().set(index, obj);
	}

	protected Object initialValue() {
		return initial.initialValue();
	}

	protected Object childValue(Object obj) {
		return initial.childValue(obj);
	}

	protected Object resetValue(Object obj) {
		return initial.resetValue(obj);
	}

	private final ThreadStorage currentStorage() {
		Thread thread = Thread.currentThread();
		ThreadStorage threadstorage = null;
		if (thread instanceof AuditableThread) {
			threadstorage = ((AuditableThread) thread).getThreadStorage();
			if (threadstorage == null) {
				threadstorage = (ThreadStorage) threadLocals.get();
				((AuditableThread) thread).setThreadStorage(threadstorage);
			}
		} else {
			threadstorage = (ThreadStorage) threadLocals.get();
		}
		return threadstorage;
	}

	private final ThreadLocalInitialValue initial;

	private final int index;

	private static final InheritableThreadLocal threadLocals = new InheritableThreadLocal() {
		protected Object initialValue() {
			return new ThreadStorage();
		}

		protected Object childValue(Object obj) {
			return ((ThreadStorage) obj).createChildCopy();
		}

	};
}
