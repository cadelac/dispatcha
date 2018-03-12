package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.state.CanChooseStateId;
import cadelac.framework.blade.core.state.CanProvideState;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateManager;
import cadelac.framework.blade.core.state.StatePolicy;

public class Patch {
	
	public <S extends State> Patch push(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
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
	

}
