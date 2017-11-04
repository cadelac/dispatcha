package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.state.State;

@FunctionalInterface
public interface Routine<D extends Dispatchable,S extends State> {
	public void routine(D dispatchable_, S state_) throws Exception;
}
