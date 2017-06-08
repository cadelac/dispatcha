package cadelac.framework.blade.handler2;

import org.apache.log4j.Logger;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.concept.state.StateManager;
import cadelac.lib.primitive.handler.ActivationReply;
import cadelac.lib.primitive.handler.AgentReply;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.invocation.ResponseSimple;

public class ActivationReplyBase<R,M extends Message,S extends State> 
	extends ActivationBase<M> implements ActivationReply<R,M,S> {
	
	public ActivationReplyBase(final AgentReply<R,M,S> agent_, final M message_) {
		super(message_);
		_agent = agent_;
	}

	@Override
	public Response<R> call()  {
		try {
			final String stateId = _agent.getStateId(getMessage());
			
			final S state = StateManager.acquireState(_agent, getMessage(), stateId);
			
			if (state.getId() == StateLess.STATELESS_STATE_ID) {
				// handler is state-less, no need to synchronize access to state
				return wrapResponse(_agent.respond(this.getMessage(), state));
			}
			else {
				// handler is not state-less, synchronize access to state
				synchronized (state) {
					return wrapResponse(_agent.respond(this.getMessage(), state));
				}
			}			
		}
		catch (Exception e) {
			logger.warn(String.format("agent threw exception: caught, wrapping exception for reaping"));
			return wrapException(e);
		}

	}

	private Response<R> wrapResponse(final R response_) {
		final Response<R> wrapper = new ResponseSimple<R>();
		wrapper.setResponse(response_);
		return wrapper;		
	}
	private Response<R> wrapException(final Exception e_) {
		final Response<R> wrapper = new ResponseSimple<R>();
		wrapper.setResponse(null);
		wrapper.setException(e_);
		return wrapper;		
	}
	
	private static final Logger logger = Logger.getLogger(ActivationReplyBase.class);
			
	private final AgentReply<R,M,S> _agent;
}
