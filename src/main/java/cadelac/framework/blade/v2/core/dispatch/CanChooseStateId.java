package cadelac.framework.blade.v2.core.dispatch;

/**
 * Determines the StateId based on the given dispatchable.
 * @author cadelac
 * 
 */
@FunctionalInterface
public interface CanChooseStateId {
	public StateId getStateId();
}
