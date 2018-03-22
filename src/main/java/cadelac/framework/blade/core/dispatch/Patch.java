package cadelac.framework.blade.core.dispatch;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.state.CanChooseStateId;
import cadelac.framework.blade.core.state.CanProvideState;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateManager;
import cadelac.framework.blade.core.state.StatePolicy;

public class Patch {
	
	/**
	 * State less Push
	 * @param stateLessBlock_
	 * @return
	 * @throws Exception
	 */
	public Patch push(
			StateLessBlock stateLessBlock_) 
					throws Exception {
		Dispatch.stateLessPush(stateLessBlock_);
		return this;
	}
	public Patch push(
			long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception {
		Dispatch.delayedStateLessPush(
				delay_
				, stateLessBlock_);
		return this;
	}
	public Patch push(
			long period_
			, long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception {
		Dispatch.periodicStateLessPush(
				period_
				, delay_
				, stateLessBlock_);
		return this;
	}

	
	
	/**
	 * State full (default) push
	 */
	public <S extends State> Patch push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		Dispatch.stateFullPush(
				StateManager.realizeState(
						StatePolicy.DEFAULT_STATE_POLICY
						, stateChooser_
						, stateProvider_)
				, stateBlock_);
		
		return this;
	}
	public <S extends State> Patch push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, long delay_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		Dispatch.delayedStateFullPush(
				StateManager.realizeState(
						StatePolicy.DEFAULT_STATE_POLICY
						, stateChooser_
						, stateProvider_)
				, delay_
				, stateBlock_);
		
		return this;
	}
	public <S extends State> Patch push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, long period_
			, long delay_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		Dispatch.periodicStateFullPush(
				StateManager.realizeState(
						StatePolicy.DEFAULT_STATE_POLICY
						, stateChooser_
						, stateProvider_)
				, period_
				, delay_
				, stateBlock_);
		
		return this;
	}
	
	
	
	/**
	 * State full (explicit) push
	 */
	public <S extends State> Patch push(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		Dispatch.stateFullPush(
				StateManager.realizeState(
						statePolicy_
						, stateChooser_
						, stateProvider_)
				, stateBlock_);
		
		return this;
	}
	public <S extends State> Patch push(
			long delay_
			, StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		Dispatch.delayedStateFullPush(
				StateManager.realizeState(
						statePolicy_
						, stateChooser_
						, stateProvider_)
				, delay_
				, stateBlock_);
		
		return this;
	}
	public <S extends State> Patch push(
			long period_
			, long delay_
			, StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		Dispatch.periodicStateFullPush(
				StateManager.realizeState(
						statePolicy_
						, stateChooser_
						, stateProvider_)
				, period_
				, delay_
				, stateBlock_);
		return this;
	}
	
	
	
	/**
	 * pull
	 * @param stateChooser_
	 * @param stateProvider_
	 * @param pullBlock_
	 * @return
	 * @throws Exception
	 */
	public <R,S extends State> Future<Response<R>> futurePull(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R, S> pullBlock_) 
					throws Exception {
		return Dispatch.pull(
				StateManager.realizeState(
						StatePolicy.DEFAULT_STATE_POLICY
						, stateChooser_
						, stateProvider_)
				, pullBlock_);
	}
	public <R,S extends State> R pull(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R,S> pullBlock_) 
					throws Exception {
		return Dispatch.extractResponse(
				futurePull(
						stateChooser_
						, stateProvider_
						, pullBlock_));
	}
	
	
	
	public <R,S extends State> Future<Response<R>> futurePull(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R, S> pullBlock_)
					throws Exception {
		return Dispatch.pull(
				StateManager.realizeState(
						statePolicy_
						, stateChooser_
						, stateProvider_)
				, pullBlock_);
	}
	public <R,S extends State> R pull(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R,S> pullBlock_) 
					throws Exception {
		return Dispatch.extractResponse(
				futurePull(
						statePolicy_
						, stateChooser_
						, stateProvider_
						, pullBlock_));
	}
	
	
	
	
}
