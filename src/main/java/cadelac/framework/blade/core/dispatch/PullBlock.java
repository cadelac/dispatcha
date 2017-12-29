package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.state.State;

@FunctionalInterface
public interface PullBlock<R,S extends State> {
	public R block(S state_) throws Exception;
}
