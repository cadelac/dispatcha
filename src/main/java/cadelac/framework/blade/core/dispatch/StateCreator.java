package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.state.State;

@FunctionalInterface
public interface StateCreator<D extends Dispatchable, S extends State> {
	public S createState(final D dispatchable_) throws Exception;
}
