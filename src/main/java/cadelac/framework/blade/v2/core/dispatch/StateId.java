package cadelac.framework.blade.v2.core.dispatch;

import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateManager;
import cadelac.framework.blade.core.state.experimental.InvocationCapture;

public class StateId extends IdentifiedBase {

	public static StateId build(final String stateId_) {
		return new StateId(stateId_);
		
	}
	public StateId(final String stateId_) {
		super(stateId_);
	}
	
	public <S extends State>
	InvocationCapture<S> getState(final CanProvideState<S> canCreateState_) 
			throws Exception {
		final S state = StateManager.getState(this);
		if ( state != null)
			return new InvocationCapture<S>(state);
		
		
		// state not found (it is new), create a new state
		return new InvocationCapture<S>(
				StateManager.installState(
						canCreateState_.getState()));
	}
}