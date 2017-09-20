package cadelac.framework.blade.core.exception;

/**
 * This exception is thrown to indicate errors in logic.
 * @author cadelac
 *
 */
public class LogicException extends FrameworkException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2981767878225050383L;

	public LogicException(final String text_)  {
		super(text_);
	}
}
