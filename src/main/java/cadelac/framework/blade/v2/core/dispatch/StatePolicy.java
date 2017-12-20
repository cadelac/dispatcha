package cadelac.framework.blade.v2.core.dispatch;

import cadelac.framework.blade.core.exception.StateException;
import cadelac.framework.blade.core.state.State;

public interface StatePolicy {

	// behavior when state is not found
	<S extends State> 
	S stateNotFoundBehavior(
			CanProvideState<S> stateProvider_) 
					throws Exception;
	
	// behavior when state is found
	<S extends State> 
	S stateIsFoundBehavior(
			CanProvideState<S> stateProvider_) 
					throws Exception;
	
	
	StatePolicy ALWAYS_CREATE = new StatePolicy() {
		@Override
		public <S extends State> 
		S stateNotFoundBehavior(
				CanProvideState<S> stateProvider_) 
						throws Exception {
			return stateProvider_.getState();
		}
		@Override
		public <S extends State> 
		S stateIsFoundBehavior(
				CanProvideState<S> stateProvider_)
						throws Exception {
			throw new StateException("State found: must not already exist");
		}
	};
	
	StatePolicy MUST_PRE_EXIST = new StatePolicy() {
		@Override
		public <S extends State> 
		S stateNotFoundBehavior(
				CanProvideState<S> stateProvider_) 
						throws Exception {
			throw new StateException("State not found: must already exist");
		}
		@Override
		public <S extends State> 
		S stateIsFoundBehavior(
				CanProvideState<S> stateProvider_)
						throws Exception {
			return stateProvider_.getState();
		}
	};
	
	StatePolicy AUTO_CREATE = new StatePolicy() {
		@Override
		public <S extends State> 
		S stateNotFoundBehavior(
				CanProvideState<S> stateProvider_) 
						throws Exception {
			return stateProvider_.getState();
		}
		@Override
		public <S extends State> 
		S stateIsFoundBehavior(
				CanProvideState<S> stateProvider_) 
						throws Exception {
			return stateProvider_.getState();
		}
	};
	
	StatePolicy DEFAULT_STATE_POLICY = AUTO_CREATE;

}


