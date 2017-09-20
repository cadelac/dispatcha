package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Message;

@FunctionalInterface
public interface StateIdMapper<M extends Message> {
	public String getStateId(final M message);
}
