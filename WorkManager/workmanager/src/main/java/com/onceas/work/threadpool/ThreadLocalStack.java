package com.onceas.work.threadpool;

import java.util.EmptyStackException;
import java.util.Stack;

public final class ThreadLocalStack {
	private static final class StackInitialValue extends
			ThreadLocalInitialValue {

		protected Object initialValue() {
			return new Stack();
		}

		protected Object resetValue(Object obj) {
			((Stack) obj).clear();
			return obj;
		}

		protected Object childValue(Object obj) {
			Stack stack = (Stack) obj;
			if (!stack.isEmpty()) {
				Stack stack1 = (Stack) initialValue();
				stack1.push(stack.peek());
				return stack1;
			} else {
				return initialValue();
			}
		}

		private StackInitialValue() {
			this(false);
		}

		private StackInitialValue(boolean flag) {
			super(flag);
		}

	}

	public ThreadLocalStack() {
		tlstack = AuditableThreadLocalFactory
				.createThreadLocal(new StackInitialValue());
	}

	public ThreadLocalStack(boolean flag) {
		tlstack = AuditableThreadLocalFactory
				.createThreadLocal(new StackInitialValue(flag));
	}

	public int getSize() {
		return ((Stack) tlstack.get()).size();
	}

	public Object get() {
		return peek();
	}

	public Object get(AuditableThread auditablethread) {
		return peek(auditablethread);
	}

	public void set(Object obj) {
		Stack stack = (Stack) tlstack.get();
		stack.clear();
		stack.push(obj);
	}

	public Object peek() {
		Stack stack = (Stack) tlstack.get();
		if (stack.isEmpty())
			return null;
		else
			return stack.peek();
	}

	public Object peek(AuditableThread auditablethread) {
		Stack stack = (Stack) tlstack.get(auditablethread);
		if (stack == null || stack.isEmpty())
			return null;
		else
			return stack.peek();
	}

	public void push(Object obj) {
		((Stack) tlstack.get()).push(obj);
	}

	public Object pop() {
		try {
			return ((Stack) tlstack.get()).pop();
		} catch (EmptyStackException emptystackexception) {
			throw new AssertionError(emptystackexception);
		}
	}

	public Object popAndPeek() {
		Stack stack;
		try {
			stack = (Stack) tlstack.get();
			stack.pop();
			if (stack.isEmpty())
				return null;
		} catch (EmptyStackException emptystackexception) {
			return null;
		}
		return stack.peek();
	}

	private final AuditableThreadLocal tlstack;
}
