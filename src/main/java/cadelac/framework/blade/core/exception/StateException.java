package cadelac.framework.blade.core.exception;

/**
 * This exception is thrown to indicate errors related to creation of states.
 * @author cadelac
 *
 */
public class StateException extends FrameworkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5890409118607749996L;

	public StateException(final String text_)  {
		super(text_);
	}
}
