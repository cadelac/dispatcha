package cadelac.framework.blade.v2.core.dispatch;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateManager;

public class DeliverableBase 
		implements Deliverable {

	@Override
	public Pushable push(
			StateLessBlock stateLessBlock_) 
					throws Exception {
		FastDispatch.stateLessPush(stateLessBlock_);
		return this;
	}

	@Override
	public Pushable push(
			long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception {
		FastDispatch.delayedStateLessPush(
				delay_
				, stateLessBlock_);
		return this;
	}

	@Override
	public Pushable push(
			long period_
			, long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception {
		FastDispatch.periodicStateLessPush(
				period_
				, delay_
				, stateLessBlock_);
		return this;
	}

	
	
	@Override
	public <S extends State> 
	Pushable push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		final S state = StateManager.realizeState(
				StatePolicy.DEFAULT_STATE_POLICY
				, stateChooser_
				, stateProvider_);

		FastDispatch.stateFullPush(state, stateBlock_);
		return this;
	}
	
	@Override
	public <S extends State> 
	Pushable push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, long delay_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		final S state = StateManager.realizeState(
				StatePolicy.DEFAULT_STATE_POLICY
				, stateChooser_
				, stateProvider_);

		FastDispatch.delayedStateFullPush(state, delay_, stateBlock_);
		return this;
	}

	@Override
	public <S extends State> 
	Pushable push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, long period_
			, long delay_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		final S state = StateManager.realizeState(
				StatePolicy.DEFAULT_STATE_POLICY
				, stateChooser_
				, stateProvider_);

		FastDispatch.periodicStateFullPush(state, period_, delay_, stateBlock_);
		return this;
	}
	
	
	
	@Override
	public <S extends State> 
	Pushable push(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		final S state = StateManager.realizeState(
				statePolicy_
				, stateChooser_
				, stateProvider_);

		FastDispatch.stateFullPush(state, stateBlock_);
		return this;
	}

	@Override
	public <S extends State> 
	Pushable push(
			long delay_
			, StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_)
					throws Exception {

		final S state = StateManager.realizeState(
				statePolicy_
				, stateChooser_
				, stateProvider_);

		FastDispatch.delayedStateFullPush(state, delay_, stateBlock_);
		return this;
	}

	@Override
	public <S extends State> 
	Pushable push(
			long period_
			, long delay_
			, StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception {

		final S state = StateManager.realizeState(
				statePolicy_
				, stateChooser_
				, stateProvider_);

		FastDispatch.periodicStateFullPush(state, period_, delay_, stateBlock_);
		return this;
	}	



	@Override
	public <R,S extends State> 
	Future<Response<R>> futurePull(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R, S> pullBlock_) 
					throws Exception {
		final S state = StateManager.realizeState(
				StatePolicy.DEFAULT_STATE_POLICY
				, stateChooser_
				, stateProvider_);
		return FastDispatch.pull(state, pullBlock_);
	}

	@Override
	public <R, S extends State> 
	R pull(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R,S> pullBlock_) 
					throws Exception {
		return FastDispatch.extractResponse(
				futurePull(
						stateChooser_
						, stateProvider_
						, pullBlock_));
	}

	
	@Override
	public <R,S extends State> 
	Future<Response<R>> futurePull(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R, S> pullBlock_)
					throws Exception {
		final S state = StateManager.realizeState(
				statePolicy_
				, stateChooser_
				, stateProvider_);
		return FastDispatch.pull(state, pullBlock_);
	}
	
	@Override
	public <R, S extends State> 
	R pull(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R, S> pullBlock_) 
					throws Exception {
		return FastDispatch.extractResponse(
				futurePull(
						statePolicy_
						, stateChooser_
						, stateProvider_
						, pullBlock_));
	}

}
