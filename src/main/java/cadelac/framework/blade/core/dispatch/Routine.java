package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;

@FunctionalInterface
public interface Routine<M extends Message,S extends State> {
	public void routine(M msg_, S state_) throws Exception;
}
