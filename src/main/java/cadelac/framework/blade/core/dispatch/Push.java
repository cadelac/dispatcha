package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateAware;

public interface Push<M extends Message,S extends State> 
		extends Identified, StateAware<M,S> {
	Routine<M,S> getRoutine();
	void setRoutine(Routine<M,S> routine);
	StateIdMapper<M> getStateIdMapper();
	StateCreator<M,S> getStateCreator();
}
