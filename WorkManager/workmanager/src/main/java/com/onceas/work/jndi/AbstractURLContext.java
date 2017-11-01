package com.onceas.work.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

public abstract class AbstractURLContext implements Context {

	public AbstractURLContext() {
	}

	protected abstract Context getContext(String s) throws NamingException;

	protected String removeURL(String s) throws InvalidNameException {
		try {
			if (s.indexOf(":") < 0)
				return s;
		} catch (Exception ee)// (MalformedURLException malformedurlexception)
		{
			InvalidNameException invalidnameexception = new InvalidNameException();
			invalidnameexception.setRootCause(ee);// (malformedurlexception);
			throw invalidnameexception;
		}
		// return (new ServerURL(ServerURL.DEFAULT_URL, s)).getFile();
		return "string";
	}

	public final Object addToEnvironment(String s, Object obj)
			throws NamingException {
		throw new OperationNotSupportedException();
	}

	public final void bind(String s, Object obj) throws NamingException {
		getContext(s).bind(removeURL(s), obj);
	}

	public final void bind(Name name, Object obj) throws NamingException {
		getContext(name.toString()).bind(removeURL(name.toString()), obj);
	}

	public final void close() throws NamingException {
		throw new OperationNotSupportedException();
	}

	public final String composeName(String s, String s1) throws NamingException {
		return s;
	}

	public final Name composeName(Name name, Name name1) throws NamingException {
		return (Name) name.clone();
	}

	public final Context createSubcontext(String s) throws NamingException {
		return getContext(s).createSubcontext(removeURL(s));
	}

	public final Context createSubcontext(Name name) throws NamingException {
		return getContext(name.toString()).createSubcontext(
				removeURL(name.toString()));
	}

	public final void destroySubcontext(String s) throws NamingException {
		getContext(s).destroySubcontext(removeURL(s));
	}

	public final void destroySubcontext(Name name) throws NamingException {
		getContext(name.toString()).destroySubcontext(
				removeURL(name.toString()));
	}

	public final Hashtable getEnvironment() throws NamingException {
		throw new OperationNotSupportedException();
	}

	public final String getNameInNamespace() throws NamingException {
		throw new OperationNotSupportedException();
	}

	public final NameParser getNameParser(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	public final NameParser getNameParser(Name name) throws NamingException {
		throw new OperationNotSupportedException();
	}

	public final NamingEnumeration list(String s) throws NamingException {
		return getContext(s).list(removeURL(s));
	}

	public final NamingEnumeration list(Name name) throws NamingException {
		return getContext(name.toString()).list(removeURL(name.toString()));
	}

	public final NamingEnumeration listBindings(String s)
			throws NamingException {
		return getContext(s).listBindings(removeURL(s));
	}

	public final NamingEnumeration listBindings(Name name)
			throws NamingException {
		return getContext(name.toString()).listBindings(
				removeURL(name.toString()));
	}

	public final Object lookup(String s) throws NamingException {
		return getContext(s).lookup(removeURL(s));
	}

	public final Object lookup(Name name) throws NamingException {
		return getContext(name.toString()).lookup(removeURL(name.toString()));
	}

	public final Object lookupLink(String s) throws NamingException {
		return getContext(s).lookupLink(removeURL(s));
	}

	public final Object lookupLink(Name name) throws NamingException {
		return getContext(name.toString()).lookupLink(
				removeURL(name.toString()));
	}

	public final void rebind(String s, Object obj) throws NamingException {
		getContext(s).rebind(removeURL(s), obj);
	}

	public final void rebind(Name name, Object obj) throws NamingException {
		getContext(name.toString()).rebind(removeURL(name.toString()), obj);
	}

	public final Object removeFromEnvironment(String s) throws NamingException {
		throw new OperationNotSupportedException();
	}

	public final void rename(String s, String s1) throws NamingException {
		getContext(s).rename(removeURL(s), s1);
	}

	public final void rename(Name name, Name name1) throws NamingException {
		getContext(name.toString()).rename(removeURL(name.toString()),
				name1.toString());
	}

	public final void unbind(String s) throws NamingException {
		getContext(s).unbind(removeURL(s));
	}

	public final void unbind(Name name) throws NamingException {
		getContext(name.toString()).unbind(removeURL(name.toString()));
	}
}
