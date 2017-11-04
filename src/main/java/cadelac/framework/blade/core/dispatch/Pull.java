package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateAware;

public interface Pull<R,D extends Dispatchable,S extends State> 
	extends Identified, StateAware<D,S>
{
	Calculation<R,D,S> getCalculation();
	StateIdMapper<D> getStateIdMapper();
	StateCreator<D,S> getStateCreator();
}
