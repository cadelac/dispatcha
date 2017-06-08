package cadelac.framework.blade.handler2;

import cadelac.framework.blade.core.Framework;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.Activation;

public class ActivationBase<M extends Message> implements Activation<M> {

	public ActivationBase(final M message_) {
		_message = message_;
		_activationId = Framework.getShell().getRack().getMonitor().getNextJobId();
	}
	
	@Override
	public M getMessage() {
		return _message;
	}

	@Override
	public long getJobId() {
		return _activationId;
	}
	
	private final M _message;
	private final long _activationId;
}
