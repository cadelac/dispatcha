package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateAware;

public interface Push<D extends Dispatchable,S extends State> 
		extends StateAware<D,S> {
	Routine<D,S> getRoutine();
	void setRoutine(Routine<D,S> routine);
	StateIdMapper<D> getStateIdMapper();
	StateCreator<D,S> getStateCreator();
}
