package cadelac.framework.blade.core.state.experimental;

import cadelac.framework.blade.core.state.State;

/**
 * Creates the State.
 * @author cadelac
 *
 * @param <D>
 */
@FunctionalInterface
public interface CanCreateState<S extends State> {
	public S createState() throws Exception;
}
