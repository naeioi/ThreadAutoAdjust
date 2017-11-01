package com.onceas.work.threadpool;

public final class FinalThreadLocal implements AuditableThreadLocal {
	static final class FinalThreadStorage implements ThreadStorage {

		/**
		 * 添加新的finallocal
		 * 
		 * @param finalthreadlocal
		 *            FinalThreadLocal
		 * @return int
		 */
		public static int newSlot(FinalThreadLocal finalthreadlocal) {
			if (FinalThreadLocal.finalized)
				throw new AssertionError(
						"A FinalThreadLocal was allocated after thread creation.");
			Class class1 = FinalThreadLocal.class$weblogic$kernel$FinalThreadLocal$FinalThreadStorage != null ? FinalThreadLocal.class$weblogic$kernel$FinalThreadLocal$FinalThreadStorage
					: (FinalThreadLocal.class$weblogic$kernel$FinalThreadLocal$FinalThreadStorage = FinalThreadLocal
							._mthclass$("weblogic.kernel.FinalThreadLocal$FinalThreadStorage"));
			synchronized (varList) {
				int i = varList.length;
				FinalThreadLocal afinalthreadlocal[] = new FinalThreadLocal[i + 1];
				System.arraycopy(varList, 0, afinalthreadlocal, 0, i);
				afinalthreadlocal[i] = finalthreadlocal;
				varList = afinalthreadlocal;
				return i;
			}

			/*
			 * JVM INSTR monitorenter ; int i = varList.length; FinalThreadLocal
			 * afinalthreadlocal[] = new FinalThreadLocal[i + 1];
			 * System.arraycopy(varList, 0, afinalthreadlocal, 0, i);
			 * afinalthreadlocal[i] = finalthreadlocal; varList =
			 * afinalthreadlocal; return i; Exception exception; exception;
			 * throw exception;
			 */
		}

		public void set(int i, Object obj) {
			storage[i] = obj;
		}

		public Object get(int i) {
			return storage[i];
		}

		public final void reset() {
			Object aobj[] = storage;
			int i = aobj.length;
			for (int j = 0; j < i; j++) {
				Object obj = aobj[j];
				aobj[j] = varList[j].initial.resetValue(obj);
			}

		}

		private static FinalThreadLocal varList[] = new FinalThreadLocal[0];// 支持并发，要用到锁

		private final Object storage[]; // local存贮

		private final int NUM_SLOTS;// count

		public FinalThreadStorage() {
			FinalThreadLocal.finalized = true;
			NUM_SLOTS = varList.length;
			storage = new Object[NUM_SLOTS];
			for (int i = 0; i < NUM_SLOTS; i++)
				storage[i] = varList[i].initial.initialValue();

		}

		protected FinalThreadStorage(FinalThreadStorage finalthreadstorage) {
			NUM_SLOTS = varList.length;
			storage = new Object[NUM_SLOTS];
			for (int i = 0; i < NUM_SLOTS; i++)
				if (finalthreadstorage != null)
					storage[i] = varList[i].initial
							.childValue(finalthreadstorage.storage[i]);
				else
					storage[i] = varList[i].initial.initialValue();

		}
	}

	FinalThreadLocal() {
		index = FinalThreadStorage.newSlot(this);
		initial = new ThreadLocalInitialValue();
	}

	FinalThreadLocal(ThreadLocalInitialValue threadlocalinitialvalue) {
		index = FinalThreadStorage.newSlot(this);
		initial = threadlocalinitialvalue;
	}

	public Object get() {
		try {
			return ((AuditableThread) Thread.currentThread()).finalThreadStorage
					.get(index);
		} catch (ClassCastException classcastexception) {
			return get((ThreadStorage) threadLocals.get());
		}
	}

	public Object get(AuditableThread auditablethread) {
		if (auditablethread == null)
			return null;
		else
			return auditablethread.finalThreadStorage.get(index);
	}

	private Object get(ThreadStorage threadstorage) {
		if (threadstorage == null)
			return null;
		else
			return threadstorage.get(index);
	}

	public void set(Object obj) {
		try {
			((AuditableThread) Thread.currentThread()).finalThreadStorage.set(
					index, obj);
		} catch (ClassCastException classcastexception) {
			set((ThreadStorage) threadLocals.get(), obj);
		}
	}

	private void set(ThreadStorage threadstorage, Object obj) {
		if (threadstorage == null) {
			return;
		} else {
			threadstorage.set(index, obj);
			return;
		}
	}

	static boolean isFinalized() {
		return finalized;
	}

	static final void resetJavaThreadStorage() {
		threadLocals.set(null);
	}

	static Class _mthclass$(String s) {
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException classnotfoundexception) {
			// throw (new
			// NoClassDefFoundError()).initCause(classnotfoundexception);
			// throw(new Exception());

		}
		return null; // 这一部分是我自己加的，要改
	}

	private static volatile boolean finalized = false;

	private final int index;

	private final ThreadLocalInitialValue initial;

	static Class class$weblogic$kernel$FinalThreadLocal$FinalThreadStorage; /*
																			 * synthetic
																			 * field
																			 */

	private static final InheritableThreadLocal threadLocals = new InheritableThreadLocal() {
		protected Object initialValue() {
			return new FinalThreadStorage();
		}

		protected Object childValue(Object obj) {
			try {
				return new FinalThreadStorage(((AuditableThread) Thread
						.currentThread()).finalThreadStorage);
			} catch (ClassCastException classcastexception) {
			}
			if (obj != null)
				return new FinalThreadStorage((FinalThreadStorage) obj);
			else
				return null;
		}

	};

}
