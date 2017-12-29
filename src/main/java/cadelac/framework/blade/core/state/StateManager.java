package cadelac.framework.blade.core.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.exception.ArgumentException;
import cadelac.framework.blade.core.exception.InitializationException;

public class StateManager {

	@SuppressWarnings("unchecked")
	public static <S extends State> S getState(
			final StateId stateId_) 
					throws ArgumentException
					, InitializationException {
		checkArguments(stateId_);
		return (S) _states.get(stateId_.getId());
	}
	
	public static boolean isExists(
			final StateId stateId_) 
					throws ArgumentException
					, InitializationException {
		return getState(stateId_)!=null;
	}
	
	public static <S extends State> S installState(
			final S state_)
					throws ArgumentException, InitializationException {

		if (state_ == null)
			throw new ArgumentException("state must not be null");
		checkArguments(StateId.build(state_.getId()));
		
		_states.put(state_.getId(), state_);
		logger.debug(String.format("installed state [%s]", state_.getId()));
		return state_;
	}

	public static <S extends State> S realizeState(
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

	private static void checkArguments(final StateId stateId_) 
			throws ArgumentException
			, InitializationException {
		if (stateId_ == null)
			throw new ArgumentException("state id must not be null");
		if (_states == null)
			throw new InitializationException("State table is not initialized");
	}
	
	
	
	private static final Logger logger = Logger.getLogger(StateManager.class);
	
	private static final Map<String,State> _states = new ConcurrentHashMap<String,State>();
}
