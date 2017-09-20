package cadelac.framework.blade.app;

import cadelac.framework.blade.core.exception.FrameworkException;

/**
 * This interface is designed to be subclassed by applications.
 * @author cadelac
 *
 */
public interface Application {
	public void init() throws FrameworkException, Exception;
	public void start() throws FrameworkException, Exception;
}
