package cadelac.framework.blade.core.message;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.dispatch.Dispatch;
import cadelac.framework.blade.core.dispatch.PullBlock;
import cadelac.framework.blade.core.dispatch.StateBlock;
import cadelac.framework.blade.core.dispatch.StateLessBlock;
import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.state.CanChooseStateId;
import cadelac.framework.blade.core.state.CanProvideState;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateManager;
import cadelac.framework.blade.core.state.StatePolicy;

public class DeliverableBase 
		implements Deliverable {

	@Override
	public Pushable push(
			StateLessBlock stateLessBlock_) 
					throws Exception {
		Dispatch.stateLessPush(stateLessBlock_);
		return this;
	}

	@Override
	public Pushable push(
			long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception {
		Dispatch.delayedStateLessPush(
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
		Dispatch.periodicStateLessPush(
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

		Dispatch.stateFullPush(state, stateBlock_);
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

		Dispatch.delayedStateFullPush(state, delay_, stateBlock_);
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

		Dispatch.periodicStateFullPush(state, period_, delay_, stateBlock_);
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

		Dispatch.stateFullPush(state, stateBlock_);
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

		Dispatch.delayedStateFullPush(state, delay_, stateBlock_);
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

		Dispatch.periodicStateFullPush(state, period_, delay_, stateBlock_);
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
		return Dispatch.pull(state, pullBlock_);
	}

	@Override
	public <R, S extends State> 
	R pull(
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
		return Dispatch.pull(state, pullBlock_);
	}
	
	@Override
	public <R, S extends State> 
	R pull(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R, S> pullBlock_) 
					throws Exception {
		return Dispatch.extractResponse(
				futurePull(
						statePolicy_
						, stateChooser_
						, stateProvider_
						, pullBlock_));
	}

}
