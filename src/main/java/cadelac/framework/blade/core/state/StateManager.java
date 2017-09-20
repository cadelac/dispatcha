package cadelac.framework.blade.core.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.exception.ArgumentException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.exception.StateException;
import cadelac.framework.blade.core.message.Message;

public class StateManager {
	
	static {
		_states = new ConcurrentHashMap<String,State>();
	}
	
	@SuppressWarnings("unchecked")
	public static <M extends Message, S extends State>
	S acquireState(final StateAware<M,S> handler_, final M message_, final String stateId_) 
			throws ArgumentException, InitializationException, StateException, Exception {
		if (handler_==null)
			throw new ArgumentException("handler must not be null");
		if (stateId_ == null)
			throw new ArgumentException("state id must not be null");
		if (message_==null)
			throw new ArgumentException("message must not be null");

		final S state = (S) _states.get(stateId_);
		if (state != null) {
			// found it, exit immediately
			return state;
		}

		if (stateId_==StateLess.STATELESS_STATE_ID) {
			// state is StateLess
			return installState((S) StateLess.STATELESS_STATE);
		}
		else {
			// state does not exist, create a new state
			final S createdState = (S) handler_.createState(message_);
			
			if (createdState == null)
				// state is not allowed to be null.
				throw new StateException(String.format("unable to create state [%s]", stateId_));
			return installState(createdState);
		}
	}

	@SuppressWarnings("unchecked")
	public static <S extends State> 
	S getState(final String stateId_) 
			throws ArgumentException, InitializationException {
		if (stateId_ == null)
			throw new ArgumentException("state id must not be null");
		if (_states == null)
			throw new InitializationException("State table is not initialized");
		return (S) _states.get(stateId_);
	}
	
	public static <S extends State>
	S installState(final S state_) 
			throws ArgumentException, InitializationException {
		if (state_ == null)
			throw new ArgumentException("state must not be null");
		if (_states == null)
			throw new InitializationException("State table is not initialized");
		_states.put(state_.getId(), state_);
		logger.debug(String.format("installed state [%s]", state_.getId()));
		return state_;
	}

	
	private static final Logger logger = Logger.getLogger(StateManager.class);
	
	private static Map<String,State> _states;
}
