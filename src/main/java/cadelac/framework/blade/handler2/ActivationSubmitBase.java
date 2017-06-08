package cadelac.framework.blade.handler2;

import org.apache.log4j.Logger;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.concept.state.StateManager;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.handler.ActivationSubmit;
import cadelac.lib.primitive.handler.AgentSubmit;

public class ActivationSubmitBase<M extends Message,S extends State> 
	extends ActivationBase<M> implements ActivationSubmit<M,S> {

	public ActivationSubmitBase(final AgentSubmit<M,S> agent_, final M message_) {
		super(message_);
		_agent = agent_;
	}
	
	@Override
	public void run() {
		try {
			final String stateId = _agent.getStateId(getMessage());
			final S state = StateManager.acquireState(_agent, getMessage(), stateId);
			if (state.getId() == StateLess.STATELESS_STATE_ID) {
				// handler is state-less, no need to synchronize access to state
				_agent.perform(getMessage(), state);
			}
			else {
				// handler is not state-less, synchronize access to state
				synchronized (state) {
					_agent.perform(getMessage(), state);
				}
			}
		}
		catch (Exception e_) {
			logger.error("exception on routine with job id  [" + getJobId() + "]: " + 
					e_.getMessage() + "\nStacktrace:\n" + FrameworkException.getStringStackTrace(e_));
		}
	}
	
	private static final Logger logger = Logger.getLogger(ActivationSubmitBase.class);
	
	private final AgentSubmit<M,S> _agent;
}
