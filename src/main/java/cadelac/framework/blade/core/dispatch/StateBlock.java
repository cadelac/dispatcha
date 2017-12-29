package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.state.State;

@FunctionalInterface
public interface StateBlock<S extends State> {
	public void block(S state_) throws Exception;
}
