package cadelac.framework.blade.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FrameworkException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3904519906768435141L;

	public FrameworkException(final String text_)  {
		super(text_);
	}
	
	public String getStringStackTrace() {
		return FrameworkException.getStringStackTrace(this);
	}
	
	public static String getStringStackTrace(final FrameworkException except_) {
		return FrameworkException.getStringStackTrace((Exception)except_);
	}
	
	public static String getStringStackTrace(final Exception except_) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        except_.printStackTrace(pw);
        pw.flush();
        sw.flush();
	    return sw.toString();
	}
	
	public static String getThrowableStackTrace(final Throwable except_) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        except_.printStackTrace(pw);
        pw.flush();
        sw.flush();
	    return sw.toString();
	}
	
}
