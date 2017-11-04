package cadelac.framework.blade.core.state.experimental;

/**
 * Determines the StateId based on the given dispatchable.
 * @author cadelac
 * 
 */
@FunctionalInterface
public interface CanChooseStateId {
	public StateId getStateId();
}
