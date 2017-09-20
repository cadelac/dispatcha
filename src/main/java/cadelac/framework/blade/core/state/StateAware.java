package cadelac.framework.blade.core.state;

import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.dispatch.StateCreator;
import cadelac.framework.blade.core.dispatch.StateIdMapper;
import cadelac.framework.blade.core.message.Message;

public interface StateAware<M extends Message, S extends State> 
	extends StateIdMapper<M>, StateCreator<M,S>, Identified {
}
