package cadelac.framework.blade.core.message;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.dispatch.PullBlock;
import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.state.CanChooseStateId;
import cadelac.framework.blade.core.state.CanProvideState;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StatePolicy;

public interface Pullable {

	// default state policy
	<R,S extends State> 
	Future<Response<R>> futurePull(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R,S> pullBlock_) 
					throws Exception;

	// default state policy
	<R,S extends State> 
	R pull(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R,S> pullBlock_) 
					throws Exception;
	
	
	// explicit state policy
	<R,S extends State> 
	Future<Response<R>> futurePull(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R,S> pullBlock_) throws Exception;
	
	// explicit state policy
	<R,S extends State> 
	R pull(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, PullBlock<R,S> pullBlock_) throws Exception;
}
