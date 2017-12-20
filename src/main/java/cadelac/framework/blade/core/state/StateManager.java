package cadelac.framework.blade.core.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.exception.ArgumentException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.exception.StateException;
import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.v2.core.dispatch.CanChooseStateId;
import cadelac.framework.blade.v2.core.dispatch.CanProvideState;
import cadelac.framework.blade.v2.core.dispatch.StateId;
import cadelac.framework.blade.v2.core.dispatch.StatePolicy;

public class StateManager {

	@SuppressWarnings("unchecked")
	public static <D extends Dispatchable, S extends State>
	S acquireState(
			final StateAware<D,S> handler_
			, final D dispatchable_
			, final String stateId_) 
					throws ArgumentException
					, InitializationException
					, StateException
					, Exception {
		
		verifyArguments(handler_, dispatchable_, stateId_);
		
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
			final S createdState = (S) handler_.createState(dispatchable_);
			
			if (createdState == null)
				// state is not allowed to be null.
				throw new StateException(String.format("unable to create state [%s]", stateId_));
			return installState(createdState);
		}
	}

	@SuppressWarnings("unchecked")
	public static <S extends State> S getState(
			final StateId stateId_) 
					throws ArgumentException
					, InitializationException {
		if (stateId_ == null)
			throw new ArgumentException("state id must not be null");
		if (_states == null)
			throw new InitializationException("State table is not initialized");
		return (S) _states.get(stateId_.getId());
	}
	
	public static boolean isExists(final StateId stateId_) 
			throws ArgumentException, InitializationException {
		if (stateId_ == null)
			throw new ArgumentException("state id must not be null");
		if (_states == null)
			throw new InitializationException("State table is not initialized");
		return _states.containsKey(stateId_.getId());
	}
	
	public static <S extends State> S installState(final S state_) 
			//throws ArgumentException, InitializationException 
	{
		/*
		if (state_ == null)
			throw new ArgumentException("state must not be null");
		if (_states == null)
			throw new InitializationException("State table is not initialized");
			*/
		_states.put(state_.getId(), state_);
		logger.debug(String.format("installed state [%s]", state_.getId()));
		return state_;
	}

	public static <S extends State> 
	S realizeState(
			StatePolicy policy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_) 
					throws Exception {
		
		final StateId stateId = stateChooser_.getStateId();
		final S lookedUpState = getState(stateId);
		
		if (lookedUpState == null) { // not found
			final S providedState = policy_.stateNotFoundBehavior(stateProvider_);
			installState(providedState);
			return providedState;
		}
		
		return policy_.stateIsFoundBehavior(() -> { 
			return lookedUpState; 
		});
	}
	
	private static <D extends Dispatchable, S extends State>
	void verifyArguments(
			final StateAware<D,S> handler_
			, final D dispatchable_
			, final String stateId_) throws ArgumentException {
		if (handler_==null)
			throw new ArgumentException("handler must not be null");
		if (stateId_ == null)
			throw new ArgumentException("state id must not be null");
		if (dispatchable_==null)
			throw new ArgumentException("dispatchable must not be null");
	}
	
	private static final Logger logger = Logger.getLogger(StateManager.class);
	
	private static final Map<String,State> _states = new ConcurrentHashMap<String,State>();
}
