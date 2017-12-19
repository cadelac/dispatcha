package cadelac.framework.blade.v2.core.dispatch;

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
}


