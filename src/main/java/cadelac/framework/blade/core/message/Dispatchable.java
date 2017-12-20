package cadelac.framework.blade.core.message;

import cadelac.framework.blade.core.dispatch.Calculation;
import cadelac.framework.blade.core.dispatch.Dispatch;
import cadelac.framework.blade.core.dispatch.PullBase;
import cadelac.framework.blade.core.dispatch.PushBase;
import cadelac.framework.blade.core.dispatch.PushStateLessBase;
import cadelac.framework.blade.core.dispatch.Routine;
import cadelac.framework.blade.core.dispatch.RoutineStateLess;
import cadelac.framework.blade.core.dispatch.StateCreator;
import cadelac.framework.blade.core.dispatch.StateIdMapper;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateAware;
import cadelac.framework.blade.core.state.StateLess;
import cadelac.framework.blade.v2.core.dispatch.CanChooseStateId;
import cadelac.framework.blade.v2.core.dispatch.StateId;

/**
 * Indicates that it can be dispatched.
 * @author cadelac
 *
 */
@Deprecated
public interface Dispatchable {
	
	// push using message-type as discriminator...
	
	/**
	 * Delivers message immediately. Uses the message's type as discriminator.
	 * @return
	 * @throws Exception
	 */
//	default Dispatchable push() throws Exception {
//		Dispatch.push(this);
//		return this;
//	}
	
	/**
	 * Delivers message after a delay. Uses the message's type as discriminator.
	 * @return
	 * @throws Exception
	 */
//	default Dispatchable push(long delay_) throws Exception {
//		Dispatch.push(this, delay_);
//		return this;
//	}
	
	/**
	 * Delivers message repeatedly. Uses the message's type as discriminator.
	 * @return
	 * @throws Exception
	 */
//	default Dispatchable push(final long period_, long delay_) 
//			throws Exception {
//		Dispatch.push(this, delay_, period_);
//		return this;
//	}
	
	// push using hash id as discriminator...
	
	/**
	 * Delivers message immediately. Uses the hash id as discriminator.
	 * @return
	 * @throws Exception
	 */
//	default Dispatchable push(String hashId_) throws Exception {
//		Dispatch.push(hashId_, this);
//		return this;
//	}
	
	/**
	 * Delivers message after a delay. Uses the hash id as discriminator.
	 * @return
	 * @throws Exception
	 */
//	default Dispatchable push(long delay_, String hashId_) 
//			throws Exception {
//		Dispatch.push(hashId_, this, delay_);
//		return this;
//	}
	
	/**
	 * Delivers message repeatedly. Uses the hash id as discriminator.
	 * @return
	 * @throws Exception
	 */
//	default Dispatchable push(final long period_, long delay_, String hashId_)
//			throws Exception {
//		Dispatch.push(hashId_, this, delay_, period_);
//		return this;
//	}

	
	/**
	 * Inline push: accepts StateAware
	 * @param stateAware_
	 * @return
	 * @throws Exception
	 */
//	@SuppressWarnings("unchecked")
//	default <D extends Dispatchable, S extends State> 
//	D push(final StateAware<D,S> stateAware_) 
//			throws Exception {
//		Dispatch.inlinePush(stateAware_, (D)this);
//		return (D) this;
//	}

	/**
	 * Inline push: accepts pushbase components
	 * @param routine_
	 * @param stateIdMapper_
	 * @param stateCreator_
	 * @return
	 * @throws Exception
	 */
//	@SuppressWarnings("unchecked")
//	default <D extends Dispatchable, S extends State> 
//	D push(
//			final Routine<D,S> routine_
//			, final StateIdMapper<D> stateIdMapper_
//			, final StateCreator<D,S> stateCreator_) 
//					throws Exception {
//		final StateAware<D,S> stateAware_ = 
//				new PushBase<D,S>(
//						"inline push base"
//						, routine_
//						, stateIdMapper_
//						, stateCreator_);
//		Dispatch.inlinePush(stateAware_, (D)this);
//		return (D) this;
//	}
	
	/**
	 * Inline StateLess push: 
	 */
//	@SuppressWarnings("unchecked")
//	default <D extends Dispatchable> 
//	D push(
//			final RoutineStateLess<D> routineStateLess_) 
//					throws Exception {
//		final StateAware<D,StateLess> stateAware_ = 
//				new PushStateLessBase<D>(
//						"inline stateless push base"
//						, routineStateLess_);
//		Dispatch.inlinePush(stateAware_, (D)this);
//		return (D) this;
//	}

	
	
	// pull using message-type as discriminator...
	
	/**
	 * Delivers pull message immediately. Uses the message's type as discriminator.
	 * 
	 * @return message response from handler
	 * @throws Exception
	 */
//	default <R> R pull() throws Exception {
//		return Dispatch.extractResponse(
//				Dispatch.pull(this));
//	}
	
	// TODO: pull using hash-id as discriminator
	
	/**
	 * Delivers inline pull-message immediately
	 * 
	 * @return message response from handler
	 * @throws Exception
	 */
//	@SuppressWarnings("unchecked")
//	default <R, D extends Dispatchable, S extends State> 
//	R pull(
//			final Calculation<R,D,S> calculation_
//			, final StateIdMapper<D> stateIdMapper_
//			, final StateCreator<D,S> stateCreator_) 
//					throws Exception {
//		final StateAware<D,S> stateAware_ = 
//				new PullBase<R,D,S>(
//						"inline pull base"
//						, calculation_
//						, stateIdMapper_
//						, stateCreator_);
//
//		return Dispatch.extractResponse(
//				Dispatch.inlinePull(stateAware_, (D)this));
//	}


	
//	default StateId stateId(CanChooseStateId canChooseStateId_) {
//		return canChooseStateId_.getStateId();
//	}

}
