package cadelac.framework.blade.core.invocation.job;

import cadelac.framework.blade.handler.SendHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.job.Job;

public interface SendJob<M extends Message, S extends State> extends Job<M,S>, Runnable {
	public SendHandler<M,S> getHandler();
}
