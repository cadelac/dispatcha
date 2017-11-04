package cadelac.framework.blade.core.state.experimental;

import cadelac.framework.blade.core.dispatch.Dispatch;
import cadelac.framework.blade.core.state.State;

public class InvocationCapture<S extends State> {

	public InvocationCapture(S state_) {
		_state = state_;
	}

	public void push(StateBlock<S> stateBlock_) throws Exception {
		Dispatch.pushInline(_state, stateBlock_);
	}
	
	public void push(long delay_, StateBlock<S> stateBlock_) throws Exception {
		Dispatch.pushInline(delay_, _state, stateBlock_);
	}
	
	S _state;
}
