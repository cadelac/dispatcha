package cadelac.framework.blade.v2.core.dispatch;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.state.State;

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
