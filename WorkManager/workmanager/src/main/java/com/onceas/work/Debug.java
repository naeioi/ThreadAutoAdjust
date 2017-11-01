package com.onceas.work;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public final class Debug {
	static final class StackTrace {

		public Location location(int i) throws ArrayIndexOutOfBoundsException {
			return stack[i];
		}

		public void dump(PrintStream printstream, String s) {
			printstream.println(s);
			for (int i = 0; i < stack.length; i++) {
				printstream.flush();
				printstream.println("  " + location(i).dump());
			}

		}

		private final Location stack[];

		StackTrace() {
			StackTraceElement astacktraceelement[] = (new Exception())
					.getStackTrace();
			stack = new Location[astacktraceelement.length - 2];
			for (int i = 2; i < astacktraceelement.length; i++)
				stack[i - 2] = new Location(astacktraceelement[i]);

		}
	}

	static final class Location {

		public String tag(int i) {
			return "[" + clazz
					+ (Debug.verboseMethods ? "." + method + "()" : "")
					+ (Debug.showLineNumbers ? " - " + linenum : "") + "] "
					+ (i == 0 ? "" : "(" + i + ")") + ": ";
		}

		public String dump() {
			return fullname() + '(' + sourcefile + ':' + linenum + ')';
		}

		public String caller() {
			return fullname() + "(), line " + linenum;
		}

		private String fullname() {
			return fullClass + '.' + method;
		}

		private static final String UNKNOWN = "<unknown>";

		private final String pkg;

		private final String clazz;

		private final String method;

		private final String linenum;

		private final String fullClass;

		private final String sourcefile;

		Location(StackTraceElement stacktraceelement) {
			fullClass = stacktraceelement.getClassName();
			method = stacktraceelement.getMethodName();
			int i = fullClass.lastIndexOf(".");
			if (i == -1) {
				pkg = "<unknown>";
				clazz = fullClass;
			} else {
				pkg = fullClass.substring(0, i);
				clazz = fullClass.substring(i + 1);
			}
			sourcefile = stacktraceelement.getFileName();
			int j = stacktraceelement.getLineNumber();
			linenum = j <= 0 ? "<unknown>" : String.valueOf(j);
		}
	}

	private Debug() {
	}

	public static void assertion(boolean flag) {
		if (!flag)
			throw new AssertionError("Assertion violated");
		else
			return;
	}

	public static void assertion(boolean flag, String s) {
		if (!flag)
			throw new AssertionError(s);
		else
			return;
	}

	public static void say(String s) {
		out.println((new StackTrace()).location(0).tag(0) + s);
	}

	public static void timestamp(String s) {
		out.println("[" + System.currentTimeMillis() + "] " + s);
	}

	public static void here() {
		out.println("*** " + (new StackTrace()).location(0).dump() + " ***");
	}

	public static void stackdump() {
		stackdump("Stack dump:");
	}

	public static void stackdump(String s) {
		(new StackTrace()).dump(out, s);
	}

	public static void caller(int i) {
		String s = null;
		try {
			s = (new StackTrace()).location(i).caller();
		} catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
		}
		out.println("caller(" + i + ") => " + s);
	}

	public static void begin(String s) {
		out.print(s + " ... ");
		out.flush();
	}

	public static void ok() {
		out.println("ok.");
	}

	public static DebugCategory getCategory(String s) {
		DebugCategory debugcategory = (DebugCategory) categories.get(s);
		if (debugcategory != null)
			return debugcategory;
		debugcategory = new DebugCategory(s);
		categories.put(s, debugcategory);
		String s1 = null;
		try {
			if (System.getProperty(s) != null)
				debugcategory.setEnabled(true);
			s1 = System.getProperty("weblogic.Debug");
		} catch (Exception exception) {
		}
		if (s1 != null) {
			StringTokenizer stringtokenizer = new StringTokenizer(s1, ",");
			do {
				if (!stringtokenizer.hasMoreTokens())
					break;
				try {
					String s2 = stringtokenizer.nextToken().trim();
					int i = s2.indexOf("=");
					if (i != -1)
						s2 = s2.substring(0, i).trim();
					if (s2.equals(s) && !debugcategory.isEnabled())
						debugcategory.setEnabled(true);
				} catch (NoSuchElementException nosuchelementexception) {
				} catch (StringIndexOutOfBoundsException stringindexoutofboundsexception) {
				}
			} while (true);
		}
		return debugcategory;
	}

	public static void attributeChangeNotification(String s, Object obj,
			Object obj1) {
		DebugCategory debugcategory = getCategory(s);
		if (obj1 != null && (obj1 instanceof Boolean))
			debugcategory.setEnabled(((Boolean) obj1).booleanValue());
	}

	private static final boolean DEBUG = false;

	private static final Logger LOGGER = Logger
			.getLogger("weblogic.utils.Debug");

	private static final PrintStream out;

	public static boolean verboseMethods = false;

	public static boolean showLineNumbers = false;

	private static final Hashtable categories = new Hashtable();

	static {
		out = System.out;
		try {
			verboseMethods = Boolean.getBoolean("debug.methodNames");
			showLineNumbers = Boolean.getBoolean("debug.lineNumbers");
		} catch (SecurityException securityexception) {
		}
	}
}
