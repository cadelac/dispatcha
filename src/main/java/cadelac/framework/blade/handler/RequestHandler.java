package cadelac.framework.blade.handler;

import cadelac.framework.blade.plug.RequestInputPlug;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.invocation.Response;

public interface RequestHandler<R, M extends Message, S extends State> extends Handler<M,S> {
	public RequestInputPlug<R,M,S> getInputPlug();
	public Response<R> invoke(final M message_, final S state_) throws Exception;
	public Response<R> process(final M message_, final S state_) throws Exception;
}
