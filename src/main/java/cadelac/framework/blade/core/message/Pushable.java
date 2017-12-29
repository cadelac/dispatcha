package cadelac.framework.blade.core.message;

import cadelac.framework.blade.core.dispatch.StateBlock;
import cadelac.framework.blade.core.dispatch.StateLessBlock;
import cadelac.framework.blade.core.state.CanChooseStateId;
import cadelac.framework.blade.core.state.CanProvideState;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StatePolicy;

public interface Pushable {
	
	/**
	 * in-line state-less (has no state policy) push
	 * @param stateLessBlock_
	 * @return
	 * @throws Exception
	 */
	Pushable push(
			StateLessBlock stateLessBlock_) 
					throws Exception;
	
	/**
	 * in-line delayed state-less (has no state policy) push
	 * @param delay_
	 * @param stateLessBlock_
	 * @return
	 * @throws Exception
	 */
	Pushable push(
			long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception;
	
	/**
	 * in-line periodic state-less (has no state policy) push
	 * @param period_
	 * @param delay_
	 * @param stateLessBlock_
	 * @return
	 * @throws Exception
	 */
	Pushable push(
			long period_
			, long delay_
			, StateLessBlock stateLessBlock_) 
					throws Exception;

	
	/**
	 * in-line state-full (default state policy) push
	 * @param stateBlock_
	 * @return
	 * @throws Exception
	 */
	<S extends State> Pushable push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception;
	
	/**
	 * in-line delayed state-full (default state policy) push
	 * @param delay_
	 * @param stateBlock_
	 * @return
	 * @throws Exception
	 */
	<S extends State> Pushable push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, long delay_
			, StateBlock<S> stateBlock_) 
					throws Exception;
	
	/**
	 * in-line periodic state-full (default state policy) push
	 * @param period_
	 * @param delay_
	 * @param stateBlock_
	 * @return
	 * @throws Exception
	 */
	<S extends State> Pushable push(
			CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, long period_
			, long delay_
			, StateBlock<S> stateBlock_) 
					throws Exception;
	
	
	/**
	 * in-line state-full (explicit state policy) push
	 * @param statePolicy_
	 * @param stateChooser_
	 * @param stateProvider_
	 * @param stateBlock_
	 * @return
	 * @throws Exception
	 */
	<S extends State> Pushable push(
			StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception;

	/**
	 * in-line delayed state-full (explicit state policy) push
	 * @param delay_
	 * @param statePolicy_
	 * @param stateBlock_
	 * @return
	 * @throws Exception
	 */
	<S extends State> Pushable push(
			long delay_
			, StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception;
	
	/**
	 * in-line periodic state-full (explicit state policy) push
	 * @param period_
	 * @param delay_
	 * @param statePolicy_
	 * @param stateBlock_
	 * @return
	 * @throws Exception
	 */
	<S extends State> Pushable push(
			long period_
			, long delay_
			, StatePolicy statePolicy_
			, CanChooseStateId stateChooser_
			, CanProvideState<S> stateProvider_
			, StateBlock<S> stateBlock_) 
					throws Exception;	
}
