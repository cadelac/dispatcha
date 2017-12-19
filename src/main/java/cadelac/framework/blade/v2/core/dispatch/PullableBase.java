package cadelac.framework.blade.v2.core.dispatch;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateManager;

public class PullableBase implements Pullable {

	@Override
	public <R,S extends State> 
	Future<Response<R>> futurePull(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R, S> pullBlock_) 
					throws Exception {
		final S state = FastDispatch.realizeState(
				StateManager.DEFAULT_STATE_POLICY
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
		final S state = FastDispatch.realizeState(
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
