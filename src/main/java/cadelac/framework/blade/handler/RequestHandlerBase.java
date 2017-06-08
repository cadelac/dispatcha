package cadelac.framework.blade.handler;

import org.apache.log4j.Logger;

import cadelac.framework.blade.plug.RequestInputPlug;
import cadelac.framework.blade.plug.RequestInputPlugBase;
import cadelac.framework.blade.plug.RequestOutputPlug;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.invocation.ResponseSimple;

public abstract class RequestHandlerBase<R, M extends Message, S extends State> 
	extends HandlerBase<M,S> implements RequestHandler<R,M,S>{

	public RequestHandlerBase(final String id_) {
		super(id_);
		setInputPlug(new RequestInputPlugBase<R,M,S>("inputPlug", this));
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public RequestInputPlug<R, M, S> getInputPlug() {
		return (RequestInputPlugBase<R,M,S>) super.getInputPlug();
	}
	
	@Override
	public Response<R> invoke(M message_, S state_) throws Exception {
		logger.debug("RequestHandler.invoke: message [" + message_.getClass().getSimpleName() + 
			"], state [" + state_.getClass().getSimpleName() + "]");
		if (state_.getId() == StateLess.STATELESS_STATE_ID) {
			// handler is state-less, no need to synchronize access to state
			return process(message_, state_);
		}
		else {
			// handler is not state-less, synchronize access to state
			synchronized (state_) {
				return process(message_, state_);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <N extends Message> RequestOutputPlug<?,N,?> getRequestOutputPlug(final String outputPlugName_) throws ArgumentException {
		return (RequestOutputPlug<?,N,?>) super.getOutputPlug(outputPlugName_);
	}
	
	public <N extends Message> void addRequestOutputPlug(final RequestOutputPlug<?,N,?> outputPlug_) throws ArgumentException {
		super.addOutputPlug(outputPlug_.getId(), outputPlug_);
	}
	
	public <N extends Message> void addRequestOutputPlug(final String outputPlugName_, final RequestOutputPlug<?,N,?> outputPlug_)
			throws ArgumentException {
		super.addOutputPlug(outputPlugName_, outputPlug_);	
	}
	
	protected Response<R> wrapResponse(final R response_) {
		final Response<R> wrapper = new ResponseSimple<R>();
		wrapper.setResponse(response_);
		return wrapper;
	}
	
	private static final Logger logger = Logger.getLogger(RequestHandler.class);
}
