package cadelac.framework.blade.v2.core.dispatch;

import cadelac.framework.blade.core.state.State;

/**
 * Can acquire/create State.
 * @author cadelac
 *
 * @param <D>
 */
@FunctionalInterface
public interface CanProvideState<S extends State> {
	public S getState() throws Exception;
}
