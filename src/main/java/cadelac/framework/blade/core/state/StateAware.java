package cadelac.framework.blade.core.state;

import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.dispatch.StateCreator;
import cadelac.framework.blade.core.dispatch.StateIdMapper;
import cadelac.framework.blade.core.message.Dispatchable;

public interface StateAware<M extends Dispatchable, S extends State> 
	extends StateIdMapper<M>, StateCreator<M,S>, Identified {
}
