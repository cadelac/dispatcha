package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateAware;

public interface Pull<R,M extends Message,S extends State> 
	extends Identified, StateAware<M,S>
{
	Calculation<R,M,S> getCalculation();
	StateIdMapper<M> getStateIdMapper();
	StateCreator<M,S> getStateCreator();
}
