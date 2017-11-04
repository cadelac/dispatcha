package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.state.State;

@FunctionalInterface
public interface Calculation<R,D extends Dispatchable,S extends State> {
	public R calculate(D msg_, S state_) throws Exception;
}
