package cadelac.framework.blade.core.state;

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
