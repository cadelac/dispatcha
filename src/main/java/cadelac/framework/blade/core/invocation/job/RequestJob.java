package cadelac.framework.blade.core.invocation.job;

import java.util.concurrent.Callable;

import cadelac.framework.blade.handler.RequestHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.job.Job;

public interface RequestJob<R, M extends Message, S extends State> extends Job<M,S>, Callable<Response<R>> {
	public RequestHandler<R,M,S> getHandler();
}
