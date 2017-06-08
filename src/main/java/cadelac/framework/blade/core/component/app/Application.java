package cadelac.framework.blade.core.component.app;

import cadelac.framework.blade.core.component.Component;
import cadelac.lib.primitive.exception.FrameworkException;

/**
 * This interface is designed to be subclassed by applications.
 * @author cadelac
 *
 */
public interface Application extends Component {
	public void init() throws FrameworkException, Exception;
	public void registerClasses() throws Exception;
}
