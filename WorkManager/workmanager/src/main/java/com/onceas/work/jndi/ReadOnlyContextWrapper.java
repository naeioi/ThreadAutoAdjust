package com.onceas.work.jndi;

import java.util.Hashtable;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.event.EventContext;
import javax.naming.event.NamingListener;

public final class ReadOnlyContextWrapper implements EventContext {

	public ReadOnlyContextWrapper(Context context1) {
		context = context1;
		if (context1 instanceof EventContext)
			eventContext = (EventContext) context1;
		else
			eventContext = null;
	}

	public String getNameInNamespace() throws NamingException {
		return context.getNameInNamespace();
	}

	public void close() throws NamingException {
		context.close();
	}

	public Object lookup(Name name) throws NamingException {
		return wrapIfContext(context.lookup(name));
	}

	public Object lookup(String s) throws NamingException {
		return wrapIfContext(context.lookup(s));
	}

	public Object lookupLink(Name name) throws NamingException {
		return wrapIfContext(context.lookupLink(name));
	}

	public Object lookupLink(String s) throws NamingException {
		return wrapIfContext(context.lookupLink(s));
	}

	public void bind(Name name, Object obj) throws NamingException {
		throw newOperationNotSupportedException("bind", name);
	}

	public void bind(String s, Object obj) throws NamingException {
		throw newOperationNotSupportedException("bind", s);
	}

	public void rebind(Name name, Object obj) throws NamingException {
		throw newOperationNotSupportedException("rebind", name);
	}

	public void rebind(String s, Object obj) throws NamingException {
		throw newOperationNotSupportedException("rebind", s);
	}

	public void unbind(Name name) throws NamingException {
		throw newOperationNotSupportedException("unbind", name);
	}

	public void unbind(String s) throws NamingException {
		throw newOperationNotSupportedException("unbind", s);
	}

	public void rename(Name name, Name name1) throws NamingException {
		throw newOperationNotSupportedException("rename", name);
	}

	public void rename(String s, String s1) throws NamingException {
		throw newOperationNotSupportedException("rename", s);
	}

	public NamingEnumeration list(Name name) throws NamingException {
		return context.list(name);
	}

	public NamingEnumeration list(String s) throws NamingException {
		return context.list(s);
	}

	public NamingEnumeration listBindings(Name name) throws NamingException {
		return context.listBindings(name);
	}

	public NamingEnumeration listBindings(String s) throws NamingException {
		return context.listBindings(s);
	}

	public NameParser getNameParser(Name name) throws NamingException {
		return context.getNameParser(name);
	}

	public NameParser getNameParser(String s) throws NamingException {
		return context.getNameParser(s);
	}

	public Name composeName(Name name, Name name1) throws NamingException {
		return context.composeName(name, name1);
	}

	public String composeName(String s, String s1) throws NamingException {
		return context.composeName(s, s1);
	}

	public Context createSubcontext(Name name) throws NamingException {
		throw newOperationNotSupportedException("createSubcontext", name);
	}

	public Context createSubcontext(String s) throws NamingException {
		throw newOperationNotSupportedException("createSubcontext", s);
	}

	public void destroySubcontext(Name name) throws NamingException {
		throw newOperationNotSupportedException("destroySubcontext", name);
	}

	public void destroySubcontext(String s) throws NamingException {
		throw newOperationNotSupportedException("destroySubcontext", s);
	}

	public Hashtable getEnvironment() throws NamingException {
		return context.getEnvironment();
	}

	public Object addToEnvironment(String s, Object obj) throws NamingException {
		return context.addToEnvironment(s, obj);
	}

	public Object removeFromEnvironment(String s) throws NamingException {
		return context.removeFromEnvironment(s);
	}

	public String toString() {
		return context.toString();
	}

	private Object wrapIfContext(Object obj) {
		if (obj instanceof Context)
			return new ReadOnlyContextWrapper((Context) obj);
		else
			return obj;
	}

	private OperationNotSupportedException newOperationNotSupportedException(
			String s, Name name) {
		OperationNotSupportedException operationnotsupportedexception = new OperationNotSupportedException(
				s + " not allowed in a ReadOnlyContext");
		operationnotsupportedexception.setRemainingName(name);
		return operationnotsupportedexception;
	}

	private OperationNotSupportedException newOperationNotSupportedException(
			String s, String s1) {
		CompositeName compositename = null;
		try {
			compositename = new CompositeName(s1);
		} catch (InvalidNameException invalidnameexception) {
		}
		return newOperationNotSupportedException(s, ((Name) (compositename)));
	}

	public void addNamingListener(Name name, int i,
			NamingListener naminglistener) throws NamingException {
		addNamingListener(name.toString(), i, naminglistener);
	}

	public void addNamingListener(String s, int i, NamingListener naminglistener)
			throws NamingException {
		if (eventContext != null)
			eventContext.addNamingListener(s, i, naminglistener);
	}

	public void removeNamingListener(NamingListener naminglistener)
			throws NamingException {
		if (eventContext != null)
			eventContext.removeNamingListener(naminglistener);
	}

	public boolean targetMustExist() throws NamingException {
		return true;
	}

	private final Context context;

	private final EventContext eventContext;
}
