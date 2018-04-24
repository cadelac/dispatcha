package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Message;

@FunctionalInterface
public interface MessageBlock<M extends Message> {
	public void block(
			String channel_
			, M message_) 
					throws Exception;
}
