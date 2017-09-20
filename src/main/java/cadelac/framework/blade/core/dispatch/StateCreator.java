package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;

@FunctionalInterface
public interface StateCreator<M extends Message, S extends State> {
	public S createState(final M message_) throws Exception;
}
