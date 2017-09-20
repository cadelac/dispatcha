package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;

@FunctionalInterface
public interface Calculation<R,M extends Message,S extends State> {
	public R calculate(M msg_, S state_) throws Exception;
}
