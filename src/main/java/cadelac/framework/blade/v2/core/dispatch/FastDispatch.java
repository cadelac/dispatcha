package cadelac.framework.blade.v2.core.dispatch;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.dispatch.Execute;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.SystemException;
import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.invocation.ResponseBase;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateManager;

public class FastDispatch {

	/**
	 * State less Push
	 * @param pushable_
	 * @param stateLessBlock_
	 * @throws Exception
	 */
	public static void stateLessPush(StateLessBlock stateLessBlock_) 
			throws Exception {
		Execute.immediateExecution(
				() -> {
					try {
						// state less; does not require synchronization
						stateLessBlock_.block();
					} 
					catch (Exception e) {
						logger.warn(String.format(
								"Exception on immediate state less push\n:%s"
								, FrameworkException.getStringStackTrace(e)));
					} 
				});
	}
	
	/**
	 * Delayed state less push
	 * @param pushable_
	 * @param stateLessBlock_
	 * @throws Exception
	 */
	public static void delayedStateLessPush(
			long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception {
		Execute.delayedExecution(
				() -> {
					try {
						// state less; does not require synchronization
						stateLessBlock_.block();
					} 
					catch (Exception e) {
						logger.warn(String.format(
								"Exception on delayed state less push\n:%s"
								, FrameworkException.getStringStackTrace(e)));
					} 
				}
				, delay_);
	}
	
	/**
	 * Periodic state less push
	 * @param pushable_
	 * @param stateLessBlock_
	 * @throws Exception
	 */
	public static void periodicStateLessPush(
			long period_
			, long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception {
		Execute.repeatedExecution(
				() -> {
					try {
						// state less; does not require synchronization
						stateLessBlock_.block();
					} 
					catch (Exception e) {
						logger.warn(String.format(
								"Exception on periodic state less push\n:%s"
								, FrameworkException.getStringStackTrace(e)));
					} 
				}
				, delay_
				, period_);
	}
	
	
	
	/**
	 * State full push
	 */
	public static <S extends State> 
	void stateFullPush(
			S state_
			, StateBlock<S> stateBlock_) 
					throws Exception {
		Execute.immediateExecution(
				() -> {
					try {
						synchronized(state_) {
							stateBlock_.block(state_);
						}
					} 
					catch (Exception e) {
						logger.warn(String.format(
								"Exception on immediate state full push\n:%s"
								, FrameworkException.getStringStackTrace(e)));
					} 
				});
	}
	
	/**
	 * Delayed state full push
	 */
	public static <S extends State> 
	void delayedStateFullPush(
			S state_
			, long delay_
			, StateBlock<S> stateBlock_) 
					throws Exception {
		Execute.delayedExecution(
				() -> {
					try {
						synchronized(state_) {
							stateBlock_.block(state_);
						}
					} 
					catch (Exception e) {
						logger.warn(String.format(
								"Exception on delayed state full push\n:%s"
								, FrameworkException.getStringStackTrace(e)));
					} 
				}
				, delay_);
	}
	
	/**
	 * Delayed state full push
	 */
	public static <S extends State> 
	void periodicStateFullPush(
			S state_
			, long period_
			, long delay_
			, StateBlock<S> stateBlock_) 
					throws Exception {
		Execute.repeatedExecution(
				() -> {
					try {
						synchronized(state_) {
							stateBlock_.block(state_);
						}
					} 
					catch (Exception e) {
						logger.warn(String.format(
								"Exception on periodic state full push\n:%s"
								, FrameworkException.getStringStackTrace(e)));
					} 
				}
				, delay_
				, period_);
	}	
	
	
	public static <R,S extends State>
	Future<Response<R>> pull(
			S state_
			, PullBlock<R,S> pullBlock_) 
					throws Exception {
		return Execute.executePull(
				() -> new ResponseBase<R>(pullBlock_.block(state_)));
	}

	
	public static <S extends State> 
	S realizeState(
			StatePolicy policy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_) 
					throws Exception {
		
		final StateId stateId = stateChooser_.getStateId();
		final S lookedUpState = StateManager.getState(stateId);
		
		if (lookedUpState == null) { // not found
			final S providedState = policy_.stateNotFoundBehavior(stateProvider_);
			StateManager.installState(providedState);
			return providedState;
		}
		
		return policy_.stateIsFoundBehavior(() -> { 
			return lookedUpState; 
		});
	}
	

	public static <R>
	R extractResponse(final Future<Response<R>> future) 
			throws Exception {
		final Response<R> futureResponse = future.get();
		if (futureResponse.getException() != null) {
			final String diagnostic = "exception encountered on reaping future...";
			logger.warn(diagnostic);
			throw (SystemException) new SystemException(diagnostic)
				.initCause(futureResponse.getException());
		}
		return futureResponse.getResponse();
	}

	
	private static final Logger logger = Logger.getLogger(FastDispatch.class);
}
